# VignaAI 对话框架完整分析

## 一、框架概述

VignaAI 是一个基于 Spring Boot 开发的 AI 对话框架，采用策略模式、责任链模式和工厂模式等设计模式，提供了完整的 AI 对话管理能力。

### 核心特性
- **过滤器链机制**：通过责任链模式实现请求前/后的统一处理
- **上下文自动压缩**：当对话轮数或 token 数达到阈值时自动触发总结
- **完整的消息链路追踪**：所有请求和响应都记录到 Neo4j 图数据库
- **流式响应支持**：支持 SSE 协议的流式输出
- **详细的日志记录**：包含 AI 全量收发日志（API Key 脱敏）

---

## 二、整体架构

### 三层架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                    Controller 层                              │
│              (AiChatController - 用户入口)                     │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    Service 层                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │UserWithVigna │  │ VignaAiChat  │  │VignaSession  │       │
│  │    Chat      │  │    Impl      │  │    Service   │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  VignaHttpClient 层                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              VignaHttpClientImpl                      │   │
│  │  • sendAiMsg() - 普通 JSON 请求                        │   │
│  │  • sendAiMsgByStream() - 流式 SSE 请求                 │   │
│  │  • beforeAdvise() - 前导过滤器链                       │   │
│  │  • afterAdvise() - 后置过滤器链                        │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  过滤器链 (MyBaseAdvisor)                     │
│  ┌────────────────┐ ┌────────────────┐ ┌────────────────┐   │
│  │VignaHistory    │ │VignaChatZip    │ │VignaChat       │   │
│  │Advisor         │ │Advisor         │ │InscriberAdvisor│   │
│  │(历史记录填充)   │ │(自动压缩)       │ │(数据持久化)     │   │
│  │index:MIN_VALUE │ │index:10        │ │index:MAX_VALUE │   │
│  └────────────────┘ └────────────────┘ └────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  数据存储层                                   │
│  ┌──────────────┐              ┌──────────────┐             │
│  │ ChatContextMap│              │  Neo4j DB    │             │
│  │(内存上下文)   │              │(图数据库)     │             │
│  └──────────────┘              └──────────────┘             │
└─────────────────────────────────────────────────────────────┘
```

### 核心组件说明

| 组件 | 职责 |
|------|------|
| **ChatContextMap** | 内存中的请求上下文仓库，使用 ConcurrentHashMap 存储 |
| **VignaMsgContext** | 单次请求的完整上下文，包含用户消息、AI 回复、历史记录等 |
| **VignaHttpClient** | HTTP 请求发送器，支持普通请求和流式请求 |
| **MyBaseAdvisor** | 过滤器接口，定义 beforeAdvise 和 afterAdvise 方法 |
| **VignaSessionService** | 会话管理服务，负责会话锁和权限检查 |
| **AiChatMsgService** | 消息持久化服务，操作 Neo4j 图数据库 |

---

## 三、对话管线流程

### 完整请求处理流程（10 步）

```
1. 接收请求
   ↓
2. 必要信息检查 (checkRequiredInfo)
   - 验证密钥服务已注册
   - 验证参数不为空
   - 验证 SessionId 存在
   - 验证上下文存在
   - 验证 API Key 可获取
   ↓
3. 执行前导过滤器链 (beforeAdvise)
   - VignaHistoryAdvisor: 加载历史记录
   - VignaChatZipAdvisor: 检查并触发自动压缩
   - VignaChatInscriberAdvisor: 记录用户消息到 Neo4j
   ↓
4. 获取最新上下文 (从 ChatContextMap)
   ↓
5. 组装 AI 请求 (buildRequest)
   - 整理系统消息、用户消息、历史消息
   - 设置模型参数 (temperature, top_p 等)
   - 合并额外 JSON 配置
   ↓
6. 发送 HTTP 请求 (sendRequest / sendStreamRequest)
   - 获取 API Key
   - 更新上下文 (尝试次数、发送时间、超时时间)
   - 执行 HTTP 请求 (带重试机制)
   - 记录全量收发日志
   ↓
7. 解析响应
   - 普通请求：解析完整 JSON 响应
   - 流式请求：逐块解析 SSE 数据
   ↓
8. 写回上下文 (ChatContextMap.setContext)
   - 设置 AI 回复内容
   - 设置完成原因/原始响应
   ↓
9. 执行后置过滤器链 (afterAdvise)
   - VignaChatZipAdvisor: 计算 AI 回复下标
   - VignaChatInscriberAdvisor: 记录 AI 回复到 Neo4j
   ↓
10. 清理上下文 (非总结请求)
    - 普通请求：finally 块中删除
    - 流式请求：doFinally 回调中删除
```

---

## 四、上下文管理机制

### VignaMsgContext 结构

```java
public class VignaMsgContext {
    // 消息内容
    private VignaMsg content;          // 当前用户消息
    private VignaMsg aiReply;          // AI 回复
    private List<VignaMsg> historyMsgList;  // 历史记录
    private VignaMsg systemMsg;        // 系统消息
    
    // 请求信息
    private String finishReason;       // 完成原因/原始响应 JSON
    private String sendJson;           // 发送的请求 JSON
    private int trySize;               // 重试次数
    private Date sendTime;             // 发送时间
    private long outTime;              // 超时时间 (毫秒)
    
    // 标识信息
    private int userId;
    private String sessionId;
    private long clientId;
    private String msgId;              // 回复消息 ID
    private VignaMsgType type;         // 消息类型 (generic/edit/retry)
    
    // 状态标志
    private boolean isSum;             // 是否为总结请求
    private boolean error;             // 是否出错
    private String errorMsg;           // 错误信息
    
    // 生命周期管理
    public boolean isDel() {
        if (sendTime == null) return false;
        return System.currentTimeMillis() > sendTime.getTime() + outTime;
    }
}
```

### 上下文生命周期

| 阶段 | 操作 | 说明 |
|------|------|------|
| **创建** | `new VignaMsgContext()` | 在 VignaAiChatImpl 中创建 |
| **设置** | `ChatContextMap.setContext()` | 每次更新后重新写入 |
| **读取** | `ChatContextMap.getContext()` | 过滤器链中多次读取 |
| **删除** | `ChatContextMap.delContext()` | 请求完成后清理（总结请求除外） |

### 并发安全机制

```java
// ChatContextMap 使用 ConcurrentHashMap
private static final ConcurrentHashMap<String, VignaMsgContext> contextMap 
    = new ConcurrentHashMap<>(100);

// 删除时使用原子操作
public static VignaMsgContext delContext(String sessionId) {
    return contextMap.remove(sessionId);  // 原子删除
}

// 定期清理过期上下文
public static void DisposalOfCorpse() {
    for (Map.Entry<String, VignaMsgContext> entry : contextMap.entrySet()) {
        if (entry.getValue().isDel()) {
            contextMap.remove(entry.getKey(), entry.getValue());
        }
    }
}
```

---

## 五、过滤器链机制

### 责任链模式实现

```java
public interface MyBaseAdvisor {
    int getIndex();                    // 排序，越小越前
    void beforeAdvise(String sessionId);  // 请求前执行
    void afterAdvise(String sessionId);   // 请求后执行
}
```

### 三个内置过滤器

#### 1. VignaHistoryAdvisor (历史记录填充器)
- **执行顺序**: `Integer.MIN_VALUE` (最先执行)
- **职责**: 从 Neo4j 加载历史记录到上下文
- **关键逻辑**:
  ```java
  public void beforeAdvise(String sessionId) {
      VignaMsgContext context = ChatContextMap.getContext(sessionId);
      // 获取从上个总结节点到当前的记录
      List<VignaMsg> msgList = msgService.getMsgList(sessionId, msgId, 1);
      context.setHistoryMsgList(msgList);
      ChatContextMap.setContext(sessionId, context);
  }
  ```

#### 2. VignaChatZipAdvisor (上下文压缩器)
- **执行顺序**: `10` (倒数第二)
- **触发条件**:
  - 文本长度 ≥ `maxTokenSize` 配置值
  - 或 对话轮数 ≥ `size` 配置值
  - 且 当前不是总结请求
  - 且 消息类型为 generic 或 null
- **关键逻辑**:
  ```java
  boolean needSum = (maxTokenSize <= textSize || size <= historyMsgList.size())
                    && !context.isSum() 
                    && (context.getType() == null || context.getType() == VignaMsgType.generic);
  
  if (needSum) {
      // 发起总结请求
      ReturnResult<String> result = vignaChat.vignaAiChat(para);
      // 将总结结果添加到历史记录
      context.getHistoryMsgList().add(summaryMsg);
  }
  ```

#### 3. VignaChatInscriberAdvisor (数据持久化器)
- **执行顺序**: `Integer.MAX_VALUE` (最后执行)
- **职责**: 将用户消息和 AI 回复记录到 Neo4j
- **支持的操作类型**:
  - `generic`: 普通消息
  - `edit`: 编辑消息（创建新版本，链接到旧版本的前驱）
  - `retry`: 重试消息（创建重试版本）
- **关键逻辑**:
  ```java
  @Transactional
  public void beforeAdvise(String sessionId) {
      // 根据 type 决定操作
      switch (context.getType()) {
          case edit -> msgService.editData(...);
          case retry -> msgService.retry(...);
          default -> msgService.setData(...);
      }
  }
  
  @Transactional
  public void afterAdvise(String sessionId) {
      // 记录 AI 回复
      VignaMessageNode aiNode = new VignaMessageNode();
      aiNode.setContent(context.getAiReply().getContent());
      aiNode.setLastMessage(userNode);  // 关联用户消息
      msgService.setData(aiNode, sessionId, true);
  }
  ```

---

## 六、重试与容错机制

### 重试策略

```java
// 在 sendRequest 方法中实现
int maxTrySize = config.getMaxTrySize() == null ? 3 : config.getMaxTrySize();
for (int attempt = 1; attempt <= maxTrySize; attempt++) {
    // 重试退避
    if (attempt > 1) {
        long waitMillis = ElseUtil.getNextTime(attempt - 1);
        Thread.sleep(waitMillis);
    }
    
    // 更新尝试信息
    context.setTrySize(attempt);
    context.setSendTime(new Date());
    context.setOutTime(config.getTimeout());
    
    try {
        // 执行 HTTP 请求
        return httpClient.execute(httpRequest, response -> {
            int statusCode = response.getCode();
            if (statusCode < 200 || statusCode >= 300) {
                // 500+ 和 429 可重试
                if (statusCode >= 500 || statusCode == 429)
                    throw new IOException(errorMsg);
                // 其他 4xx 不可重试
                throw new IllegalArgumentException(errorMsg);
            }
            return body;
        });
    } catch (IllegalArgumentException e) {
        // 参数错误，立即终止
        if (!para.isSumUp()) ChatContextMap.delContext(sessionId);
        throw new OperationException("请求参数错误，停止重试");
    } catch (Exception e) {
        // 记录日志，继续下一次重试
        log.warn("请求失败，尝试次数：{}/{}", attempt, maxTrySize);
    }
}
// 所有重试失败
if (!para.isSumUp()) ChatContextMap.delContext(sessionId);
throw new OperationException("AI 模型调用失败，已重试 " + maxTrySize + " 次");
```

### 错误分类处理

| 错误类型 | HTTP 状态码 | 处理方式 |
|----------|------------|----------|
| **临时性错误** | 500, 502, 503, 504, 429 | 重试直到最大次数 |
| **参数错误** | 400, 401, 403, 404 | 立即终止，不清理总结请求上下文 |
| **网络异常** | - | 重试直到最大次数 |
| **中断异常** | - | 立即终止，清理上下文 |

### 上下文清理策略

| 场景 | 清理时机 | 备注 |
|------|----------|------|
| **普通请求成功** | finally 块中 | `if (!para.isSumUp()) delContext()` |
| **普通请求失败** | catch 块中 | 立即清理（总结请求除外） |
| **流式请求完成** | doFinally 回调 | `SignalType.ON_COMPLETE` 时触发后置过滤器 |
| **流式请求失败** | doFinally 回调 | 其他信号类型时清理（总结请求除外） |
| **总结请求** | 不清理 | 保留上下文用于后续普通请求 |

---

## 七、流式响应处理

### 流式请求与普通请求的对齐

#### 普通请求流程
```
1. checkRequiredInfo()
2. beforeAdvise() - 执行过滤器链
3. getContext() - 获取上下文
4. buildRequest() - 组装请求
5. sendRequest() - 发送 HTTP 请求（带重试）
6. 解析完整响应
7. setContext() - 写回上下文
8. afterAdvise() - 执行后置过滤器
9. delContext() - 清理上下文
```

#### 流式请求流程
```
1. 创建 Flux Sink
2. 订阅 sink 用于累积内容和错误处理
3. checkRequiredInfo()
4. beforeAdvise() - 执行过滤器链
5. getContext() - 获取上下文
6. buildRequest() - 组装请求（stream=true）
7. sendStreamRequest() - 发送异步 HTTP 请求
   - 更新上下文（尝试次数、发送时间）
   - 执行 SSE 流式请求
   - 逐块发射到 sink
8. sink.doFinally() 回调
   - ON_COMPLETE: finishStreamContext() → afterAdvise()
   - 其他：清理上下文（总结请求除外）
9. 返回 Flux<String>
```

### 核心技术实现

```java
// 1. 创建桥接 Sink
Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer(1024, false);

// 2. 本地订阅累积内容
StringBuilder aiContent = new StringBuilder();
sink.asFlux()
    .doOnNext(aiContent::append)
    .doFinally(signal -> {
        if (signal == SignalType.ON_COMPLETE) {
            // 流式完成：写回上下文并触发后置过滤器
            finishStreamContext(sessionId, aiContent.toString());
        } else {
            // 出错或取消：清理上下文
            if (!isSumUp) ChatContextMap.delContext(sessionId);
        }
    })
    .subscribe();

// 3. 发送异步 SSE 请求
httpAsyncClient.execute(
    SimpleRequestProducer.create(request),
    new AbstractCharResponseConsumer<Void>() {
        @Override
        protected void data(CharBuffer src, boolean endOfStream) {
            // 解析 SSE 行
            while (src.hasRemaining()) {
                char c = src.get();
                if (c == '\n') {
                    handleSseLine(lineBuffer.toString().trim(), sink);
                    lineBuffer.setLength(0);
                } else if (c != '\r') {
                    lineBuffer.append(c);
                }
            }
            if (endOfStream) {
                sink.tryEmitComplete();
            }
        }
        
        @Override
        public void failed(Exception cause) {
            sink.tryEmitError(cause);
        }
    },
    null
);

// 4. 收尾工作
private void finishStreamContext(String sessionId, String aiContent) {
    VignaMsgContext context = ChatContextMap.getContext(sessionId);
    context.setFinishReason(aiContent);
    VignaMsg aiMsg = new VignaMsg();
    aiMsg.setContent(aiContent);
    context.setAiReply(aiMsg);
    ChatContextMap.setContext(sessionId, context);
    afterAdvise(sessionId);  // 触发后置过滤器链
}
```

---

## 八、会话分支管理

### Neo4j 图数据库模型

#### 节点类型

**VignaSession（会话节点）**
```java
@Node("VignaSession")
public class VignaSessionNode {
    @Id
    private String sessionId;
    private String tailId;  // 指向默认链路最新消息
    private int userId;
    private OffsetDateTime createTime;
}
```

**VignaMessage（消息节点）**
```java
@Node("VignaMessage")
public class VignaMessageNode {
    @Id
    private String messageId;
    private String sessionId;
    private String content;
    private int role;  // 0:user, 1:ai, 2:system, 3:sum
    private int turnIndex;  // 对话轮次下标
    private String lastMessageId;  // 指向前驱消息
    private List<String> modifiedVersions;  // 被修改的版本
    private List<String> retriedVersions;   // 被重试的版本
    private Boolean isSummary;
    private Boolean error;
    private String json;  // 完整请求/响应 JSON
    private OffsetDateTime timestamp;
}
```

#### 关系类型

```
(:VignaSession)-[:HAS_MESSAGE]->(:VignaMessage)  // 会话包含消息
(:VignaMessage)-[:LAST]->(:VignaMessage)         // 消息链前驱关系
(:VignaMessage)-[:MODIFIED_VERSION]->(:VignaMessage)  // 修改版本
(:VignaMessage)-[:RETRIED_VERSION]->(:VignaMessage)   // 重试版本
```

### Edit/Retry 操作实现

#### Edit 操作（编辑消息）
```java
@Transactional
public VignaMessageNode editData(String msgId, String sessionId, VignaMsg msg, Long clientID) {
    // 1. 查找被编辑的旧节点
    VignaMessageNode oldNode = neo4jTemplate.findById(msgId, VignaMessageNode.class)...;
    
    // 2. 创建新节点，前驱是 oldNode 的前驱（跳过 oldNode）
    VignaMessageNode newNode = new VignaMessageNode();
    newNode.setLastMessage(oldNode.getLastMessage());  // 关键：跳过旧节点
    
    // 3. 保存新节点（更新会话 tailId）
    VignaMessageNode saved = setData(newNode, sessionId, true);
    
    // 4. 记录版本关系
    oldNode.getModifiedVersions().add(saved.getMessageId());
    neo4jTemplate.save(oldNode);
    
    return saved;
}
```

#### Retry 操作（重试消息）
```java
@Transactional
public VignaMessageNode retry(String msgId, String sessionId, VignaMsg msg, Long clientID) {
    // 类似 edit，但语义不同
    VignaMessageNode oldNode = neo4jTemplate.findById(msgId, VignaMessageNode.class)...;
    
    VignaMessageNode newNode = new VignaMessageNode();
    newNode.setLastMessage(oldNode.getLastMessage());
    
    VignaMessageNode saved = setData(newNode, sessionId, true);
    
    oldNode.getRetriedVersions().add(saved.getMessageId());
    neo4jTemplate.save(oldNode);
    
    return saved;
}
```

### 历史记录查询

```java
// CypherDSL 查询从指定消息到链首的路径
private List<VignaMessageNode> queryMessageChain(String sessionId, String msgId) {
    Node msg = Cypher.node("VignaMessage").named("msg");
    Node previous = Cypher.node("VignaMessage").named("p");
    NamedPath path = Cypher.path("path").definedBy(
        msg.relationshipTo(previous, "LAST").min(0));
    
    Statement statement;
    if (msgId == null) {
        // 从会话 tailId 开始
        Node session = Cypher.node("VignaSession").named("s");
        statement = Cypher.match(session)
            .where(session.property("sessionId").isEqualTo(sessionId))
            .match(msg)
            .where(msg.property("messageId").isEqualTo(session.property("tailId")))
            .match(path)
            .returning(Cypher.name("path"))
            .build();
    } else {
        // 从指定 msgId 开始
        statement = Cypher.match(msg)
            .where(msg.property("messageId").isEqualTo(msgId))
            .match(path)
            .returning(Cypher.name("path"))
            .build();
    }
    
    return msgRepository.findAll(statement, VignaMessageNode.class);
}
```

---

## 九、自动压缩总结机制

### 触发条件

```java
boolean needSum = 
    (maxTokenSize <= textSize || size <= historyMsgList.size())  // 达到阈值
    && !context.isSum()                                          // 不是总结请求
    && (context.getType() == null || context.getType() == VignaMsgType.generic);  // 普通消息
```

### 总结流程

```
1. 检测到需要压缩
   ↓
2. 创建总结请求参数
   - isSumUp = true
   - 使用专用总结提示词
   ↓
3. 递归调用 vignaAiChat()
   - 不加载历史记录（已有足够上下文）
   - 使用总结专用 Prompt
   ↓
4. 获取总结结果
   ↓
5. 创建总结消息节点
   - role = sum
   - index = userMsg.getIndex() + 2
   - content = "[System Summary]: xxx"
   ↓
6. 将总结节点添加到历史记录
   ↓
7. 重置用户消息下标为 index + 1
   ↓
8. 继续执行普通请求流程
```

### 总结节点特性

| 特性 | 说明 |
|------|------|
| **角色** | `VignaRole.sum` (特殊角色) |
| **下标** | 比上一条 AI 回复大 2 |
| **内容前缀** | `[System Summary]:` |
| **查询过滤** | `getMsgList()` 会跳过总结节点，不加入 AI 上下文 |
| **截断点** | 历史记录查询以上个总结节点为界 |

---

## 十、普通请求 vs 流式请求对比

| 维度 | 普通请求 | 流式请求 |
|------|----------|----------|
| **入口方法** | `vignaAiChat(ChatServicePara)` | `vignaAiChatByStream(ChatServicePara)` |
| **返回类型** | `ReturnResult<String>` | `Flux<String>` |
| **HTTP 客户端** | `CloseableHttpClient` (同步) | `CloseableHttpAsyncClient` (异步) |
| **请求参数** | `stream: false` | `stream: true` |
| **重试机制** | ✅ 支持，可配置次数 | ❌ 不支持，固定 1 次 |
| **上下文创建** | ✅ 相同逻辑 | ✅ 相同逻辑 |
| **前导过滤器** | ✅ 执行 | ✅ 执行 |
| **后置过滤器** | ✅ 执行 | ✅ 执行（在 doFinally 中） |
| **响应解析** | 一次性解析完整 JSON | 逐块解析 SSE 数据 |
| **内容累积** | 不需要 | `StringBuilder` 累积 |
| **错误处理** | try-catch-finally | `doFinally(SignalType)` |
| **上下文清理** | finally 块中 | doFinally 回调中 |
| **日志记录** | 完整请求/响应 | 仅记录最终拼接内容 |

---

## 十一、框架优势总结

### 1. 架构清晰
- 三层架构分工明确
- 职责单一原则贯彻到位
- 易于理解和维护

### 2. 可扩展性强
- 过滤器链支持自定义扩展
- 策略模式支持多 AI 厂商
- 配置驱动，支持动态调整

### 3. 数据完整性
- 完整的请求/响应日志
- Neo4j 图数据库存储对话关系
- 支持分支管理和版本追溯

### 4. 智能化程度高
- 自动上下文压缩总结
- 智能重试和错误分类
- 支持编辑和重试操作

### 5. 用户体验好
- 流式响应降低等待焦虑
- 详细的错误提示
- 会话分支自由切换

### 6. 生产级特性
- 线程安全的上下文管理
- 完整的日志记录（含脱敏）
- 事务保证数据一致性

---

## 十二、改进建议

### 1. 线程安全问题
- **现状**: `ChatContextMap` 使用静态 `ConcurrentHashMap`
- **风险**: 多实例部署时上下文隔离问题
- **建议**: 改为 Spring Cache 或 Redis 分布式缓存

### 2. 内存泄漏风险
- **现状**: 依赖 `DisposalOfCorpse()` 定期清理
- **风险**: 定时任务未启动时可能泄漏
- **建议**: 增加 TTL 过期机制，确保必然清理

### 3. 流式请求上下文清理
- **现状**: 已在 `doFinally` 中完善
- **注意**: 需确保客户端订阅，否则可能不触发回调
- **建议**: 增加超时强制清理机制

### 4. 错误处理增强
- **现状**: 部分错误直接抛异常
- **建议**: 添加全局异常处理器，统一错误格式

### 5. 限流保护
- **现状**: 无限流机制
- **建议**: 添加用户级/会话级限流，防止滥用

### 6. 配置校验
- **现状**: 配置为空时使用默认值
- **建议**: 启动时校验必要配置，失败则阻止启动

---

## 十三、关键代码位置索引

| 功能 | 文件路径 | 关键方法 |
|------|----------|----------|
| **对话入口** | `VignaAiChatImpl.java` | `vignaAiChat()`, `vignaAiChatByStream()` |
| **HTTP 请求** | `VignaHttpClientImpl.java` | `sendAiMsg()`, `sendAiMsgByStream()` |
| **过滤器链** | `VignaHttpClientImpl.java` | `beforeAdvise()`, `afterAdvise()` |
| **历史记录** | `VignaHistoryAdvisor.java` | `beforeAdvise()` |
| **自动压缩** | `VignaChatZipAdvisor.java` | `beforeAdvise()` |
| **数据持久化** | `VignaChatInscriberAdvisor.java` | `beforeAdvise()`, `afterAdvise()` |
| **消息服务** | `AiChatMsgServiceImpl.java` | `getMsgList()`, `editData()`, `retry()` |
| **会话管理** | `VignaSessionServiceImpl.java` | `getSessionId()`, `lockSession()` |
| **上下文存储** | `ChatContextMap.java` | `setContext()`, `getContext()`, `delContext()` |

---

*文档生成时间：2025 年*
*框架版本：开发中*

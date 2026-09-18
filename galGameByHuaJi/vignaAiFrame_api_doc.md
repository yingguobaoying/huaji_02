# VignaAI 框架 API 接口文档

## AI 控制层入口接口

---

# 1. GET /api/user/getUserSessionList

## 无入参

## 返回内容 (存放位置：returnResult 中的 Map)

```json
{
  "string"<会话 id>: [{
    "role": int <角色类型：0=user, 1=ai, 2=system, 3=sum>,
    "content": string <消息内容>,
    "index": int <对话轮次下标>,
    "tools": ? <拓展接口，当前为 null>,
    "timestamp": time <时间，格式为 yyyy/MM/dd hh:mm:ss>
  }]
}
```

**说明**: 
- 返回用户所有会话列表，key 为 sessionId，value 为该会话的首条消息预览
- 当前实现返回空 Map，待后续完善

---

# 2. POST /api/user/chat/sendMessage

## 请求体 JSON

```json
{
  "sessionId": string <会话 ID，必填>,
  "msgId": string <回复的消息 ID，选填，不填则回复默认链路>,
  "content": string <用户发送的消息内容，必填>,
  "clientId": long <使用的客户端配置 ID，选填>,
  "code": string <配置代码，与 clientId 二选一>,
  "type": string <消息类型：generic/edit/retry，默认为 generic>,
  "stream": boolean <是否流式响应，默认为 false>
}
```

## 返回内容

### 普通请求 (stream=false)
存放位置：`ReturnResult<String>`的 `returnResult` 字段

```json
{
  "operationResult": boolean <是否成功>,
  "msg": string <提示信息>,
  "returnResult": string <AI 回复的完整内容>
}
```

### 流式请求 (stream=true)
返回类型：`Flux<String>` (SSE 流式输出)

```
data: <内容片段 1>\n\n
data: <内容片段 2>\n\n
...
data: [DONE]\n\n
```

**说明**:
- type=edit 时，msgId 必填，表示编辑指定消息
- type=retry 时，msgId 必填，表示重试指定消息的 AI 回复
- type=generic 或未指定时，正常对话
- clientId 和 code 优先级：clientId > code > 默认配置

---

# 3. POST /api/user/chat/editMessage

## 请求体 JSON

```json
{
  "sessionId": string <会话 ID，必填>,
  "msgId": string <要编辑的消息 ID，必填>,
  "content": string <新的消息内容，必填>,
  "clientId": long <使用的客户端配置 ID，选填>
}
```

## 返回内容 (存放位置：returnResult)

```json
{
  "messageId": string <新保存的消息节点 ID>,
  "turnIndex": int <对话轮次下标>,
  "content": string <消息内容>,
  "role": int <角色类型>,
  "timestamp": time <创建时间>
}
```

**说明**:
- 编辑消息会创建一个新节点，新节点的前驱是被编辑节点的前驱（跳过被编辑节点）
- 被编辑节点的 modifiedVersions 列表会添加新节点 ID
- 会话的 tailId 会更新为新节点

---

# 4. POST /api/user/chat/retryMessage

## 请求体 JSON

```json
{
  "sessionId": string <会话 ID，必填>,
  "msgId": string <要重试的消息 ID，必填>,
  "clientId": long <使用的客户端配置 ID，选填>
}
```

## 返回内容 (存放位置：returnResult)

```json
{
  "messageId": string <新保存的消息节点 ID>,
  "turnIndex": int <对话轮次下标>,
  "content": string <AI 回复内容>,
  "role": int <角色类型>,
  "timestamp": time <创建时间>
}
```

**说明**:
- 重试消息会创建一个新节点，新节点的前驱是被重试节点的前驱
- 被重试节点的 retriedVersions 列表会添加新节点 ID
- 会话的 tailId 会更新为新节点

---

# 5. GET /api/user/chat/getHistoryMsg

## URL 参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | string | 是 | 会话 ID |
| msgId | string | 否 | 从哪条消息开始查询，不填则从默认链路尾部开始 |
| size | int | 是 | 获取的总结节点个数（不包含总结节点本身） |

## 返回内容 (存放位置：resultList)

```json
[{
  "role": int <角色类型：0=user, 1=ai, 2=system, 3=sum>,
  "content": string <消息内容>,
  "index": int <对话轮次下标>,
  "tools": null <拓展接口，当前为 null>,
  "timestamp": time <时间，格式为 yyyy/MM/dd hh:mm:ss>,
  "messageId": string <消息节点 ID>,
  "isSummary": boolean <是否为总结节点>
}]
```

**说明**:
- 返回从指定消息向上追溯到上个总结节点的完整历史记录
- 总结节点本身不会加入返回结果，仅作为截断点
- 错误消息 (error=true) 会被过滤掉
- 结果按 index 从小到大排序（旧→新）

---

# 6. GET /api/user/chat/getSessionInfo

## URL 参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | string | 是 | 会话 ID |

## 返回内容 (存放位置：returnResult)

```json
{
  "sessionId": string <会话 ID>,
  "userId": int <所属用户 ID>,
  "tailId": string <默认链路最新消息 ID>,
  "createTime": time <创建时间>,
  "lastActiveTime": time <最后活跃时间>
}
```

---

# 7. POST /api/user/chat/createSession

## 请求体 JSON

```json
{
  "userId": int <用户 ID，必填>,
  "clientId": long <使用的客户端配置 ID，选填>,
  "firstMessage": string <首条消息内容，选填>
}
```

## 返回内容 (存放位置：returnResult)

```json
{
  "sessionId": string <新创建的会话 ID>,
  "tailId": string <首条消息 ID，如果有>,
  "createTime": time <创建时间>
}
```

**说明**:
- 创建新会话时会检查用户权限
- 如果提供 firstMessage，会自动创建首条消息节点

---

# 8. DELETE /api/user/chat/deleteSession

## URL 参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | string | 是 | 会话 ID |

## 返回内容

```json
{
  "operationResult": boolean <是否成功>,
  "msg": string <提示信息>
}
```

**说明**:
- 删除会话及其关联的所有消息节点
- 需要检查会话锁状态，占用中的会话不能删除

---

## 附录：枚举值说明

### VignaRole (角色类型)

| 值 | 说明 |
|----|------|
| 0 | user - 用户消息 |
| 1 | ai - AI 回复 |
| 2 | system - 系统消息 |
| 3 | sum - 总结节点 |

### VignaMsgType (消息类型)

| 值 | 说明 |
|----|------|
| generic | 普通消息 |
| edit | 编辑消息 |
| retry | 重试消息 |

### ReturnResult 结构

```java
public class ReturnResult<T> {
    private boolean operationResult;  // 操作是否成功
    private String msg;               // 提示信息
    private T returnResult;           // 返回数据
    private boolean hasError;         // 是否有系统错误
    
    // 静态工厂方法
    public static ReturnResult isTrue(String msg, T data);
    public static ReturnResult isFalse(String msg);
    public static ReturnResult isError(String msg);
}
```

---

*文档生成时间：2025 年*
*API 版本：开发中*

-- gal.ai_client_config 定义

CREATE TABLE `ai_client_config`
(
    `id`                bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `code`              varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '唯一代码（用于在代码中引用该配置）',
    `name`              varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户端显示名称',
    `model`             varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '模型名称（如 qwen:9b, deepseek-chat）',
    `base_url`          text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT 'API基础地址（为空则使用默认）',
    `api_key`           text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT 'API密钥（明文或Vault引用）',
    `key_is_vault`      bit(1)                                                       NOT NULL DEFAULT b'0' COMMENT '1-密钥存储在Vault，api_key为引用路径；0-明文',
    `max_tokens`        int                                                                   DEFAULT NULL COMMENT '最大输出token数',
    `temperature`       int                                                                   DEFAULT NULL COMMENT '温度（0~100，应用层除以100）',
    `top_p`             int                                                                   DEFAULT NULL COMMENT 'Top-P（0~100）',
    `frequency_penalty` int                                                                   DEFAULT NULL COMMENT '频率惩罚（0~100）',
    `presence_penalty`  int                                                                   DEFAULT NULL COMMENT '存在惩罚（0~100）',
    `thinking`          bit(1)                                                       NOT NULL DEFAULT b'0' COMMENT '是否开启思考模式（部分模型支持）',
    `reasoning_effort`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '思考强度（如 low/medium/high）',
    `stream`            bit(1)                                                       NOT NULL DEFAULT b'0' COMMENT '是否默认使用流式响应',
    `timeout`           int                                                                   DEFAULT NULL COMMENT '请求超时时间（ms）',
    `is_active`         bit(1)                                                       NOT NULL DEFAULT b'1' COMMENT '是否启用（用于动态开关）',
    `created_at`        datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`        datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `extra_config json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '额外信息',
    `merchant`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '模型提供商',
    `content`           text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci        NOT NULL COMMENT '提示词模板内容（支持占位符如 {name}）',
    `description`       varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT NULL COMMENT '模板描述',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_code` (`code`) USING BTREE,
    KEY `idx_is_active` (`is_active`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT ='AI客户端配置表（支持多模型、多参数配置）';


-- gal.ai_record 定义

CREATE TABLE `ai_record`
(
    `id`             bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话标识（用于区分不同对话）',
    `role`           int                                                          NOT NULL DEFAULT '1' COMMENT '消息角色：1-用户，2-系统 ,3-ai回复,4-工具',
    `content`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci        NOT NULL COMMENT '消息内容',
    `user_id`        bigint                                                       NOT NULL COMMENT '所属用户ID（记录谁发的消息）',
    `external_id`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT NULL COMMENT '外部系统ID（如第三方消息ID）',
    `created_at`     datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`        tinyint(1)                                                   NOT NULL DEFAULT '0' COMMENT '逻辑删除标志：0-正常，1-已删除',
    `output_tokens`  int                                                                   DEFAULT NULL COMMENT '输出消耗',
    `input_tokens`   int                                                                   DEFAULT NULL COMMENT '输入消耗',
    `client_id`      bigint                                                                DEFAULT NULL COMMENT '客户端ID',
    `prompt_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '使用的提示词(如果存在)',
    `request_json`   json                                                                  DEFAULT NULL COMMENT '请求体json',
    `index`          int                                                                   DEFAULT NULL COMMENT '对话索引',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_session_id` (`session_id`) USING BTREE,
    KEY `idx_user_id` (`user_id`) USING BTREE,
    KEY `idx_created_at` (`created_at`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT ='AI聊天记录表（支持多轮会话）';


-- gal.long_text 定义

CREATE TABLE `long_text`
(
    `LongText` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '长文本内容,此处存放的信息为长文本,比如用户规则,帮助信息等',
    `text_id`  int NOT NULL AUTO_INCREMENT,
    `title`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '文本标题',
    KEY `title` (`title`) USING BTREE,
    KEY `text_id` (`text_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.resources 定义

CREATE TABLE `resources`
(
    `r_id`           int                                                          NOT NULL AUTO_INCREMENT COMMENT '自然主键',
    `r_name`         varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源名称(在后端比较规则:名称+厂家为唯一标识)',
    `r_manufacturer` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '这里是资源的发行商或者作者信息',
    `r_introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '资源的简介',
    `r_type`         enum ('游戏资源','软件资源','GalGame','图包资源','视频资源','RPG','SLG','其他资源') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '资源的分类',
    `r_jpeg`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                        DEFAULT NULL COMMENT '首页略缩',
    `r_enter_time`   datetime                                                                                                                             DEFAULT NULL COMMENT '资源的录入时间',
    `up_user`        bigint                                                                                                                               DEFAULT NULL COMMENT '录入人员信息(这里不设置外键,由后端完成)',
    PRIMARY KEY (`r_id`) USING BTREE,
    UNIQUE KEY `r_name` (`r_name`, `r_manufacturer`) USING BTREE,
    KEY `id` (`r_id`) USING BTREE,
    KEY `name` (`r_name`) USING BTREE,
    KEY `changShang` (`r_manufacturer`) USING BTREE,
    KEY `upUser` (`up_user`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.tag 定义

CREATE TABLE `tag`
(
    `tag_id`   int                                                          NOT NULL AUTO_INCREMENT,
    `tag_name` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    PRIMARY KEY (`tag_id`) USING BTREE,
    KEY `tag_id` (`tag_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 28
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.user_token 定义

CREATE TABLE `user_token`
(
    `token`    varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存放令牌的地方',
    `user_id`  int                                                           NOT NULL COMMENT '请求用户ID',
    `die_time` datetime                                                      NOT NULL COMMENT '令牌过期时间(以这个为准而不是令牌过期时间)',
    `type`     int DEFAULT NULL COMMENT '令牌用途',
    `token_id` bigint                                                        NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`token_id`, `user_id`) USING BTREE,
    UNIQUE KEY `token` (`token`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.users 定义

CREATE TABLE `users`
(
    `user_id`                int                                                           NOT NULL AUTO_INCREMENT COMMENT '自增的自然主键',
    `user_name`              varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '用来称呼用户的名称',
    `user_name_login`        varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL                                                       DEFAULT '' COMMENT '用户用来登陆的名称',
    `user_password`          text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NOT NULL COMMENT '密码(这里将会存放加密后的密码)',
    `Coin`                   int                                                                                                                          DEFAULT '0' COMMENT '用户的积分/硬币,这个是用来限制用户使用本地服务器下载使用的',
    `mailbox`                varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
    `bio`                    text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '用户的简介',
    `user_head_portrait_url` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                 DEFAULT NULL COMMENT '用户头像,命名统一为user_id.*(默认为default.jpeg)',
    `status`                 enum ('ok','blacklist','frozen','banned','is disabled','Not authenticated') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'Not authenticated' COMMENT '用户的账号状态,当前有:\r\nok:正常  blacklist:被拉黑  frozen:冻结(用户手动)  bnanner:被封号力(管理员操作) is disabled :不可用(因为违反一些规则导致的)Not authenticated:注册了没有验证邮箱',
    `sex`                    varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                 DEFAULT NULL COMMENT '用户性别',
    `jurisdiction`           int                                                                                                                          DEFAULT '2' COMMENT '权限级别:1-10,10为root,9为用户管理,0为游客,1\r\n未验证用户,3为普通用户,4,为高级用户,5为资源管理,\r\n所有用户均可编辑自己上传的链接资源\r\n未说明的后面按照需要添加(高权限有低级权限的所有权限)(如果后面太多可以将其作为一个字典表的主键使用)\r\n',
    `birthday`               date                                                                                                                         DEFAULT NULL COMMENT '生日默认为1970-01-01',
    `register_time`          datetime                                                                                                                     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '注册时间',
    `user_pe`                varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                 DEFAULT NULL COMMENT '手机号',
    PRIMARY KEY (`user_id`) USING BTREE,
    UNIQUE KEY `id` (`user_id`) USING BTREE,
    UNIQUE KEY `user login name` (`user_name_login`) USING BTREE,
    UNIQUE KEY `user mail` (`mailbox`) USING BTREE,
    UNIQUE KEY `user pe` (`user_pe`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.comment 定义

CREATE TABLE `comment`
(
    `comment_id`     bigint NOT NULL AUTO_INCREMENT,
    `comment`        text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '内容',
    `comment_user`   int    NOT NULL COMMENT '评论用户',
    `comment_time`   datetime DEFAULT NULL COMMENT '评论时间',
    `father_comment` bigint   DEFAULT NULL COMMENT '父评论,如果为0则是顶置',
    `comment_r_id`   int      DEFAULT NULL COMMENT '评论位置',
    PRIMARY KEY (`comment_id`) USING BTREE,
    KEY `comment_r` (`comment_r_id`) USING BTREE,
    KEY `comment_user` (`comment_user`) USING BTREE,
    KEY `father_comment` (`father_comment`) USING BTREE,
    CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`comment_r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`comment_user`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `comment_ibfk_3` FOREIGN KEY (`father_comment`) REFERENCES `comment` (`comment_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC;


-- gal.feedback 定义

CREATE TABLE `feedback`
(
    `text`  text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '反馈内容',
    `u_id`  int                                                   NOT NULL,
    `state` enum ('正在处理','处理完成','等待处理') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci                                       DEFAULT NULL COMMENT '状态',
    `type`  enum ('bug','UI错误','请求收录新的资源','资源链接错误','账户问题','其他问题') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
    KEY `feedback_User` (`u_id`) USING BTREE,
    CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`u_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC;


-- gal.fen 定义

CREATE TABLE `fen`
(
    `r_id`    int NOT NULL,
    `user_id` int DEFAULT NULL,
    KEY `fen_user` (`r_id`) USING BTREE,
    KEY `user_id` (`user_id`) USING BTREE,
    CONSTRAINT `fen_ibfk_1` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fen_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC;


-- gal.friend_map 定义

CREATE TABLE `friend_map`
(
    `user_id`             int NOT NULL COMMENT '用户',
    `friend_id`           int NOT NULL COMMENT '关注的用户',
    `friend_confirmation` bit(1) DEFAULT b'0' COMMENT '是否被确认',
    `initiate_user`       int    DEFAULT NULL COMMENT '发起用户',
    PRIMARY KEY (`user_id`, `friend_id`) USING BTREE,
    KEY `friend_id` (`friend_id`) USING BTREE,
    CONSTRAINT `friend_map_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `friend_map_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.links 定义

CREATE TABLE `links`
(
    `link`         text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '链接本体',
    `link_up_user` int                                                                           DEFAULT NULL COMMENT '链接上传者',
    `link_r`       int                                                                           DEFAULT NULL COMMENT '链接指向的资源',
    `link_state`   enum ('ok','instable','die') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '链接状态,在,炸了,不稳定',
    `links_public` enum ('yes','no') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci            DEFAULT NULL COMMENT '是否是官方上传',
    `up_time`      datetime                                                                      DEFAULT NULL COMMENT '链接提供时间',
    `notes`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '注释信息/使用注意事项',
    `link_pointer` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                  DEFAULT NULL COMMENT '链接指向',
    `link_id`      bigint NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`link_id`) USING BTREE,
    KEY `rid` (`link_r`) USING BTREE,
    KEY `userid` (`link_up_user`) USING BTREE,
    CONSTRAINT `links_ibfk_1` FOREIGN KEY (`link_r`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `links_ibfk_2` FOREIGN KEY (`link_up_user`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.resource_extension_information 定义

CREATE TABLE `resource_extension_information`
(
    `r_id`                   int NOT NULL,
    `link_price`             int                                                                DEFAULT '1' COMMENT '获取外部连接资源价格(需要的积分)',
    `download_locally_price` int                                                                DEFAULT '5' COMMENT '获取本地下载的连接',
    `has_download_locally`   enum ('yes','no') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'no' COMMENT '是否存在本地连接信息',
    PRIMARY KEY (`r_id`) USING BTREE,
    KEY `r_id` (`r_id`) USING BTREE,
    CONSTRAINT `resource_extension_information_ibfk_1` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.resources_file_map 定义

CREATE TABLE `resources_file_map`
(
    `r_id`      int                                                           DEFAULT NULL COMMENT '属于的资源',
    `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '文件路径/文件名称',
    `up_user`   int                                                           DEFAULT NULL COMMENT '提供者',
    `is_public` bit(1)                                                        DEFAULT NULL COMMENT '是否为官方渠道',
    `size`      int                                                           DEFAULT NULL COMMENT '文件数量',
    `file_size` bigint                                                        DEFAULT NULL COMMENT '本文件大小',
    `notes`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '注释信息/使用注意事项',
    KEY `resources_file_map_ibfk_1` (`r_id`) USING BTREE,
    CONSTRAINT `resources_file_map_ibfk_1` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.resources_jpeg_map 定义

CREATE TABLE `resources_jpeg_map`
(
    `resources_id` int NOT NULL COMMENT '资源编号',
    `jpeg_name`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '对应的图片名称/url',
    KEY `resources_id` (`resources_id`) USING BTREE,
    CONSTRAINT `resources_jpeg_map_ibfk_1` FOREIGN KEY (`resources_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.resources_tag_map 定义

CREATE TABLE `resources_tag_map`
(
    `r_id`   int NOT NULL,
    `tag_id` int NOT NULL,
    PRIMARY KEY (`r_id`, `tag_id`) USING BTREE,
    KEY `tag_id` (`tag_id`) USING BTREE,
    CONSTRAINT `resources_tag_map_ibfk_1` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `resources_tag_map_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC;


-- gal.`session` 定义

CREATE TABLE `session`
(
    `session_id`      bigint NOT NULL AUTO_INCREMENT,
    `status`          bit(1)                                                       DEFAULT NULL COMMENT '当前状态,在,不在,不可用(原因为用户状态)',
    `user_id`         int    NOT NULL COMMENT '对应用户',
    `last_login_time` datetime                                                     DEFAULT NULL COMMENT '上次登录时间',
    `last_login_IP`   varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '上次登录的IP地址',
    PRIMARY KEY (`session_id`) USING BTREE,
    KEY `user` (`user_id`) USING BTREE,
    CONSTRAINT `session_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.user_download 定义

CREATE TABLE `user_download`
(
    `r_id`      int                                                           DEFAULT NULL,
    `user_id`   int NOT NULL,
    `time`      datetime                                                      DEFAULT NULL,
    `d_type`    varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   DEFAULT '外部链接',
    `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    KEY `rid` (`r_id`) USING BTREE,
    KEY `userid` (`user_id`) USING BTREE,
    CONSTRAINT `user_download_ibfk_1` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `user_download_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;


-- gal.user_resource_repository 定义

CREATE TABLE `user_resource_repository`
(
    `user_id`         int NOT NULL COMMENT '获取资源的用户ID',
    `r_id`            int      DEFAULT NULL COMMENT '被获取的资源',
    `has_down`        bit(1)   DEFAULT NULL COMMENT '本地下载权限',
    `expiration_time` datetime DEFAULT NULL COMMENT '保存信息过期时间,默认为24小时',
    `get_time`        datetime DEFAULT NULL COMMENT 's',
    `has_link`        bit(1)   DEFAULT NULL COMMENT '是否有外部链接获取权限',
    KEY `user_id` (`user_id`) USING BTREE,
    KEY `r_id` (`r_id`) USING BTREE,
    CONSTRAINT `user_resource_repository_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `user_resource_repository_ibfk_2` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

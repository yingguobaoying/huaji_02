/*
 Navicat Premium Data Transfer

 Source Server         : mygalgame
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : gal

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 24/08/2026 18:07:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_classification
-- ----------------------------
DROP TABLE IF EXISTS `ai_classification`;
CREATE TABLE `ai_classification`
(
    `client_id`         bigint(0) NOT NULL COMMENT 'ai客户端id',
    `gave_user`         int(0)    NULL DEFAULT NULL COMMENT '授权的用户',
    `id`                bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `need_jurisdiction` int(0)    NULL DEFAULT NULL COMMENT '需要的权限,当这里有值的时候无法授予用户',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `ai_classification_ai_client_config_id_fk` (`client_id`) USING BTREE,
    INDEX `ai_classification_users_user_id_fk` (`gave_user`) USING BTREE,
    CONSTRAINT `ai_classification_ai_client_config_id_fk` FOREIGN KEY (`client_id`) REFERENCES `ai_client_config` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `ai_classification_users_user_id_fk` FOREIGN KEY (`gave_user`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI授权列表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ai_client_config
-- ----------------------------
DROP TABLE IF EXISTS `ai_client_config`;
CREATE TABLE `ai_client_config`
(
    `id`                bigint(0)                                                     NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `code`              varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '唯一代码（用于在代码中引用该配置）',
    `name`              varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '客户端显示名称',
    `model`             varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '模型名称（如 qwen:9b, deepseek-chat）',
    `base_url`          text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT 'API基础地址（为空则使用默认）',
    `api_key`           text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT 'API密钥（明文或Vault引用）',
    `key_is_vault`      bit(1)                                                        NOT NULL DEFAULT b'0' COMMENT '1-密钥存储在Vault，api_key为引用路径；0-明文',
    `max_tokens`        int(0)                                                        NULL     DEFAULT NULL COMMENT '最大输出token数',
    `temperature`       int(0)                                                        NULL     DEFAULT NULL COMMENT '温度（0~100，应用层除以100）',
    `top_p`             int(0)                                                        NULL     DEFAULT NULL COMMENT 'Top-P（0~100）',
    `frequency_penalty` int(0)                                                        NULL     DEFAULT NULL COMMENT '频率惩罚（0~100）',
    `presence_penalty`  int(0)                                                        NULL     DEFAULT NULL COMMENT '存在惩罚（0~100）',
    `thinking`          bit(1)                                                        NOT NULL DEFAULT b'0' COMMENT '是否开启思考模式（部分模型支持）',
    `reasoning_effort`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '思考强度（如 low/medium/high）',
    `stream`            bit(1)                                                        NOT NULL DEFAULT b'0' COMMENT '是否默认使用流式响应',
    `timeout`           bigint(0)                                                     NULL     DEFAULT NULL COMMENT '请求超时时间（ms）',
    `is_active`         bit(1)                                                        NOT NULL DEFAULT b'1' COMMENT '是否启用（用于动态开关）',
    `created_at`        datetime(0)                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`        datetime(0)                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `extra_config_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '额外信息',
    `merchant`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '模型提供商',
    `content`           text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NOT NULL COMMENT '提示词模板内容（支持占位符如 {name}）',
    `description`       varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '模板描述',
    `max_try_size`      int(0)                                                        NULL     DEFAULT NULL COMMENT '最大重试次数',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_code` (`code`) USING BTREE,
    INDEX `idx_is_active` (`is_active`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI客户端配置表（支持多模型、多参数配置）'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ai_record
-- ----------------------------
DROP TABLE IF EXISTS `ai_record`;
CREATE TABLE `ai_record`
(
    `id`             bigint(0)                                                     NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '会话标识（用于区分不同对话）',
    `role`           int(0)                                                        NOT NULL DEFAULT 1 COMMENT '消息角色：1-用户，2-系统 ,3-ai回复,4-工具',
    `content`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NOT NULL COMMENT '消息内容',
    `user_id`        int(0)                                                        NOT NULL COMMENT '所属用户ID（记录谁发的消息）',
    `external_id`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '外部系统ID（如第三方消息ID）',
    `created_at`     datetime(0)                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`        tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-正常，1-已删除',
    `output_tokens`  int(0)                                                        NULL     DEFAULT NULL COMMENT '输出消耗',
    `input_tokens`   int(0)                                                        NULL     DEFAULT NULL COMMENT '输入消耗',
    `client_id`      bigint(0)                                                     NULL     DEFAULT NULL COMMENT '客户端ID',
    `prompt_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '使用的提示词(如果存在)',
    `request_json`   text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '请求体json',
    `chat_index`     int(0)                                                        NULL     DEFAULT NULL COMMENT '对话索引',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_session_id` (`session_id`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_created_at` (`created_at`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI聊天记录表（支持多轮会话）'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`
(
    `comment_id`     bigint(0)                                       NOT NULL AUTO_INCREMENT,
    `comment`        text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '内容',
    `comment_user`   int(0)                                          NOT NULL COMMENT '评论用户',
    `comment_time`   datetime(0)                                     NULL DEFAULT NULL COMMENT '评论时间',
    `father_comment` bigint(0)                                       NULL DEFAULT NULL COMMENT '父评论,如果为0则是顶置',
    `comment_r_id`   int(0)                                          NULL DEFAULT NULL COMMENT '评论位置',
    PRIMARY KEY (`comment_id`) USING BTREE,
    INDEX `comment_r` (`comment_r_id`) USING BTREE,
    INDEX `comment_user` (`comment_user`) USING BTREE,
    INDEX `father_comment` (`father_comment`) USING BTREE,
    CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`comment_r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`comment_user`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `comment_ibfk_3` FOREIGN KEY (`father_comment`) REFERENCES `comment` (`comment_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`
(
    `text`  text CHARACTER SET utf8 COLLATE utf8_general_ci                                                                          NOT NULL COMMENT '反馈内容',
    `u_id`  int(0)                                                                                                                   NOT NULL,
    `state` enum ('正在处理','处理完成','等待处理') CHARACTER SET utf8 COLLATE utf8_general_ci                                       NULL DEFAULT NULL COMMENT '状态',
    `type`  enum ('bug','UI错误','请求收录新的资源','资源链接错误','账户问题','其他问题') CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    INDEX `feedback_User` (`u_id`) USING BTREE,
    CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`u_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fen
-- ----------------------------
DROP TABLE IF EXISTS `fen`;
CREATE TABLE `fen`
(
    `r_id`    int(0) NOT NULL,
    `user_id` int(0) NULL DEFAULT NULL,
    INDEX `fen_user` (`r_id`) USING BTREE,
    INDEX `user_id` (`user_id`) USING BTREE,
    CONSTRAINT `fen_ibfk_1` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fen_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for friend_map
-- ----------------------------
DROP TABLE IF EXISTS `friend_map`;
CREATE TABLE `friend_map`
(
    `user_id`             int(0) NOT NULL COMMENT '用户',
    `friend_id`           int(0) NOT NULL COMMENT '关注的用户',
    `friend_confirmation` bit(1) NULL DEFAULT b'0' COMMENT '是否被确认',
    `initiate_user`       int(0) NULL DEFAULT NULL COMMENT '发起用户',
    PRIMARY KEY (`user_id`, `friend_id`) USING BTREE,
    INDEX `friend_id` (`friend_id`) USING BTREE,
    CONSTRAINT `friend_map_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `friend_map_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for links
-- ----------------------------
DROP TABLE IF EXISTS `links`;
CREATE TABLE `links`
(
    `link`         text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                         NULL COMMENT '链接本体',
    `link_up_user` int(0)                                                                        NULL DEFAULT NULL COMMENT '链接上传者',
    `link_r`       int(0)                                                                        NULL DEFAULT NULL COMMENT '链接指向的资源',
    `link_state`   enum ('ok','instable','die') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '链接状态,在,炸了,不稳定',
    `links_public` enum ('yes','no') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci            NULL DEFAULT NULL COMMENT '是否是官方上传',
    `up_time`      datetime(0)                                                                   NULL DEFAULT NULL COMMENT '链接提供时间',
    `notes`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                         NULL COMMENT '注释信息/使用注意事项',
    `link_pointer` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                  NULL DEFAULT NULL COMMENT '链接指向',
    `link_id`      bigint(0)                                                                     NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`link_id`) USING BTREE,
    INDEX `rid` (`link_r`) USING BTREE,
    INDEX `userid` (`link_up_user`) USING BTREE,
    CONSTRAINT `links_ibfk_1` FOREIGN KEY (`link_r`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `links_ibfk_2` FOREIGN KEY (`link_up_user`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for long_text
-- ----------------------------
DROP TABLE IF EXISTS `long_text`;
CREATE TABLE `long_text`
(
    `LongText` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci     NULL COMMENT '长文本内容,此处存放的信息为长文本,比如用户规则,帮助信息等',
    `text_id`  int(0)                                                        NOT NULL AUTO_INCREMENT,
    `title`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文本标题',
    INDEX `title` (`title`) USING BTREE,
    INDEX `text_id` (`text_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resource_extension_information
-- ----------------------------
DROP TABLE IF EXISTS `resource_extension_information`;
CREATE TABLE `resource_extension_information`
(
    `r_id`                   int(0)                                                             NOT NULL,
    `link_price`             int(0)                                                             NULL DEFAULT 1 COMMENT '获取外部连接资源价格(需要的积分)',
    `download_locally_price` int(0)                                                             NULL DEFAULT 5 COMMENT '获取本地下载的连接',
    `has_download_locally`   enum ('yes','no') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'no' COMMENT '是否存在本地连接信息',
    PRIMARY KEY (`r_id`) USING BTREE,
    INDEX `r_id` (`r_id`) USING BTREE,
    CONSTRAINT `resource_extension_information_ibfk_1` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resources
-- ----------------------------
DROP TABLE IF EXISTS `resources`;
CREATE TABLE `resources`
(
    `r_id`           int(0)                                                                                                                               NOT NULL AUTO_INCREMENT COMMENT '自然主键',
    `r_name`         varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                         NOT NULL COMMENT '资源名称(在后端比较规则:名称+厂家为唯一标识)',
    `r_manufacturer` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                         NOT NULL COMMENT '这里是资源的发行商或者作者信息',
    `r_introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                                NULL COMMENT '资源的简介',
    `r_type`         enum ('游戏资源','软件资源','GalGame','图包资源','视频资源','RPG','SLG','其他资源') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资源的分类',
    `r_jpeg`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                        NULL DEFAULT NULL COMMENT '首页略缩',
    `r_enter_time`   datetime(0)                                                                                                                          NULL DEFAULT NULL COMMENT '资源的录入时间',
    `up_user`        bigint(0)                                                                                                                            NULL DEFAULT NULL COMMENT '录入人员信息(这里不设置外键,由后端完成)',
    PRIMARY KEY (`r_id`) USING BTREE,
    UNIQUE INDEX `r_name` (`r_name`, `r_manufacturer`) USING BTREE,
    INDEX `id` (`r_id`) USING BTREE,
    INDEX `name` (`r_name`) USING BTREE,
    INDEX `changShang` (`r_manufacturer`) USING BTREE,
    INDEX `upUser` (`up_user`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resources_file_map
-- ----------------------------
DROP TABLE IF EXISTS `resources_file_map`;
CREATE TABLE `resources_file_map`
(
    `r_id`      int(0)                                                        NULL DEFAULT NULL COMMENT '属于的资源',
    `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件路径/文件名称',
    `up_user`   int(0)                                                        NULL DEFAULT NULL COMMENT '提供者',
    `is_public` bit(1)                                                        NULL DEFAULT NULL COMMENT '是否为官方渠道',
    `size`      int(0)                                                        NULL DEFAULT NULL COMMENT '文件数量',
    `file_size` bigint(0)                                                     NULL DEFAULT NULL COMMENT '本文件大小',
    `notes`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '注释信息/使用注意事项',
    INDEX `resources_file_map_ibfk_1` (`r_id`) USING BTREE,
    CONSTRAINT `resources_file_map_ibfk_1` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resources_jpeg_map
-- ----------------------------
DROP TABLE IF EXISTS `resources_jpeg_map`;
CREATE TABLE `resources_jpeg_map`
(
    `resources_id` int(0)                                                        NOT NULL COMMENT '资源编号',
    `jpeg_name`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对应的图片名称/url',
    INDEX `resources_id` (`resources_id`) USING BTREE,
    CONSTRAINT `resources_jpeg_map_ibfk_1` FOREIGN KEY (`resources_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resources_tag_map
-- ----------------------------
DROP TABLE IF EXISTS `resources_tag_map`;
CREATE TABLE `resources_tag_map`
(
    `r_id`   int(0) NOT NULL,
    `tag_id` int(0) NOT NULL,
    PRIMARY KEY (`r_id`, `tag_id`) USING BTREE,
    INDEX `tag_id` (`tag_id`) USING BTREE,
    CONSTRAINT `resources_tag_map_ibfk_1` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `resources_tag_map_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for session
-- ----------------------------
DROP TABLE IF EXISTS `session`;
CREATE TABLE `session`
(
    `session_id`      bigint(0)                                                    NOT NULL AUTO_INCREMENT,
    `status`          bit(1)                                                       NULL DEFAULT NULL COMMENT '当前状态,在,不在,不可用(原因为用户状态)',
    `user_id`         int(0)                                                       NOT NULL COMMENT '对应用户',
    `last_login_time` datetime(0)                                                  NULL DEFAULT NULL COMMENT '上次登录时间',
    `last_login_IP`   varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上次登录的IP地址',
    PRIMARY KEY (`session_id`) USING BTREE,
    INDEX `user` (`user_id`) USING BTREE,
    CONSTRAINT `session_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`
(
    `tag_id`   int(0)                                                       NOT NULL AUTO_INCREMENT,
    `tag_name` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    PRIMARY KEY (`tag_id`) USING BTREE,
    INDEX `tag_id` (`tag_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_download
-- ----------------------------
DROP TABLE IF EXISTS `user_download`;
CREATE TABLE `user_download`
(
    `r_id`      int(0)                                                        NULL DEFAULT NULL,
    `user_id`   int(0)                                                        NOT NULL,
    `time`      datetime(0)                                                   NULL DEFAULT NULL,
    `d_type`    varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NULL DEFAULT '外部链接',
    `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
    INDEX `rid` (`r_id`) USING BTREE,
    INDEX `userid` (`user_id`) USING BTREE,
    CONSTRAINT `user_download_ibfk_1` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `user_download_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_resource_repository
-- ----------------------------
DROP TABLE IF EXISTS `user_resource_repository`;
CREATE TABLE `user_resource_repository`
(
    `user_id`         int(0)      NOT NULL COMMENT '获取资源的用户ID',
    `r_id`            int(0)      NULL DEFAULT NULL COMMENT '被获取的资源',
    `has_down`        bit(1)      NULL DEFAULT NULL COMMENT '本地下载权限',
    `expiration_time` datetime(0) NULL DEFAULT NULL COMMENT '保存信息过期时间,默认为24小时',
    `get_time`        datetime(0) NULL DEFAULT NULL COMMENT 's',
    `has_link`        bit(1)      NULL DEFAULT NULL COMMENT '是否有外部链接获取权限',
    INDEX `user_id` (`user_id`) USING BTREE,
    INDEX `r_id` (`r_id`) USING BTREE,
    CONSTRAINT `user_resource_repository_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `user_resource_repository_ibfk_2` FOREIGN KEY (`r_id`) REFERENCES `resources` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_token
-- ----------------------------
DROP TABLE IF EXISTS `user_token`;
CREATE TABLE `user_token`
(
    `token`    varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存放令牌的地方',
    `user_id`  int(0)                                                        NOT NULL COMMENT '请求用户ID',
    `die_time` datetime(0)                                                   NOT NULL COMMENT '令牌过期时间(以这个为准而不是令牌过期时间)',
    `type`     int(0)                                                        NULL DEFAULT NULL COMMENT '令牌用途',
    `token_id` bigint(0)                                                     NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`token_id`, `user_id`) USING BTREE,
    UNIQUE INDEX `token` (`token`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`
(
    `user_id`                int(0)                                                                                                                       NOT NULL AUTO_INCREMENT COMMENT '自增的自然主键',
    `user_name`              varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                 NOT NULL COMMENT '用来称呼用户的名称',
    `user_name_login`        varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                 NOT NULL DEFAULT '' COMMENT '用户用来登陆的名称',
    `user_password`          text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                        NOT NULL COMMENT '密码(这里将会存放加密后的密码)',
    `Coin`                   int(0)                                                                                                                       NULL     DEFAULT 0 COMMENT '用户的积分/硬币,这个是用来限制用户使用本地服务器下载使用的',
    `mailbox`                varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                NOT NULL COMMENT '邮箱',
    `bio`                    text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                        NULL COMMENT '用户的简介',
    `user_head_portrait_url` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                 NULL     DEFAULT NULL COMMENT '用户头像,命名统一为user_id.*(默认为default.jpeg)',
    `status`                 enum ('ok','blacklist','frozen','banned','is disabled','Not authenticated') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT 'Not authenticated' COMMENT '用户的账号状态,当前有:\r\nok:正常  blacklist:被拉黑  frozen:冻结(用户手动)  bnanner:被封号力(管理员操作) is disabled :不可用(因为违反一些规则导致的)Not authenticated:注册了没有验证邮箱',
    `sex`                    varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                 NULL     DEFAULT NULL COMMENT '用户性别',
    `jurisdiction`           int(0)                                                                                                                       NULL     DEFAULT 2 COMMENT '权限级别:1-10,10为root,9为用户管理,0为游客,1\r\n未验证用户,3为普通用户,4,为高级用户,5为资源管理,\r\n所有用户均可编辑自己上传的链接资源\r\n未说明的后面按照需要添加(高权限有低级权限的所有权限)(如果后面太多可以将其作为一个字典表的主键使用)\r\n',
    `birthday`               date                                                                                                                         NULL     DEFAULT NULL COMMENT '生日默认为1970-01-01',
    `register_time`          datetime(0)                                                                                                                  NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '注册时间',
    `user_pe`                varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci                                                                 NULL     DEFAULT NULL COMMENT '手机号',
    PRIMARY KEY (`user_id`) USING BTREE,
    UNIQUE INDEX `id` (`user_id`) USING BTREE,
    UNIQUE INDEX `user login name` (`user_name_login`) USING BTREE,
    UNIQUE INDEX `user mail` (`mailbox`) USING BTREE,
    UNIQUE INDEX `user pe` (`user_pe`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

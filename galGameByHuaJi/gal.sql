create table gal.ai_classification
(
    client_id         bigint not null comment 'ai客户端id',
    gave_user         int    null comment '授权的用户',
    id                bigint auto_increment comment 'id'
        primary key,
    need_jurisdiction int    null comment '需要的权限,当这里有值的时候无法授予用户',
    constraint ai_classification_ai_client_config_id_fk
        foreign key (client_id) references gal.ai_client_config (id)
            on update cascade on delete cascade,
    constraint ai_classification_users_user_id_fk
        foreign key (gave_user) references gal.users (user_id)
            on update cascade on delete cascade
)
    comment 'AI授权列表';


create table ai_client_config
(
    id                  bigint auto_increment comment '主键ID'
        primary key,
    code                varchar(64)                        not null comment '唯一代码（用于在代码中引用该配置）',
    name                varchar(64)                        not null comment '客户端显示名称',
    model               varchar(64)                        null comment '模型名称（如 qwen:9b, deepseek-chat）',
    base_url            text                               null comment 'API基础地址（为空则使用默认）',
    api_key             text                               null comment 'API密钥（明文或Vault引用）',
    key_is_vault        bit      default b'0'              not null comment '1-密钥存储在Vault，api_key为引用路径；0-明文',
    max_tokens          int                                null comment '最大输出token数',
    temperature         int                                null comment '温度（0~100，应用层除以100）',
    top_p               int                                null comment 'Top-P（0~100）',
    frequency_penalty   int                                null comment '频率惩罚（0~100）',
    presence_penalty    int                                null comment '存在惩罚（0~100）',
    thinking            bit      default b'0'              not null comment '是否开启思考模式（部分模型支持）',
    reasoning_effort    varchar(64)                        null comment '思考强度（如 low/medium/high）',
    stream              bit      default b'0'              not null comment '是否默认使用流式响应',
    timeout             int                                null comment '请求超时时间（ms）',
    is_active           bit      default b'1'              not null comment '是否启用（用于动态开关）',
    created_at          datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at          datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `extra_config json` text                               null comment '额外信息',
    merchant            varchar(64)                        null comment '模型提供商',
    content             text                               not null comment '提示词模板内容（支持占位符如 {name}）',
    description         varchar(512)                       null comment '模板描述',
    constraint uk_code
        unique (code)
)
    comment 'AI客户端配置表（支持多模型、多参数配置）' row_format = DYNAMIC;

create index idx_is_active
    on ai_client_config (is_active);

create table gal.ai_record
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    session_id     varchar(64)                          not null comment '会话标识（用于区分不同对话）',
    role           int        default 1                 not null comment '消息角色：1-用户，2-系统 ,3-ai回复,4-工具',
    content        text                                 not null comment '消息内容',
    user_id        int                                  not null comment '所属用户ID（记录谁发的消息）',
    external_id    varchar(100)                         null comment '外部系统ID（如第三方消息ID）',
    created_at     datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    deleted        tinyint(1) default 0                 not null comment '逻辑删除标志：0-正常，1-已删除',
    output_tokens  int                                  null comment '输出消耗',
    input_tokens   int                                  null comment '输入消耗',
    client_id      bigint                               null comment '客户端ID',
    prompt_content text                                 null comment '使用的提示词(如果存在)',
    request_json   json                                 null comment '请求体json',
    chat_index     int                                  null comment '对话索引'
)
    comment 'AI聊天记录表（支持多轮会话）' row_format = DYNAMIC;

create index idx_created_at
    on gal.ai_record (created_at);

create index idx_session_id
    on gal.ai_record (session_id);

create index idx_user_id
    on gal.ai_record (user_id);


create index idx_created_at
    on ai_record (created_at);

create index idx_session_id
    on ai_record (session_id);

create index idx_user_id
    on ai_record (user_id);

create table long_text
(
    `LongText` longtext     null comment '长文本内容,此处存放的信息为长文本,比如用户规则,帮助信息等',
    text_id    int,
    title      varchar(255) null comment '文本标题'
)
    row_format = DYNAMIC;

create index text_id
    on long_text (text_id);

create index title
    on long_text (title);

alter table long_text
    modify text_id int auto_increment;

create table resources
(
    r_id           int auto_increment comment '自然主键'
        primary key,
    r_name         varchar(16)                                                                                not null comment '资源名称(在后端比较规则:名称+厂家为唯一标识)',
    r_manufacturer varchar(32)                                                                                not null comment '这里是资源的发行商或者作者信息',
    r_introduction text                                                                                       null comment '资源的简介',
    r_type         enum ('游戏资源', '软件资源', 'GalGame', '图包资源', '视频资源', 'RPG', 'SLG', '其他资源') null comment '资源的分类',
    r_jpeg         varchar(255)                                                                               null comment '首页略缩',
    r_enter_time   datetime                                                                                   null comment '资源的录入时间',
    up_user        bigint                                                                                     null comment '录入人员信息(这里不设置外键,由后端完成)',
    constraint r_name
        unique (r_name, r_manufacturer)
)
    row_format = DYNAMIC;

create table resource_extension_information
(
    r_id                   int                             not null
        primary key,
    link_price             int                default 1    null comment '获取外部连接资源价格(需要的积分)',
    download_locally_price int                default 5    null comment '获取本地下载的连接',
    has_download_locally   enum ('yes', 'no') default 'no' null comment '是否存在本地连接信息',
    constraint resource_extension_information_ibfk_1
        foreign key (r_id) references resources (r_id)
            on update cascade on delete cascade
)
    row_format = DYNAMIC;

create index r_id
    on resource_extension_information (r_id);

create index changShang
    on resources (r_manufacturer);

create index id
    on resources (r_id);

create index name
    on resources (r_name);

create index upUser
    on resources (up_user);

create table resources_file_map
(
    r_id      int          null comment '属于的资源',
    file_name varchar(255) null comment '文件路径/文件名称',
    up_user   int          null comment '提供者',
    is_public bit          null comment '是否为官方渠道',
    size      int          null comment '文件数量',
    file_size bigint       null comment '本文件大小',
    notes     text         null comment '注释信息/使用注意事项',
    constraint resources_file_map_ibfk_1
        foreign key (r_id) references resources (r_id)
            on update cascade on delete cascade
)
    row_format = DYNAMIC;

create table resources_jpeg_map
(
    resources_id int          not null comment '资源编号',
    jpeg_name    varchar(255) null comment '对应的图片名称/url',
    constraint resources_jpeg_map_ibfk_1
        foreign key (resources_id) references resources (r_id)
            on update cascade on delete cascade
)
    row_format = DYNAMIC;

create index resources_id
    on resources_jpeg_map (resources_id);

create table tag
(
    tag_id   int auto_increment
        primary key,
    tag_name varchar(15) not null
)
    row_format = DYNAMIC;

create table resources_tag_map
(
    r_id   int not null,
    tag_id int not null,
    primary key (r_id, tag_id),
    constraint resources_tag_map_ibfk_1
        foreign key (r_id) references resources (r_id)
            on update cascade on delete cascade,
    constraint resources_tag_map_ibfk_2
        foreign key (tag_id) references tag (tag_id)
            on update cascade on delete cascade
)
    charset = utf8mb3
    row_format = DYNAMIC;

create index tag_id
    on resources_tag_map (tag_id);

create index tag_id
    on tag (tag_id);

create table user_token
(
    token    varchar(500) not null comment '存放令牌的地方',
    user_id  int          not null comment '请求用户ID',
    die_time datetime     not null comment '令牌过期时间(以这个为准而不是令牌过期时间)',
    type     int          null comment '令牌用途',
    token_id bigint auto_increment,
    primary key (token_id, user_id),
    constraint token
        unique (token)
)
    row_format = DYNAMIC;

create table users
(
    user_id                int auto_increment comment '自增的自然主键'
        primary key,
    user_name              varchar(31)                                                                                                  not null comment '用来称呼用户的名称',
    user_name_login        varchar(15)                                                                      default ''                  not null comment '用户用来登陆的名称',
    user_password          text                                                                                                         not null comment '密码(这里将会存放加密后的密码)',
    Coin                   int                                                                              default 0                   null comment '用户的积分/硬币,这个是用来限制用户使用本地服务器下载使用的',
    mailbox                varchar(255)                                                                                                 not null comment '邮箱',
    bio                    text                                                                                                         null comment '用户的简介',
    user_head_portrait_url varchar(64)                                                                                                  null comment '用户头像,命名统一为user_id.*(默认为default.jpeg)',
    status                 enum ('ok', 'blacklist', 'frozen', 'banned', 'is disabled', 'Not authenticated') default 'Not authenticated' null comment '用户的账号状态,当前有:
ok:正常  blacklist:被拉黑  frozen:冻结(用户手动)  bnanner:被封号力(管理员操作) is disabled :不可用(因为违反一些规则导致的)Not authenticated:注册了没有验证邮箱',
    sex                    varchar(15)                                                                                                  null comment '用户性别',
    jurisdiction           int                                                                              default 2                   null comment '权限级别:1-10,10为root,9为用户管理,0为游客,1
未验证用户,3为普通用户,4,为高级用户,5为资源管理,
所有用户均可编辑自己上传的链接资源
未说明的后面按照需要添加(高权限有低级权限的所有权限)(如果后面太多可以将其作为一个字典表的主键使用)
',
    birthday               date                                                                                                         null comment '生日默认为1970-01-01',
    register_time          datetime                                                                                                     null on update CURRENT_TIMESTAMP comment '注册时间',
    user_pe                varchar(15)                                                                                                  null comment '手机号',
    constraint id
        unique (user_id),
    constraint `user login name`
        unique (user_name_login),
    constraint `user mail`
        unique (mailbox),
    constraint `user pe`
        unique (user_pe)
)
    row_format = DYNAMIC;

create table comment
(
    comment_id     bigint auto_increment
        primary key,
    comment        text     null comment '内容',
    comment_user   int      not null comment '评论用户',
    comment_time   datetime null comment '评论时间',
    father_comment bigint   null comment '父评论,如果为0则是顶置',
    comment_r_id   int      null comment '评论位置',
    constraint comment_ibfk_1
        foreign key (comment_r_id) references resources (r_id)
            on update cascade on delete cascade,
    constraint comment_ibfk_2
        foreign key (comment_user) references users (user_id)
            on update cascade on delete cascade,
    constraint comment_ibfk_3
        foreign key (father_comment) references comment (comment_id)
            on update cascade on delete cascade
)
    charset = utf8mb3
    row_format = DYNAMIC;

create index comment_r
    on comment (comment_r_id);

create index comment_user
    on comment (comment_user);

create index father_comment
    on comment (father_comment);

create table feedback
(
    text  text                                                                               not null comment '反馈内容',
    u_id  int                                                                                not null,
    state enum ('正在处理', '处理完成', '等待处理')                                          null comment '状态',
    type  enum ('bug', 'UI错误', '请求收录新的资源', '资源链接错误', '账户问题', '其他问题') null,
    constraint feedback_ibfk_1
        foreign key (u_id) references users (user_id)
            on update cascade on delete cascade
)
    charset = utf8mb3
    row_format = DYNAMIC;

create index feedback_User
    on feedback (u_id);

create table fen
(
    r_id    int not null,
    user_id int null,
    constraint fen_ibfk_1
        foreign key (r_id) references resources (r_id)
            on update cascade on delete cascade,
    constraint fen_ibfk_2
        foreign key (user_id) references users (user_id)
            on update cascade on delete cascade
)
    charset = utf8mb3
    row_format = DYNAMIC;

create index fen_user
    on fen (r_id);

create index user_id
    on fen (user_id);

create table friend_map
(
    user_id             int              not null comment '用户',
    friend_id           int              not null comment '关注的用户',
    friend_confirmation bit default b'0' null comment '是否被确认',
    initiate_user       int              null comment '发起用户',
    primary key (user_id, friend_id),
    constraint friend_map_ibfk_1
        foreign key (user_id) references users (user_id)
            on update cascade on delete cascade,
    constraint friend_map_ibfk_2
        foreign key (friend_id) references users (user_id)
            on update cascade on delete cascade
)
    row_format = DYNAMIC;

create index friend_id
    on friend_map (friend_id);

create table links
(
    link         text                           null comment '链接本体',
    link_up_user int                            null comment '链接上传者',
    link_r       int                            null comment '链接指向的资源',
    link_state   enum ('ok', 'instable', 'die') null comment '链接状态,在,炸了,不稳定',
    links_public enum ('yes', 'no')             null comment '是否是官方上传',
    up_time      datetime                       null comment '链接提供时间',
    notes        text                           null comment '注释信息/使用注意事项',
    link_pointer varchar(15)                    null comment '链接指向',
    link_id      bigint auto_increment
        primary key,
    constraint links_ibfk_1
        foreign key (link_r) references resources (r_id)
            on update cascade on delete cascade,
    constraint links_ibfk_2
        foreign key (link_up_user) references users (user_id)
            on update cascade on delete cascade
)
    row_format = DYNAMIC;

create index rid
    on links (link_r);

create index userid
    on links (link_up_user);

create table session
(
    session_id      bigint auto_increment
        primary key,
    status          bit         null comment '当前状态,在,不在,不可用(原因为用户状态)',
    user_id         int         not null comment '对应用户',
    last_login_time datetime    null comment '上次登录时间',
    last_login_IP   varchar(16) null comment '上次登录的IP地址',
    constraint session_ibfk_1
        foreign key (user_id) references users (user_id)
            on update cascade on delete cascade
)
    row_format = DYNAMIC;

create index user
    on session (user_id);

create table user_download
(
    r_id      int                           null,
    user_id   int                           not null,
    time      datetime                      null,
    d_type    varchar(5) default '外部链接' null,
    file_name varchar(255)                  null,
    constraint user_download_ibfk_1
        foreign key (r_id) references resources (r_id)
            on update cascade on delete cascade,
    constraint user_download_ibfk_2
        foreign key (user_id) references users (user_id)
            on update cascade on delete cascade
)
    row_format = DYNAMIC;

create index rid
    on user_download (r_id);

create index userid
    on user_download (user_id);

create table user_resource_repository
(
    user_id         int      not null comment '获取资源的用户ID',
    r_id            int      null comment '被获取的资源',
    has_down        bit      null comment '本地下载权限',
    expiration_time datetime null comment '保存信息过期时间,默认为24小时',
    get_time        datetime null comment 's',
    has_link        bit      null comment '是否有外部链接获取权限',
    constraint user_resource_repository_ibfk_1
        foreign key (user_id) references users (user_id)
            on update cascade on delete cascade,
    constraint user_resource_repository_ibfk_2
        foreign key (r_id) references resources (r_id)
            on update cascade on delete cascade
)
    row_format = DYNAMIC;

create index r_id
    on user_resource_repository (r_id);

create index user_id
    on user_resource_repository (user_id);

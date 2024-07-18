/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

create table agent_info
(
    id                    bigint unsigned auto_increment comment '序号' primary key,
    agent_id              varchar(255)    							not null comment 'Agent ID',
    user_id               bigint unsigned                           not null comment '用户ID',
    name                  varchar(255)                              not null comment '名称',
    details               varchar(512)                              not null comment '描述',
    configs				  text                                      null comment '配置信息(JSON)',
    status                int             default 0                 null comment '0.正常 1.删除',
    created               timestamp       default CURRENT_TIMESTAMP not null comment '创建时间',
    updated               timestamp       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    check (json_valid(`configs`)),
    unique key `idx_agent_id` (`agent_id`)
) comment '智能体信息' collate = utf8mb4_bin;

create table session_info
(
    id              bigint unsigned auto_increment primary key,
    session_id      varchar(127)                           not null comment '会话ID',
    user_id         bigint unsigned                        not null comment '用户ID',
    session_name    varchar(4000)                          null comment '名称',
    status          int          default 0                 null comment '0.正常 1.删除',
    created         timestamp       default CURRENT_TIMESTAMP not null comment '创建时间',
    updated         timestamp       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    agent_id        varchar(10)  default '001'             not null comment 'Agent ID',
    unique key `idx_session_id` (`session_id`)
) comment '对话信息' collate = utf8mb4_bin;

create table chat_info
(
    id                bigint unsigned auto_increment primary key,
    session_id        varchar(127)                           not null comment '会话ID',
    chat_id       	  varchar(127)                           not null comment '对话ID',
    user_id           bigint unsigned                        not null comment '用户ID',
    question          text                                   null comment '问题',
    answer            mediumtext                             null comment '答案',
    status            int          default 0                 null comment '0.正常 1.删除',
    updated           timestamp                              null comment '创建时间',
    created           timestamp    default CURRENT_TIMESTAMP not null comment '更新时间',
    unique key `idx_chat_id` (`chat_id`),
    index `idx_session_chat_id` (`session_id`, `chat_id`)
) comment '一条对话信息' collate = utf8mb4_bin;

create table chat_his_info
(
    id                bigint unsigned auto_increment primary key,
    session_id        varchar(127)                           not null comment '会话ID',
    chat_id       	  varchar(127)                           not null comment '对话ID',
    chat_his_id       varchar(127)                           not null comment '对话历史ID',
    user_id           bigint unsigned                        not null comment '用户ID',
    question          text                                   null comment '问题',
    answer            mediumtext                             null comment '答案',
    status            int          default 0                 null comment '0.正常 1.删除',
    updated           timestamp                              null comment '创建时间',
    created           timestamp    default CURRENT_TIMESTAMP not null comment '更新时间',
    unique key `idx_chat_his_id` (`chat_his_id`),
    index `idx_session_chat_id` (`session_id`, `chat_id`)
) comment '一条对话重复提问历史' collate = utf8mb4_bin;
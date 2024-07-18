# Ai Model Hub

一个模型应用开发集成框架，对接各大模型厂商OPEN API，将数据转换成统一格式，可进行模型快速切换、prompt构建、知识库调用、数据存储等功能，加速模型应用的落地开发。

## 快速开始

### 环境要求

描述运行和开发此项目所需的环境和依赖项。

#### JDK

- [OpenJDK 17](https://openjdk.java.net/projects/jdk/17/) 或更高版本

#### 构建工具

- [Maven 3.6.3](https://maven.apache.org/download.cgi) 或更高版本

#### 依赖项

项目中使用的主要库和框架：

- Spring Boot 3.2.6
- MySQL Connector 8.0.33

#### 依赖系统

- MySQL 8.0.27+
- Redis 6.2.6+

#### 环境变量

- export CHATGPT_KEY=your_chatgpt_key
- export DOUBAO_KEY=your_doubao_key

#### 请求实例

```shell
# 使用agent区分不同模型调用
curl --location 'http://127.0.0.1:8080/ai-model-hub/ai/stream/chat' \
--header 'Content-Type: application/json' \
--data '{
    "agent": "1",
    "content": "介绍一下军博，使用20个汉字以内"
}'
```

## 架构图

<div style="text-align: center; margin-top: 20px; margin-bottom: 20px;">
    <img src="https://github.com/Jindou2018/image/raw/master/ai-model-hub/%E6%9E%B6%E6%9E%842024-07-08-17-54-29.png" alt="Database Image" style="width: 50%;">
</div>

### 模块

- Controller 对接前端协议，实现流式打字机效果，如：stream/sse/wc
- Service 处理通用功能
  - ID生成
  - 调用模型处理流程
  - 模型厂商匹配调用
- Process
  - Up 处理调用模型前的操作
    - 模型厂商、模型名称匹配选择
    - 查询知识库
    - 三方接口调用
    - 构建Prompt
  - Down 处理调用模型后的操作
    - 保存问题和答案
- Model
  - Actuator 通用参数化转化为具体模型厂商参数化，调用模型
  - Listener 将模型的返回数据转换为通用响应数据，回写前端
- Client
  - 具体对接模型厂商的接口，如：stream/sse/wc

### 项目结构

```
ai-model-hub/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── org/
│       │       └── salt/
│       │           └── ai/
│       │               └── hub/
│       │                   ├── models.ai/
│       │                   │	├── aliyun/
│       │                   │	├── chatgpt/
│       │                   │	│   ├── dto
│       │                   │	│       ├── ChatGPTRequest.java
│       │                   │	│       └── ChatGPTResponse.java
│       │                   │	│   ├── ChatGPTActuator.java
│       │                   │	│   └── ChatGPTListener.java
│       │                   │	├── doubao/
│       │                   │	├── moonshot/
│       │                   │	└── enums/
│       │                   │	    └── VendorType.java
│       │                   ├── chat/
│       │                   │	├── controller/
│       │                   │	│   ├── ChatSseController.java
│       │                   │	│   └── ChatStreamController.java
│       │                   │	├── process/
│       │                   │	│   └── SimpleContextProcess.java
│       │                   │	└── service/
│       │                   │	    └── ChatService.java
│       │                   ├── data/
│       │                   └── frame/
│       │                       ├── chat/
│       │                       │   ├── client/
│       │                       │   │   └── stream/
│       │                       │   │       └── HttpStreamClient.java 
│       │                       │   ├── front/
│       │                       │   │   ├── sse/
│       │                       │   │       └── SseResponse.java 
│       │                       │   │   └── stream/
│       │                       │   │       └── StreamResponse.java 
│       │                       │   ├── model/
│       │                       │   │   ├── AiChatActuator.java 
│       │                       │   │   ├── ListenerStrategy.java
│       │                       │   │   └── DoListener.java
│       │                       │   ├── process/
│       │                       │   │   ├── ChatProcess.java 
│       │                       │   └── structs/
│       │                       ├── config/
│       │                       └── utils/
│       └── resources/
│           └── application.yml
│           └── application-dev.yml
│           └── logback.xml
│
├── pom.xml
├── README.md
├── README_CN.md
├── LICENSE
└── .gitignore

```

## 功能列表

- 对接部分厂商

### 当前支持模型

- ChatGPT
- DouBao
- QWen(aliyun)
- Moonshot
- Ollama

## 存储的作用
框架添加存储主要作用是：

- 存储Agent配置，框架可以通过前端传递的Agent信息，在DB中获取具体的Agent配置，包括模型提供厂商、及具体模型等，当然你可以在框架中扩展你需要的配置信息，如：Agent System Promet、问候语等。
- 存储聊天上下文，可以在每次回答后存储具体的问题和答案内容，并在下次提问是自动构建到模型请求中。

## 设计思路
框架实现最简设计，共4张表，如下图：

- Agent表，存储Agent配置，具体配置使用JSON字段，便于扩展。
- Session表，存储一次对话信息，包含多次对问答，关联一条Agent记录。
- Chat表，存储一次问答，包括问题及答案，关联一条Session记录。
- Chat_His表，存储一次问答历史，用于重答时记录问题答案历史。

<div style="text-align: center; margin-top: 20px; margin-bottom: 20px;">
  <img src="https://github.com/Jindou2018/image/raw/master/ai-model-hub/db.png" alt="Database Image" style="width: 50%;">
</div>

注：用户字段框架默认为1，根据实际情况扩展

## 具体表设计
```
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
```
## 初始化Agent配置
```
INSERT INTO ai_model_hub.agent_info (agent_id, user_id, name, details, configs) VALUES ('1', 1, 'chatgpt', 'chatgpt demo', '{"vendor":"chatgpt","model":"gpt-3.5-turbo"}');
INSERT INTO ai_model_hub.agent_info (agent_id, user_id, name, details, configs) VALUES ('2', 1, 'doubao', 'doubao demo', '{"vendor":"doubao","model":"ep-20240611104225-2d4ww"}');
INSERT INTO ai_model_hub.agent_info (agent_id, user_id, name, details, configs) VALUES ('3', 1, 'aliyun', 'aliyun demo', '{"vendor":"aliyun","model":"qwen-max"}');
INSERT INTO ai_model_hub.agent_info (agent_id, user_id, name, details, configs) VALUES ('4', 1, 'moonshot', 'moonshot demo', '{"vendor":"moonshot","model":"moonshot-v1-8k"}');
INSERT INTO ai_model_hub.agent_info (agent_id, user_id, name, details, configs) VALUES ('5', 1, 'ollama', 'ollama demo', '{"vendor":"ollama","model":"llama3:8b"});
```
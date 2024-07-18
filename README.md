# AI Model Hub

An application development integration framework for AI models, which connects to various model providers' OPEN APIs, converting data into a unified format. It facilitates quick model switching, prompt construction, knowledge base utilization, data storage, and more, accelerating the development of model applications.

## Quick Start

### Environment Requirements

Describe the environment and dependencies needed to run and develop this project.

#### JDK

- [OpenJDK 17](https://openjdk.java.net/projects/jdk/17/) or higher

#### Build Tool

- [Maven 3.6.3](https://maven.apache.org/download.cgi) or higher

#### Dependencies

Main libraries and frameworks used in the project:

- Spring Boot 3.2.6
- MySQL Connector 8.0.33

#### Dependency System

- MySQL 8.0.27+
- Redis 6.2.6+

#### Environment Variables

- export CHATGPT_KEY=your_chatgpt_key
- export DOUBAO_KEY=your_doubao_key

#### Request Example

```shell
# Use agent to distinguish different model calls
curl --location 'http://127.0.0.1:8080/ai-model-hub/ai/stream/chat' \
--header 'Content-Type: application/json' \
--data '{
    "agent": "1",
    "content": "Introduce the Military Museum in 20 Chinese characters or less"
}'
```

## Architecture diagram

<div style="text-align: center; margin-top: 20px; margin-bottom: 20px;">
    <img src="https://github.com/Jindou2018/image/raw/master/ai-model-hub/%E6%9E%B6%E6%9E%842024-07-08-17-54-29.png" alt="Database Image" style="width: 50%;">
</div>

### Modules
- Controller connects to the front-end protocol to achieve the streaming typewriter effect, such as: stream/sse/wc
- Service handles general functions
  - ID generation
  - Call model processing flow
  - Model vendor matching call
- Process
  - Up handles operations before calling the model
    - Model vendor, model name matching selection
    - Query knowledge base
    - Three-party interface call
    - Build prompt
  - Down handles operations after calling the model
    - Save questions and answers
- Model
  - Actuator converts general parameterization into specific model vendor parameterization and calls the model
  - Listener converts the model's return data into general response data and writes it back to the front-end
- Client
  - Specific interface to connect to the model vendor, such as: stream/sse/wc

### Project Structure

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

### Currently Supported Models

- ChatGPT
- DouBao
- QWen (aliyun)
- Moonshot
- Ollama

## DB Design

### Purpose of Storage
The main purposes of adding storage to the framework are:

- **Storing Agent Configuration**: The framework can retrieve specific Agent configurations from the database based on the Agent information provided through the frontend. This includes the model provider, the specific model, and any other configurable details such as Agent System Prompt, greetings, etc.
- **Storing Chat Context**: The framework can store specific questions and answers after each response and automatically construct these into the model request during subsequent questions.

### Design Concept
The framework implements a minimal design with four tables, as shown in the diagram below:

- **Agent Table**: Stores Agent configurations with specific configurations using JSON fields for easy extension.
- **Session Table**: Stores information about a conversation, including multiple question-and-answer exchanges, linked to an Agent record.
- **Chat Table**: Stores individual question-and-answer exchanges, linked to a Session record.
- **Chat_His Table**: Stores the history of question-and-answer exchanges for retry purposes.

<div style="text-align: center; margin-top: 20px; margin-bottom: 20px;">
  <img src="https://github.com/Jindou2018/image/raw/master/ai-model-hub/db.png" alt="Database Image" style="width: 50%;">
</div>

Note: The framework defaults the user field to 1 and can be extended as needed.

### Detailed Table Design
```
create table agent_info
(
    id                    bigint unsigned auto_increment comment 'ID' primary key,
    agent_id              varchar(255) not null comment 'Agent ID',
    user_id               bigint unsigned not null comment 'User ID',
    name                  varchar(255) not null comment 'Name',
    details               varchar(512) not null comment 'Description',
    configs               text null comment 'Configuration Info (JSON)',
    status                int default 0 null comment '0. Active 1. Deleted',
    created               timestamp default CURRENT_TIMESTAMP not null comment 'Creation Time',
    updated               timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update Time',
    check (json_valid(`configs`)),
    unique key `idx_agent_id` (`agent_id`)
) comment 'Agent Information' collate = utf8mb4_bin;

create table session_info
(
    id              bigint unsigned auto_increment primary key,
    session_id      varchar(127) not null comment 'Session ID',
    user_id         bigint unsigned not null comment 'User ID',
    session_name    varchar(4000) null comment 'Name',
    status          int default 0 null comment '0. Active 1. Deleted',
    created         timestamp default CURRENT_TIMESTAMP not null comment 'Creation Time',
    updated         timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update Time',
    agent_id        varchar(10) default '001' not null comment 'Agent ID',
    unique key `idx_session_id` (`session_id`)
) comment 'Session Information' collate = utf8mb4_bin;

create table chat_info
(
    id                bigint unsigned auto_increment primary key,
    session_id        varchar(127) not null comment 'Session ID',
    chat_id           varchar(127) not null comment 'Chat ID',
    user_id           bigint unsigned not null comment 'User ID',
    question          text null comment 'Question',
    answer            mediumtext null comment 'Answer',
    status            int default 0 null comment '0. Active 1. Deleted',
    updated           timestamp null comment 'Update Time',
    created           timestamp default CURRENT_TIMESTAMP not null comment 'Creation Time',
    unique key `idx_chat_id` (`chat_id`),
    index `idx_session_chat_id` (`session_id`, `chat_id`)
) comment 'Chat Information' collate = utf8mb4_bin;

create table chat_his_info
(
    id                bigint unsigned auto_increment primary key,
    session_id        varchar(127) not null comment 'Session ID',
    chat_id           varchar(127) not null comment 'Chat ID',
    chat_his_id       varchar(127) not null comment 'Chat History ID',
    user_id           bigint unsigned not null comment 'User ID',
    question          text null comment 'Question',
    answer            mediumtext null comment 'Answer',
    status            int default 0 null comment '0. Active 1. Deleted',
    updated           timestamp null comment 'Update Time',
    created           timestamp default CURRENT_TIMESTAMP not null comment 'Creation Time',
    unique key `idx_chat_his_id` (`chat_his_id`),
    index `idx_session_chat_id` (`session_id`, `chat_id`)
) comment 'Chat History Information' collate = utf8mb4_bin;
```
### Initializing Agent Configurations

```
INSERT INTO ai_model_hub.agent_info (agent_id, user_id, name, details, configs) VALUES ('1', 1, 'chatgpt', 'chatgpt demo', '{"vendor":"chatgpt","model":"gpt-3.5-turbo"}');
INSERT INTO ai_model_hub.agent_info (agent_id, user_id, name, details, configs) VALUES ('2', 1, 'doubao', 'doubao demo', '{"vendor":"doubao","model":"ep-20240611104225-2d4ww"}');
INSERT INTO ai_model_hub.agent_info (agent_id, user_id, name, details, configs) VALUES ('3', 1, 'aliyun', 'aliyun demo', '{"vendor":"aliyun","model":"qwen-max"}');
INSERT INTO ai_model_hub.agent_info (agent_id, user_id, name, details, configs) VALUES ('4', 1, 'moonshot', 'moonshot demo', '{"vendor":"moonshot","model":"moonshot-v1-8k"}');
INSERT INTO ai_model_hub.agent_info (agent_id, user_id, name, details, configs) VALUES ('5', 1, 'ollama', 'ollama demo', '{"vendor":"ollama","model":"llama3:8b"}');
```
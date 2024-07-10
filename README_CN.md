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

![image](https://github.com/Jindou2018/image/raw/master/ai-model-hub/%E6%9E%B6%E6%9E%842024-07-08-15-04-31.png)

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


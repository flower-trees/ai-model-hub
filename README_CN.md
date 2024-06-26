# Ai Model Hub

一个模型应用开发集成框架，对接各大模型厂商OPEN API，将数据转换成统一格式，可进行模型快速切换、prompt构建、知识库调用、数据存储等功能，加速模型应用的落地开发。

## 当前支持模型

- ChatGPT
- DouBao
- QWen (建设中)
- Claude3 (建设中)
- KIMI (建设中)
- 百川 (建设中)
- 智谱 (建设中)
- 文心一言 (建设中)

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

## 架构设计



## 项目结构



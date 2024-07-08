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

![image](https://github.com/Jindou2018/image/raw/master/ai-model-hub/%E6%9E%B6%E6%9E%842024-07-08-17-54-29.png)

## Feature List

### Currently Supported Models

- ChatGPT
- DouBao
- QWen (aliyun)
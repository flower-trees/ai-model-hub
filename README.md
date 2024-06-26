# AI Model Hub

A framework for developing and integrating model applications, connecting with major model providers' open APIs. It converts data into a unified format, allowing for quick model switching, prompt construction, knowledge base calling, data storage, and more, accelerating the development of model applications.

## Supported Models

- ChatGPT
- DouBao
- QWen (in progress)
- Claude3 (in progress)
- Gemini (in progress)
- KIMI (in progress)
- BaiChuan (in progress)
- Zhipu (in progress)
- WenXin YiYan (in progress)

## Quick Start

### Environment Requirements

Describes the environment and dependencies required to run and develop this project.

#### JDK

- [OpenJDK 17](https://openjdk.java.net/projects/jdk/17/) or higher

#### Build Tools

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
# Use the agent parameter to distinguish between different model calls
curl --location 'http://127.0.0.1:8080/ai-model-hub/ai/stream/chat' \
--header 'Content-Type: application/json' \
--data '{
    "agent": "1",
    "content": "Introduce the Military Museum in 20 characters or less."
}'
```
# Aiany 项目

## 项目概述
Aiany 是一个 Java 项目，用于与 OpenAI 和 Azure OpenAI 服务进行交互。


## 依赖管理
项目使用 Maven 进行依赖管理，主要依赖包括：
- **Retrofit2**：用于构建 RESTful API 客户端。
- **OkHttp3**：用于 HTTP 请求和响应处理。
- **Gson 和 Jackson**：用于 JSON 数据的序列化和反序列化。
- **Lombok**：用于简化 Java 代码的编写。
- **Azure AI OpenAI**：用于与 Azure OpenAI 服务进行交互。
- **Jtokkit**：用于计算文本的 token 数量。

### 版本管理
项目使用属性文件来管理依赖的版本，例如：
```xml:pom.xml
<properties>
    <java.version>17</java.version>
    <aiany.version>0.0.1</aiany.version>
    <lombok.version>1.18.30</lombok.version>
    <slf4j-api.version>2.0.16</slf4j-api.version>
    <gson.version>2.10.1</gson.version>
    <bytebuddy.version>1.15.10</bytebuddy.version>
    <opensearch-java.version>2.9.0</opensearch-java.version>
    <jackson.version>2.16.1</jackson.version>
    <github-api.version>1.318</github-api.version>
    <netty.version>4.1.111.Final</netty.version>
    <retrofit.version>2.9.0</retrofit.version>
</properties>
```

## 构建和运行
### 构建项目
在项目根目录下，使用以下命令构建项目：
```bash
mvn clean install
```

# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.achui.navtive-image' is invalid and this project uses 'com.achui.navtive_image' instead.

# Spring Boot 文件处理服务

这是一个基于 Spring Boot 3、JDK 21 和 GraalVM Native Image 技术开发的文件处理服务，支持从SFTP服务器或API读取不同格式的文件（CSV、XML等），进行数据处理和转换，并将结果上传到SFTP、API或S3。

## 项目功能

1. 支持从SFTP读取CSV、XML格式文件
2. 支持SFTP账号密码和证书两种认证方式
3. 支持文件内容的数据清洗和转换
4. 使用模板引擎进行数据格式转换
5. 支持将处理后的数据转换为JSON、CSV、XML等格式
6. 支持将转换后的数据上传到SFTP或发送到API
7. 提供REST API接口和定时任务功能

## 如何运行

### 前提条件

*   Java 21
*   Maven
*   Docker (用于 Docker 部署)
*   GraalVM (用于本地 Native Image 构建)

### 本地运行 (JAR)

1.  构建 JAR 包:
    ```bash
    ./mvnw clean package
    ```
2.  运行 JAR 包:
    ```bash
    java -jar target/navtive-image-0.0.1-SNAPSHOT.jar
    ```

### 本地运行 (Native Image)

1.  构建 Native Image:
    ```bash
    ./mvnw native:compile -Pnative -DskipTests
    ```
2.  运行 Native Image:
    ```bash
    target/navtive-image
    ```

### Docker 部署

1.  构建 Docker 镜像:
    ```bash
    docker build -t navtive-image-app .
    ```
2.  运行 Docker 容器:
    ```bash
    docker run -p 8080:8080 navtive-image-app
    ```

## API 端点

### 文件处理接口
```bash
POST /api/v1/file-processing/process

请求体示例：
{
  "sourceSftp": {
    "host": "source.example.com",
    "port": 22,
    "username": "user",
    "password": "password"
  },
  "sourcePath": "/path/to/source.csv",
  "sourceFormat": "csv",
  "targetSftp": {
    "host": "target.example.com",
    "port": 22,
    "username": "user",
    "privateKeyPath": "/path/to/private.key",
    "passphrase": "passphrase"
  },
  "targetPath": "/path/to/target.json",
  "targetFormat": "json",
  "templateName": "csv-to-json.ftl",
  "params": {
    "param1": "value1"
  }
}
```

### 健康检查接口
- `GET /actuator/health` - 查看应用健康状态
- `GET /actuator/metrics` - 查看应用指标

### Additional Links
These additional references should also help you:

* [Configure AOT settings in Build Plugin](https://docs.spring.io/spring-boot/3.5.0/how-to/aot.html)

## GraalVM Native Support

This project has been configured to let you generate either a lightweight container or a native executable.
It is also possible to run your tests in a native image.

### Lightweight Container with Cloud Native Buildpacks
If you're already familiar with Spring Boot container images support, this is the easiest way to get started.
Docker should be installed and configured on your machine prior to creating the image.

To create the image, run the following goal:

```
$ ./mvnw spring-boot:build-image -Pnative
```

Then, you can run the app like any other container:

```
$ docker run --rm navtive-image:0.0.1-SNAPSHOT
```

### Executable with Native Build Tools
Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

NOTE: GraalVM 22.3+ is required.

To create the executable, run the following goal:

```
$ ./mvnw native:compile -Pnative
```

Then, you can run the app as follows:
```
$ target/navtive-image
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

To run your existing tests in a native image, run the following goal:

```
$ ./mvnw test -PnativeTest
```


### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.


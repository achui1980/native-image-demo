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

## GraalVM 原生镜像配置

为了成功将此项目编译为 GraalVM 原生镜像，需要处理由反射（Reflection）等动态特性带来的挑战。

### Reachability Metadata (可达性元数据)

原生镜像的构建过程需要提前知道所有在运行时可能被访问的类、方法和资源。对于像 JSch (SFTP) 和 Spring Boot 配置绑定 (`@ConfigurationProperties`) 这样大量使用反射的库，我们需要手动提供元数据。

这些元数据配置文件位于 `src/main/resources/META-INF/native-image/` 目录下：

-   `reflect-config.json`: 声明了需要通过反射访问的类、方法和字段。我们为 JSch 和 `TaskProperties` 配置类添加了相关条目。

当您添加新的依赖或编写了使用反射的代码时，可能需要更新这些文件。

### 使用追踪代理自动采集元数据

如果遇到 `ClassNotFoundException` 或类似的反射错误，最推荐的方法是使用 GraalVM 的追踪代理来自动生成所需的配置。

请按以下步骤操作：

1.  **构建常规的 JAR 包**
    首先，构建一个标准的可执行JAR文件（不使用 `native` profile）。
    ```bash
    mvn clean package
    ```

2.  **使用 Agent 运行程序**
    在项目根目录运行以下命令，启动程序并挂载追踪代理。代理会将收集到的配置直接输出到正确的源码目录。
    ```bash
    java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image -jar target/navtive-image-0.0.1-SNAPSHOT.jar
    ```

3.  **完整测试应用功能**
    在程序运行期间，请务必通过API或其他方式，完整地触发应用的所有功能，特别是那些之前导致错误的功能。代理只会记录被实际执行到的代码路径。

4.  **停止程序并生成配置**
    完成测试后，通过 `Ctrl+C` 停止应用程序。代理会自动在 `src/main/resources/META-INF/native-image/` 目录下创建或合并 `reflect-config.json` 等配置文件。

5.  **重新构建原生镜像**
    最后，使用新的元数据重新构建原生镜像。
    ```bash
    mvn -Pnative clean package
    ```

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


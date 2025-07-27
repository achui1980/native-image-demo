# 使用 GraalVM JDK 作为构建环境，进行 Native Image 构建
FROM oracle/graalvm-ce:21 as builder

# 设置工作目录
WORKDIR /app

# 复制 Maven Wrapper 相关文件
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# 下载 Maven 依赖 (利用 Docker 缓存)
RUN ./mvnw dependency:go-offline

# 复制源代码
COPY src ./src

# 构建 Native Image
# 注意：确保 pom.xml 中已配置 native-maven-plugin
RUN ./mvnw native:compile -Pnative -DskipTests

# 使用一个轻量级的基础镜像来运行 Native Image
FROM alpine:latest

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 Native Image
# Native Image 的名称通常是 pom.xml 中的 artifactId
COPY --from=builder /app/target/navtive-image .

# 暴露应用程序端口 (与 Spring Boot 默认端口一致)
EXPOSE 8080

# 运行 Native Image
ENTRYPOINT ["./navtive-image"]
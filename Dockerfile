# 阶段1：Maven 构建 JAR（构建上下文为 daming-admin 根目录）
FROM maven:3.8.6-eclipse-temurin-8-alpine AS builder
WORKDIR /build

# 使用国内 Maven 镜像，避免连 Maven Central 出现 handshake_failure / connection reset
COPY .m2-docker-settings.xml /root/.m2/settings.xml

# 先复制所有 pom，便于依赖层缓存
COPY pom.xml .
COPY ruoyi-admin/pom.xml ruoyi-admin/
COPY ruoyi-common/pom.xml ruoyi-common/
COPY ruoyi-framework/pom.xml ruoyi-framework/
COPY ruoyi-system/pom.xml ruoyi-system/
COPY ruoyi-quartz/pom.xml ruoyi-quartz/
COPY ruoyi-generator/pom.xml ruoyi-generator/
COPY dm_questionBank/pom.xml dm_questionBank/

RUN mvn dependency:go-offline -B -q || true

# 再复制源码并打包
COPY . .
RUN mvn clean package -DskipTests -B -q -pl ruoyi-admin -am

# 阶段2：运行
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

RUN apk add --no-cache tzdata && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone

COPY --from=builder /build/ruoyi-admin/target/ruoyi-admin.jar app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=druid,docker -jar app.jar"]

EXPOSE 8080

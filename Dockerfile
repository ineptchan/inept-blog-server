# ---------- build stage ----------
FROM gradle:8-jdk17 AS build
WORKDIR /app

# 先拷贝构建描述文件，利用缓存加速
COPY build.gradle.kts settings.gradle.kts gradlew gradlew.bat ./
COPY gradle ./gradle
RUN chmod +x ./gradlew

# 再拷贝源码
COPY . .

# 打包 Spring Boot jar（跳过测试可加快：-x test）
RUN sh ./gradlew bootJar -x test --no-daemon

# ---------- runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# 把打出来的 jar 拷贝进来（文件名用通配符避免写死版本号）
COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

# 可选：通过 JAVA_OPTS 传 JVM 参数
ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

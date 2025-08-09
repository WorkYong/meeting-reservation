# ----- Build stage -----
FROM gradle:8.7-jdk21 AS build
WORKDIR /workspace

# Gradle 메타 먼저 복사(캐시 최적화)
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# gradlew 실행권한 부여 (복사 후!)
RUN chmod +x gradlew

# (선택) 의존성 캐싱 워밍업 — 이후 빌드 빨라짐
# RUN ./gradlew --version

# 소스 복사
COPY src src

# 컨테이너 안에서 JAR 빌드
RUN ./gradlew clean bootJar --no-daemon

# ----- Run stage -----
FROM openjdk:21
WORKDIR /app
COPY --from=build /workspace/build/libs/wiseaiMeetingApi.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
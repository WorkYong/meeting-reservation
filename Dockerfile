FROM openjdk:21

# wiseaiMeetingApi.jar 파일 복사
COPY build/libs/wiseaiMeetingApi.jar /wiseaiMeetingApi.jar

EXPOSE 8080

# 실행 시 wiseaiMeetingApi.jar 파일 실행
ENTRYPOINT ["java", "-jar", "/wiseaiMeetingApi.jar"]

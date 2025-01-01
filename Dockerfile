# OpenJDK 17을 사용하는 공식 이미지를 기반으로 합니다.
FROM openjdk:17-jdk-slim

# JAR 파일을 Docker 이미지로 복사합니다.
COPY build/libs/danjam-backend-1.0.0.jar /danjam-backend.jar

# 컨테이너 실행 시 JAR 파일을 실행합니다.
ENTRYPOINT ["java", "-jar", "/danjam-backend.jar"]

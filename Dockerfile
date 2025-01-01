# OpenJDK 17을 사용하는 공식 이미지를 기반으로 합니다.
FROM openjdk:17-jdk-slim

# JAR 파일을 Docker 이미지로 복사합니다.
COPY build/libs/danjam-1.0.0.jar /danjam-backend.jar

# MySQL 준비되기 전까지 대기
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# MySQL이 준비될 때까지 기다린 후 JAR 파일을 실행합니다.
ENTRYPOINT ["/wait-for-it.sh", "danjam-mysql:3306", "--", "java", "-jar", "/danjam-backend.jar"]
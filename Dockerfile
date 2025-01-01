## OpenJDK 17을 사용하는 공식 이미지를 기반으로 합니다.
#FROM openjdk:17-jdk-slim
#
## JAR 파일을 Docker 이미지로 복사합니다.
#COPY build/libs/danjam-1.0.0.jar /danjam-backend.jar
#
## 컨테이너 실행 시 JAR 파일을 실행합니다.
#ENTRYPOINT ["java", "-jar", "/danjam-backend.jar"]


# Build 단계
FROM openjdk:17-jdk-slim as build

WORKDIR /danjam-backend

# Gradle Wrapper 및 설정 파일 복사
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# gradlew에 실행 권한 부여
RUN chmod +x gradlew

# Gradle 종속성 캐시 생성 및 JAR 파일 빌드
RUN ./gradlew clean bootJar

# 배포 단계
FROM openjdk:17-jdk-slim

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar danjam-backend.jar

# 실행 명령
CMD ["java", "-jar", "danjam-backend.jar"]



## OpenJDK 17을 사용하는 공식 이미지를 기반으로 합니다.
#FROM openjdk:17-jdk-slim
#
## dockerize 바이너리를 다운로드하여 Docker 이미지에 포함
#RUN apt-get update && apt-get install -y wget && \
#    wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz && \
#    tar -xvzf dockerize-linux-amd64-v0.6.1.tar.gz && \
#    mv dockerize /usr/local/bin/ && \
#    chmod +x /usr/local/bin/dockerize && \
#    rm dockerize-linux-amd64-v0.6.1.tar.gz
#
## JAR 파일을 Docker 이미지로 복사
#COPY build/libs/danjam-1.0.0.jar /danjam-backend.jar
#
## MySQL 준비되기 전까지 대기
## dockerize 명령어로 MySQL이 준비될 때까지 기다립니다.
#ENTRYPOINT ["dockerize", "-wait", "tcp://danjam-mysql:3306", "-timeout", "30s", "java", "-jar", "/danjam-backend.jar"]

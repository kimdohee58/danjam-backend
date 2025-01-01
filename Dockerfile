## OpenJDK 17을 사용하는 공식 이미지를 기반으로 합니다.
#FROM openjdk:17-jdk-slim
#
## JAR 파일을 Docker 이미지로 복사합니다.
#COPY build/libs/danjam-1.0.0.jar /danjam-backend.jar
#
## 컨테이너 실행 시 JAR 파일을 실행합니다.
#ENTRYPOINT ["java", "-jar", "/danjam-backend.jar"]


# OpenJDK 17을 사용하는 공식 이미지를 기반으로 합니다.
FROM openjdk:17-jdk-slim as build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 및 설정 파일 복사 (캐시 활용)
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# Gradle 종속성 캐시 생성
RUN ./gradlew dependencies

# 프로젝트의 모든 파일 복사
COPY . .

# Gradle 빌드 실행 (clean 및 bootJar)
RUN ./gradlew clean bootJar

# 실제 실행용 이미지 생성
FROM openjdk:17-jdk-slim

# 실행용 사용자 생성
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar /danjam-backend.jar

# 사용자 설정
USER appuser

# 컨테이너 실행 시 JAR 파일 실행
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xms512m", "-Xmx1024m", "-jar", "/danjam-backend.jar"]



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

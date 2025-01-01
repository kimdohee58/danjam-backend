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

WORKDIR /back-main

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

WORKDIR /back-main

# 빌드된 JAR 파일 복사
COPY --from=build /back-main/build/libs/danjam-1.0.0.jar /back-main/danjam-backend.jar

# 실행 명령
CMD ["java", "-jar", "/back-main/danjam-backend.jar"]

FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :api:bootJar -x :api:generateJooq --no-daemon

FROM eclipse-temurin:21-jdk
WORKDIR /app

# 타임존을 한국 시간으로 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --from=build /app/api/build/libs/*.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","./app.jar"]
VOLUME ["/var/logback"]
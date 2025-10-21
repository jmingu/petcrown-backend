FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :api:bootJar --no-daemon

FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/api/build/libs/*.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","./app.jar"]
VOLUME ["/var/logback"]
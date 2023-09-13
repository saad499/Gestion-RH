FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar /app/gateway.jar

ENTRYPOINT ["java","-jar","/app/gateway.jar"]
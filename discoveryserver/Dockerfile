FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar /app/eureka.jar

ENTRYPOINT ["java","-jar","/app/eureka.jar"]
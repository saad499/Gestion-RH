FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar /app/employees.jar

ENTRYPOINT ["java","-jar","/app/employees.jar"]
@echo off
setlocal enabledelayedexpansion

echo Building the Spring Boot Application
call mvn clean package -DskipTests

echo Starting the Docker containers using envirolment file
docker-compose -f docker-compose-employees.yml up --build -d

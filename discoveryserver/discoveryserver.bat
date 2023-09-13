@echo off
setlocal enabledelayedexpansion

echo Building the Spring Boot Application
call mvn clean package -DskipTests
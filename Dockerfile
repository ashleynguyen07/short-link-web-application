# Stage 1: Build Spring Boot App
FROM openjdk:17-jdk-slim
COPY target/short-link-application-0.0.1-SNAPSHOT.jar application-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/application-0.0.1-SNAPSHOT.jar"]

# Stage 2: Build Nextjs App
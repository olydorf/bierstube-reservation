# Fetch the base java image
FROM openjdk:11-jdk-slim

# Install curl (required for health check)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

VOLUME /tmp

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the port
EXPOSE 8080

# For Spring Boot application, create an environment variable SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=docker

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]

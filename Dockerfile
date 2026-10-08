# SET09803 DevOps Application Dockerfile
# Purpose: Packages the Java application into an Amazon Corretto 17 image.
# Use official Amazon Corretto 17 JDK image as the base
FROM amazoncorretto:17
# Copy compiled bytecode classes into the container
COPY ./target/SET09803-2026-7-DevOps-Group-3-1.0-SNAPSHOT-jar-with-dependencies.jar /tmp/app.jar# Set working directory inside the container
WORKDIR /tmp
# Entrypoint command to launch the application
ENTRYPOINT ["java", "-jar", "app.jar"]
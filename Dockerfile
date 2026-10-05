# SET09803 DevOps Application Dockerfile
# Purpose: Packages the Java application into an Amazon Corretto 17 image.
# Use official Amazon Corretto 17 JDK image as the base
FROM amazoncorretto:17
# Copy compiled bytecode classes into the container
COPY ./target/classes/com/napier/devops /tmp/com/napier/devops
# Set working directory inside the container
WORKDIR /tmp
# Entrypoint command to launch the application
CMD ["java", "-cp", "/tmp", "com.napier.devops.App"]

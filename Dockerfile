FROM amazoncorretto:17
COPY ./target/classes/org/example /tmp/org/example
WORKDIR /tmp
ENTRYPOINT ["java", "org.example.Main"]
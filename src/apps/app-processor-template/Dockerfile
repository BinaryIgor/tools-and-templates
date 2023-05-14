FROM openjdk:17.0.1-jdk-slim

ENV TEMPLATES_DIR=/templates
ENV TZ=UTC

COPY target/templates /templates
COPY target/app-processor-template-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT java "${JVM_OPTS}" -jar app.jar
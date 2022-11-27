FROM openjdk:17.0.1-jdk-slim
COPY target/system-monitor-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT java "${JVM_OPTS}" -jar app.jar
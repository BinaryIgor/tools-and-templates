FROM eclipse-temurin:17-focal
COPY target/system-monitor-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT java "${JVM_OPTS}" -jar app.jar
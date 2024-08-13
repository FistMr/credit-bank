FROM openjdk:17-alpine
WORKDIR /app
COPY /target/statement-0.0.1-SNAPSHOT.jar /app/statement.jar
ENTRYPOINT ["java", "-jar", "statement.jar"]
EXPOSE 8083
FROM openjdk:17-alpine
WORKDIR /app
COPY /target/calculator-0.0.1-SNAPSHOT.jar /app/calculator.jar
ENTRYPOINT ["java", "-jar", "calculator.jar"]
EXPOSE 8081
FROM openjdk:17-alpine
WORKDIR /app
COPY /target/gateway-0.0.1-SNAPSHOT.jar /app/gateway.jar
ENTRYPOINT ["java", "-jar", "gateway.jar"]
EXPOSE 8080
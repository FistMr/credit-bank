FROM openjdk:17-alpine
WORKDIR /app
COPY /target/deal-0.0.1-SNAPSHOT.jar /app/deal.jar
ENTRYPOINT ["java", "-jar", "deal.jar"]
EXPOSE 8082
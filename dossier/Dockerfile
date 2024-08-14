FROM openjdk:17-alpine
WORKDIR /app
COPY /target/dossier-0.0.1-SNAPSHOT.jar /app/dossier.jar
ENTRYPOINT ["java", "-jar", "dossier.jar"]
EXPOSE 8084
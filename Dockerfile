FROM maven:3.5.0-alpine
MAINTAINER thibaut.mottet@pupscan.fr

WORKDIR /workspace
COPY . .
RUN mvn install

CMD ["java", "-Dspring.profiles.active=production", "-jar", "./target/crawler-0.0.1-SNAPSHOT.jar"]


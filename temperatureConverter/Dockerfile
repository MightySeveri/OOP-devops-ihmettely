FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .
COPY . .

RUN mvn clean test

CMD ["mvn", "test"]

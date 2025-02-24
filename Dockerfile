FROM maven:3.8.7-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src

RUN mvn clean package -DskipTests


# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/app.jar app.jar

EXPOSE 9090

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
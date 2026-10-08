# Build Spring Boot backend
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
RUN rm -f target/*.original

# Run the application
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/smarthire-backend-1.0.0.jar app.jar
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]

# Use Maven to build the app
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Use a lightweight JDK to run the app
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Run the Spring Boot app
CMD ["java", "-jar", "app.jar"]
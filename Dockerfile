# Step 1: Build the application using JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
# We use -DskipTests to speed up deployment on Render's free tier
RUN mvn clean package -DskipTests

# Step 2: Run the application using a slim JRE 21
FROM eclipse-temurin:21-jre-jammy
COPY --from=build /target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

# Essential: Adding the preview flag here as well because it's in your pom.xml
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
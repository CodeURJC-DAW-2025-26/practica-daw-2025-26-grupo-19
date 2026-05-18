# ---- STAGE 1: Compile React (Node) ----
FROM node:20 AS frontend-builder
WORKDIR /app
# Copy the package.json file from your frontend
COPY frontend/exam0_minimal/package*.json ./
RUN npm install
# Copy the rest of the frontend code and compile
COPY frontend/exam0_minimal/ ./
RUN npm run build


# ---- STAGE 2: Compile Spring Boot (Maven) ----
FROM maven:3.9-eclipse-temurin-21 AS backend-builder
WORKDIR /project
# Copy the pom.xml and download dependencies
COPY setup/pom.xml /project/
RUN mvn dependency:go-offline
# Copy the Java code
COPY setup/src /project/src
# Copy compiled React (Stage 1) into the Java static files.
COPY --from=frontend-builder /app/build/client /project/src/main/resources/static/new
# Compile Java (it now includes React)
RUN mvn -B package -DskipTests


# ---- STAGE 3: Final Image ----
FROM eclipse-temurin:21-jre
WORKDIR /usr/src/app/
# Copy the final JAR
COPY --from=backend-builder /project/target/*.jar /usr/src/app/app.jar
EXPOSE 8443
CMD [ "java", "-jar", "app.jar" ]
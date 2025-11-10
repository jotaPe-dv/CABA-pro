# Dockerfile para Spring Boot API
FROM eclipse-temurin:17-jdk-alpine AS build

# Directorio de trabajo
WORKDIR /app

# Copiar archivos Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

# Compilar aplicación
RUN ./mvnw clean package -DskipTests

# Etapa final - imagen más ligera
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8081

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]

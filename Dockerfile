FROM eclipse-temurin:24-jdk AS build
WORKDIR /app
COPY . .

# Verificar si Maven está instalado
RUN mvn -v || echo "Maven no está instalado en esta imagen"

# Si no está, instalarlo
RUN apt-get update && apt-get install -y maven

# Compilar el proyecto
RUN mvn package -DskipTests

FROM eclipse-temurin:24-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9010
CMD ["java", "-jar", "app.jar"]
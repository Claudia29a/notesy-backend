FROM eclipse-temurin:21 AS build
WORKDIR /app
COPY . .
RUN ./gradlew jar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/notesy-backend-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
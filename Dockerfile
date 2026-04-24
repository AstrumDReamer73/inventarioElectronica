#FROM eclipse-temurin:21-jdk AS build
#WORKDIR /app
#
#COPY gradlew .
#COPY gradle/ gradle/
#COPY build.gradle.kts .
#COPY settings.gradle.kts .
#
## Da permisos al wrapper
#RUN chmod +x gradlew
#
## Descarga dependencias
#RUN ./gradlew dependencies --no-daemon || true
#
#COPY src ./src
#
## Build
#RUN ./gradlew bootJar --no-daemon -x test
#
#FROM eclipse-temurin:21-jre-alpine
#WORKDIR /app
#COPY --from=build /app/build/libs/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]
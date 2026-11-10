# Stage 1: Build the application
FROM gradle:jdk21-noble AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build -x integrationTest --no-daemon

# Stage 2: Create the minimal runtime image
FROM azul-zulu:21-jre-alpine
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/application.jar
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
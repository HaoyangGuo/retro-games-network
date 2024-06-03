FROM amazoncorretto:22

WORKDIR /app

COPY target/retro-games-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]
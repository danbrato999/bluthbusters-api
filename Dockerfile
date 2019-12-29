FROM gradle:jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean shadowJar --no-daemon

FROM openjdk:8-jre-alpine
EXPOSE 9090
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/bluthbusters-api.jar
ENV CORS_EXPRESSION .*
ENV MONGODB_URI localhost
ENV OMDB_API_KEY apikey
ENV YOUTUBE_API_KEY apikey
WORKDIR /app
ENTRYPOINT ["java", "-jar", "./bluthbusters-api.jar"]

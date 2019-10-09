FROM openjdk:11

WORKDIR /usr/src/app/temp

COPY . .

RUN ./gradlew bootJar

RUN mv build/libs/* ..

WORKDIR /usr/src/app

RUN rm -rf temp

EXPOSE 8080

CMD [ "java", "-jar", "api-0.0.1-SNAPSHOT.jar" ]
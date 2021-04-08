FROM openjdk:8

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar", "-Duser.language=en", "-Duser.region=US","/app.jar"]

FROM adoptopenjdk/openjdk11-openj9:jre
VOLUME /tmp
ADD demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
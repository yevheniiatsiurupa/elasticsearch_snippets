FROM adoptopenjdk/openjdk11:latest

ADD ./build/libs/elasticsearch-snippets-0.0.1-SNAPSHOT.jar /
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,address=6001,suspend=n", "-jar", "/elasticsearch-snippets-0.0.1-SNAPSHOT.jar"]

EXPOSE 8080
EXPOSE 6001

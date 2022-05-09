# Build env
FROM maven:3-jdk-11-slim AS build-java-stage
LABEL MAINTAINER="Mickael BARON"

ADD pom.xml /work/pom.xml
WORKDIR /work
RUN ["mvn", "dependency:go-offline"]

ADD ["src", "/work/src"]
RUN ["mvn", "package"]

# Run env
FROM openjdk:11-jre-slim
COPY --from=build-java-stage /work/target/classes /classes/
COPY --from=build-java-stage /work/target/dependency/*.jar /dependency/

ENTRYPOINT ["java", "-cp", "classes:dependency/*", "fr.mickaelbaron.helloworldlogmicroservice.HelloWorldLogMicroservice"]
CMD [localhost]
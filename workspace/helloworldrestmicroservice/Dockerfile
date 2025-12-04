FROM openliberty/open-liberty:full-java11-openj9-ubi

ARG VERSION=1.0
ARG REVISION=SNAPSHOT

LABEL \
    org.opencontainers.image.authors="Mickael BARON" \
    org.opencontainers.image.url="local" \
    org.opencontainers.image.source="https://github.com/mickaelbaron/microservices-docker-java-tutorial" \
    org.opencontainers.image.version="$VERSION" \
    org.opencontainers.image.revision="$REVISION" \
    vendor="Mickael BARON" \
    name="rest" \
    version="$VERSION-$REVISION" \
    summary="The Rest microservice from the microservices Docker Java tutorial" \
    description="This image contains the Rest microservice running with the Open Liberty runtime."

USER root

ENV MAVEN_VERSION=3.9.11
RUN yum install iputils -y && curl -fsSLk https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
    && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME=/usr/share/maven

ADD pom.xml /work/pom.xml
WORKDIR /work
RUN ["mvn", "dependency:go-offline"]

ADD ["src", "/work/src"]
RUN ["mvn", "package"]

RUN mv src/main/liberty/config/server.xml /config/server.xml && chown 1001:0 /config/server.xml && mv target/helloworldrestmicroservice.war /config/apps/ && chown 1001:0 /config/apps/helloworldrestmicroservice.war

USER default

RUN configure.sh
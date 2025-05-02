# Exercice 4 : préparer le microservice Rest (écrire un Dockerfile et créer sa propre image Docker)

Le microservice **Rest** a pour objectif d'isoler dans un conteneur Docker le projet Java du service web _HelloWorld_. Une image à base de Java et de [Maven](https://maven.apache.org/) sera utilisée. Elle sera construite en s'assurant que le projet [Maven](https://maven.apache.org/) compile correctement.

## But

- Écrire un fichier _Dockerfile_.
- Construire une image.
- Compiler un programme Java lors de la construction d'une image.
- Réduire la taille d'une image avec le _Multi-Stage Build_.

## Étapes à suivre

- Créer un fichier _Dockerfile_ à la racine du projet _helloworldrestmicroservice_ et saisir le contenu présenté ci-dessous.

```yaml
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

ENV MAVEN_VERSION=3.9.9
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
```

=================

Donnons quelques détails sur le contenu de ce fichier :

- **Ligne 1** : l'image Docker se base sur une image contenant **Java 11**.

- **Lignes 3 à 16** : ajout de **métadonnées** pour documenter la provenance de l'image (titre, auteur, description, version, etc.).

- **Lignes 20 à 24** : installation de l'outil **ping** en prévision de l'exercice 5 et de [**Maven**](https://maven.apache.org/).

- **Ligne 26** : le fichier _pom.xml_ est copié dans le répertoire _/work/pom.xml_ de l'image.

- **Ligne 27** : le répertoire de travail est défini comme _/work_.

- **Ligne 28** : Maven est exécuté pour télécharger les dépendances du projet.

> 💡 Cette organisation permet d'éviter de retélécharger les dépendances à chaque modification du code source. En revanche, une modification du fichier _pom.xml_ relancera le processus à partir de cette étape.

- **Ligne 30** : le dossier _src_ est copié dans _/work/src_.

- **Ligne 31** : compilation et création du package via Maven (`mvn package`).

- **Ligne 33** : les fichiers générés (le fichier _.war_ et le fichier de configuration _server.xml_) sont déplacés dans le répertoire de destination.

=================

- Construire l'image à partir du fichier _Dockerfile_ en exécutant la ligne de commande suivante.

```bash
docker build -t mickaelbaron/helloworldrestmicroservice .
```

La sortie console attendue :

```bash
[+] Building 35.2s (14/14) FINISHED                                                                                      docker:desktop-linux
 => [internal] load build definition from Dockerfile                                                                                     0.0s
 => => transferring dockerfile: 1.47kB                                                                                                   0.0s
 => [internal] load metadata for docker.io/openliberty/open-liberty:full-java11-openj9-ubi                                               0.0s
 => [internal] load .dockerignore                                                                                                        0.0s
 => => transferring context: 2B                                                                                                          0.0s
 => [1/9] FROM docker.io/openliberty/open-liberty:full-java11-openj9-ubi@sha256:...                                                      0.1s
 => => resolve docker.io/openliberty/open-liberty:full-java11-openj9-ubi@sha256:...                                                      0.0s
 => [internal] load build context                                                                                                        0.0s
 => => transferring context: 83.83kB                                                                                                     0.0s
 => [2/9] RUN yum install iputils -y && curl -fsSLk https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz  2.6s
 => [3/9] ADD pom.xml /work/pom.xml                                                                                                      0.0s
 => [4/9] WORKDIR /work                                                                                                                  0.0s
 => [5/9] RUN ["mvn", "dependency:go-offline"]                                                                                          24.1s
 => [6/9] ADD [src, /work/src]                                                                                                           0.0s
 => [7/9] RUN ["mvn", "package"]                                                                                                         1.3s
 => [8/9] RUN mv src/main/liberty/config/server.xml /config/server.xml && chown 1001:0 /config/server.xml && ...                         0.1s
 => [9/9] RUN configure.sh                                                                                                               5.1s
 => exporting to image                                                                                                                   1.7s
 => => exporting layers                                                                                                                  1.2s
 => => exporting manifest sha256:7f59b36117a1a4187a3b529d211d24bf0e809c57568c2b8ca82a713da58921e0                                        0.0s
 => => exporting config sha256:79a3a033db2c245ba8fea87282d9b8d6fe107eb16b77cb3fcf91e81164396181                                          0.0s
 => => exporting attestation manifest sha256:6d7bd0cf00586f19b70f01bbbc04b0a10ae5a74b311bbf9a4885e91663e83336                            0.0s
 => => exporting manifest list sha256:94122594de6a0d0153657f0396013e832d56887505c9143be59f965be497a00d                                   0.0s
 => => naming to docker.io/mickaelbaron/helloworldrestmicroservice:latest                                                                0.0s
 => => unpacking to docker.io/mickaelbaron/helloworldrestmicroservice:latest                                                             0.5s
```

> Remarquer le téléchargement des dépendances Java (les fichiers Jar) réalisées après la commande `mvn dependency:go-offline`.

- S'assurer que l'image a été correctement construite en exécutant la ligne de commande suivante.

```bash
docker images
```

La sortie console attendue :

```bash
REPOSITORY                                TAG                              IMAGE ID       CREATED         SIZE
mickaelbaron/helloworldrestmicroservice   latest                           94122594de6a   3 minutes ago   2.13GB
openliberty/open-liberty                  full-java11-openj9-ubi           408556785234   3 days ago      1.88GB
redis                                     latest                           fbdbaea47b9a   3 months ago    195MB
```

Vous remarquerez que l'image `openliberty/open-liberty:full-java11-openj9-ubi` est disponible puisque l'image que nous venons de construire est basée sur celle-ci. Noter également la taille de notre image 2.13 Go. En fait l'image _mickaelbaron/helloworldrestmicroservice_ pèse 1 Go de plus car toute la partie Java 11 est déjà présente dans l'image de base _openliberty/open-liberty:full-java11-openj9-ubi_.

- Vérifions que les dépendances Java (les fichiers Jar) ont correctement été stockées dans les couches de notre image. Exécuter la ligne de commande suivante.

```bash
docker history mickaelbaron/helloworldrestmicroservice
```

La sortie console attendue :

```bash
IMAGE          CREATED         CREATED BY                                      SIZE      COMMENT
94122594de6a   8 minutes ago   RUN |2 VERSION=1.0 REVISION=SNAPSHOT /bin/sh…   27MB      buildkit.dockerfile.v0
<missing>      9 minutes ago   USER default                                    0B        buildkit.dockerfile.v0
<missing>      9 minutes ago   RUN |2 VERSION=1.0 REVISION=SNAPSHOT /bin/sh…   2.83MB    buildkit.dockerfile.v0
<missing>      9 minutes ago   RUN |2 VERSION=1.0 REVISION=SNAPSHOT mvn pac…   6.11MB    buildkit.dockerfile.v0
<missing>      9 minutes ago   ADD src /work/src # buildkit                    213kB     buildkit.dockerfile.v0
<missing>      9 minutes ago   RUN |2 VERSION=1.0 REVISION=SNAPSHOT mvn dep…   86.4MB    buildkit.dockerfile.v0
<missing>      9 minutes ago   WORKDIR /work                                   4.1kB     buildkit.dockerfile.v0
<missing>      9 minutes ago   ADD pom.xml /work/pom.xml # buildkit            12.3kB    buildkit.dockerfile.v0
<missing>      9 minutes ago   ENV MAVEN_HOME=/usr/share/maven                 0B        buildkit.dockerfile.v0
<missing>      9 minutes ago   RUN |2 VERSION=1.0 REVISION=SNAPSHOT /bin/sh…   27.1MB    buildkit.dockerfile.v0
<missing>      9 minutes ago   ENV MAVEN_VERSION=3.9.9                         0B        buildkit.dockerfile.v0
<missing>      9 minutes ago   USER root                                       0B        buildkit.dockerfile.v0
<missing>      9 minutes ago   LABEL org.opencontainers.image.authors=Micka…   0B        buildkit.dockerfile.v0
<missing>      9 minutes ago   ARG REVISION=SNAPSHOT                           0B        buildkit.dockerfile.v0
<missing>      9 minutes ago   ARG VERSION=1.0                                 0B        buildkit.dockerfile.v0
<missing>      3 days ago      CMD ["/opt/ol/wlp/bin/server" "run" "default…   0B        buildkit.dockerfile.v0
<missing>      3 days ago      ENTRYPOINT ["/opt/ol/helpers/runtime/docker-…   0B        buildkit.dockerfile.v0
<missing>      3 days ago      EXPOSE map[9080/tcp:{} 9443/tcp:{}]             0B        buildkit.dockerfile.v0
<missing>      3 days ago      USER 1001                                       0B        buildkit.dockerfile.v0
<missing>      3 days ago      ENV RANDFILE=/tmp/.rnd OPENJ9_JAVA_OPTIONS=-…   0B        buildkit.dockerfile.v0
<missing>      3 days ago      RUN |4 LIBERTY_VERSION=25.0.0.4 LIBERTY_BUIL…   45.7MB    buildkit.dockerfile.v0
<missing>      3 days ago      RUN |4 LIBERTY_VERSION=25.0.0.4 LIBERTY_BUIL…   283kB     buildkit.dockerfile.v0
<missing>      3 days ago      RUN |4 LIBERTY_VERSION=25.0.0.4 LIBERTY_BUIL…   57.3kB    buildkit.dockerfile.v0
<missing>      3 days ago      ENV PATH=/opt/java/openjdk/bin:/usr/local/sb…   0B        buildkit.dockerfile.v0
<missing>      3 days ago      RUN |4 LIBERTY_VERSION=25.0.0.4 LIBERTY_BUIL…   9.35MB    buildkit.dockerfile.v0
...
```

Nous remarquons à ligne 7, l'exécution de notre commande `mvn dependency:go-offline` dont le résultat a fait augmenter la taille de l'image de 86 Mo. Ceci est dû aux dépendances Java qui ont été téléchargées. Par conséquent, si nous modifions uniquement le code source de notre projet, seule la compilation des sources sera réalisée. Vérifions cela en modifiant le code source de notre projet.

- Éditer la classe `fr.mickaelbaron.helloworldrestmicroservice.service.HelloWorldResource` et faites une modification (ajouter par exemple un espace), puis relancer la construction de l'image.

```bash
docker build -t mickaelbaron/helloworldrestmicroservice .
```

La sortie console attendue :

```bash
[+] Building 6.2s (14/14) FINISHED                                                                                                                 docker:desktop-linux
 => [internal] load build definition from Dockerfile                                                                                               0.0s
 => => transferring dockerfile: 1.47kB                                                                                                             0.0s
 => [internal] load metadata for docker.io/openliberty/open-liberty:full-java11-openj9-ubi                                                         0.0s
 => [internal] load .dockerignore                                                                                                                  0.0s
 => => transferring context: 2B                                                                                                                    0.0s
 => [1/9] FROM docker.io/openliberty/open-liberty:full-java11-openj9-ubi@sha256:...                                                                0.0s
 => => resolve docker.io/openliberty/open-liberty:full-java11-openj9-ubi@sha256:...                                                                0.0s
 => [internal] load build context                                                                                                                  0.0s
 => => transferring context: 5.66kB                                                                                                                0.0s
 => CACHED [2/9] RUN yum install iputils -y && curl -fsSLk https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz ... 0.0s
 => CACHED [3/9] ADD pom.xml /work/pom.xml                                                                                                         0.0s
 => CACHED [4/9] WORKDIR /work                                                                                                                     0.0s
 => CACHED [5/9] RUN ["mvn", "dependency:go-offline"]                                                                                              0.0s
 => [6/9] ADD [src, /work/src]                                                                                                                     0.0s
 => [7/9] RUN ["mvn", "package"]                                                                                                                   1.3s
 => [8/9] RUN mv src/main/liberty/config/server.xml /config/server.xml && chown 1001:0 /config/server.xml ...                                      0.1s
 => [9/9] RUN configure.sh                                                                                                                         4.1s
 => exporting to image                                                                                                                             0.5s
 => => exporting layers                                                                                                                            0.3s
 => => exporting manifest sha256:adf19d98a8ed9a8d65a5f62566dde78c70f76b325c3f91e0875ee69155240e80                                                  0.0s
 => => exporting config sha256:24fac79eefe153f102a9daf0a9b3416c13360761f6147354a237c756f0c57794                                                    0.0s
 => => exporting attestation manifest sha256:34a6c34730708ac193f8ac2a7375798fd56f0a4016bc734d5c370e8299c36689                                      0.0s
 => => exporting manifest list sha256:65d5e6f40787fe45afecea8b6fa465edef92454602f2f3363680d996d79dfce3                                             0.0s
 => => naming to docker.io/mickaelbaron/helloworldrestmicroservice:latest                                                                          0.0s
 => => unpacking to docker.io/mickaelbaron/helloworldrestmicroservice:latest                                                                       0.1s
```

Vous noterez que la construction de l'image se fait plus rapidement, car les étapes précédant `mvn dependency:go-offline` sont en cache.

À cet étape, la taille de l'image est quand même assez imposante : 2.13 Go. Plusieurs techniques existent pour réduire la taille. La première est de chercher une image de base réduite. L'image de base actuelle de [Open Liberty](https://openliberty.io/) inclut toutes les fonctionnalités de [MicroProfile](https://microprofile.io/). Une image appelée `openliberty/open-liberty:kernel-slim-java11-openj9-ubi` permet d'ajouter à postériori les fonctionnalités en inspectant le fichier _server.xml_.

- Créer un fichier _DockerfileSlim_ à la racine du projet _helloworldrestmicroservice_ et saisir le contenu présenté ci-dessous.

```yaml
FROM openliberty/open-liberty:kernel-slim-java11-openj9-ubi

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

ENV MAVEN_VERSION=3.9.9
RUN curl -fsSLk https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
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

RUN features.sh

RUN configure.sh
```

Ce fichier est très similaire au fichier _Dockerfile_ excepté l'image de base et l'instruction `RUN features.sh` qui permet de configurer uniquement les fonctionnalités [MicroProfile](https://microprofile.io/) nécessaires.

- Construire l'image à partir du fichier _DockerfileSlim_ en exécutant la ligne de commande suivante.

```bash
docker build -t mickaelbaron/helloworldrestmicroservice:slim -f DockerfileSlim .
```

La sortie console attendue :

```bash
REPOSITORY                                TAG                                     IMAGE ID       CREATED          SIZE
mickaelbaron/helloworldrestmicroservice   slim                                    f83ec1e9fd30   31 minutes ago   1.41GB
mickaelbaron/helloworldrestmicroservice   latest                                  65d5e6f40787   48 minutes ago   2.13GB
openliberty/open-liberty                  full-java11-openj9-ubi                  408556785234   3 days ago       1.88GB
openliberty/open-liberty                  kernel-slim-java11-openj9-ubi           cf779178c0b6   3 days ago       1.13GB
redis                                     latest                                  fbdbaea47b9a   3 months ago     195MB
```

Le résultat est encourageant car la taille de l'image est passée de 2.13 Go à 1.41 Go pour la même fonctionnalité. À cet étape, nous constatons que nous venons de construire une image qui fournit Java dans sa version JDK, l'outil [Maven](https://maven.apache.org/), des dépendances [Maven](https://maven.apache.org/) nécessaires à la compilation pour l'exécution du projet _helloworldrestmicroservice_. Or, nous avons juste besoin d'une version Java dans sa version JRE, des dépendances pour le serveur [Open Liberty](https://openliberty.io/), un fichier War et un fichier de configuration _serveur.xml_.  

Pour résoudre ces problèmes, nous allons utiliser le _Multi-Stage Build_ qui permet de découper un fichier _Dockerfile_ en plusieurs phases. Dans notre cas, nous aurons ainsi une phase de compilation et une phase d'exécution. La phase exécution qui sera finale fournira une image plus réduite puisqu'elle sera basée sur une image JRE et non JDK.

- Créer un fichier _DockerfileMSB_ à la racine du projet _helloworldrestmicroservice_, éditer le fichier et saisir le contenu présenté ci-dessous.

```yaml
# Build env
FROM maven:3.9.9-eclipse-temurin-11 AS build-java-stage

ADD pom.xml /work/pom.xml
WORKDIR /work
RUN ["mvn", "dependency:go-offline"]

ADD ["src", "/work/src"]
RUN ["mvn", "package"]

FROM openliberty/open-liberty:kernel-slim-java11-openj9-ubi-minimal

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

COPY --chown=1001:0 --from=build-java-stage /work/src/main/liberty/config/server.xml /config/server.xml
COPY --chown=1001:0 --from=build-java-stage /work/target/helloworldrestmicroservice.war /config/apps/
    
RUN features.sh

RUN configure.sh
```

=================

Donnons quelques détails sur le contenu de ce fichier.

- **De la ligne 1 à la ligne 10**, les instructions décrivent la phase de compilation qui porte le nom de `build-java-stage`.

- **De la ligne 11 à la ligne 33**, les instructions décrivent la phase d'exécution.

Pour la phase de compilation, contrairement à notre premier fichier _Dockerfile_, nous sommes partis d'une image de base [Maven](https://maven.apache.org/). Il n'est donc pas nécessaire d'installer [Maven](https://maven.apache.org/). À la fin de cette phase de compilation, les classes compilées et les dépendances seront disponibles. Dans la phase d'exécution, ces fichiers et dépendances seront copiés depuis la première phase vers les répertoires _classes/_ et _dependency/_ (lignes 28 et 29).

=================

- Nous allons construire l'image à partir du fichier _DockerfileMSB_, exécuter la ligne de commande suivante.

```bash
docker build -t mickaelbaron/helloworldrestmicroservice:msb -f DockerfileMSB .
```

```bash
[+] Building 35.9s (17/17) FINISHED                                                                                             docker:desktop-linux
 => [internal] load build definition from DockerfileMSB                                                                         0.0s
 => => transferring dockerfile: 1.21kB                                                                                          0.0s
 => [internal] load metadata for docker.io/openliberty/open-liberty:kernel-slim-java11-openj9-ubi-minimal                       0.0s
 => [internal] load metadata for docker.io/library/maven:3.9.9-eclipse-temurin-11                                               0.4s
 => [internal] load .dockerignore                                                                                               0.0s
 => => transferring context: 2B                                                                                                 0.0s
 => CACHED [build-java-stage 1/6] FROM docker.io/library/maven:3.9.9-eclipse-temurin-11@sha256:c83c4bc77fd7217445c66f5da8fdf8ec 0.0s
 => => resolve docker.io/library/maven:3.9.9-eclipse-temurin-11@sha256:c83c4bc77fd7217445c66f5da8fdf8ec4cfeaa2e3e0d8acb65a4b2b7 0.0s
 => CACHED [stage-1 1/5] FROM docker.io/openliberty/open-liberty:kernel-slim-java11-openj9-ubi-minimal@sha256:7ce09d7f1e0ed03c3 0.0s
 => => resolve docker.io/openliberty/open-liberty:kernel-slim-java11-openj9-ubi-minimal@sha256:7ce09d7f1e0ed03c3d9cc3570e9e55df 0.0s
 => [internal] load build context                                                                                               0.0s
 => => transferring context: 4.43kB                                                                                             0.0s
 => [build-java-stage 2/6] ADD pom.xml /work/pom.xml                                                                            0.0s
 => [build-java-stage 3/6] WORKDIR /work                                                                                        0.0s
 => [build-java-stage 4/6] RUN ["mvn", "dependency:go-offline"]                                                                18.8s
 => [build-java-stage 5/6] ADD [src, /work/src]                                                                                 0.0s
 => [build-java-stage 6/6] RUN ["mvn", "package"]                                                                               1.2s
 => [stage-1 2/5] COPY --chown=1001:0 --from=build-java-stage /work/src/main/liberty/config/server.xml /config/server.xml       0.0s
 => [stage-1 3/5] COPY --chown=1001:0 --from=build-java-stage /work/target/helloworldrestmicroservice.war /config/apps/         0.0s
 => [stage-1 4/5] RUN features.sh                                                                                               7.3s
 => [stage-1 5/5] RUN configure.sh                                                                                              7.1s
 => exporting to image                                                                                                          0.7s
 => => exporting layers                                                                                                         0.5s
 => => exporting manifest sha256:c8abe5568bc50407abb34755d4edb753ff9710bcf1e7f6be9a2f25edae611c91                               0.0s
 => => exporting config sha256:f6a360b4df6bd8ef809f28a3c470d09d3741abee53ee1fd01e3dd4eee92ee3ac                                 0.0s
 => => exporting attestation manifest sha256:dec79cd7cab8c1fcd447be4f22136099fa71f39564895af23e0778bb88d208cc                   0.0s
 => => exporting manifest list sha256:04ed03073f40bc910a1e094a1b155f6b5f12fec2cb1dd7aa066164333b7defbc                          0.0s
 => => naming to docker.io/mickaelbaron/helloworldrestmicroservice:msb                                                          0.0s
 => => unpacking to docker.io/mickaelbaron/helloworldrestmicroservice:msb                                                       0.2s
```

- S'assurer que l'image a été correctement construite en exécutant la ligne de commande suivante.

```bash
docker images
```

La sortie console attendue :

```
mickaelbaron/helloworldrestmicroservice   msb                                     04ed03073f40   2 minutes ago   574MB
mickaelbaron/helloworldrestmicroservice   slim                                    e1db246447b1   22 hours ago    1.41GB
mickaelbaron/helloworldrestmicroservice   latest                                  65d5e6f40787   22 hours ago    2.13GB
openliberty/open-liberty                  full-java11-openj9-ubi                  408556785234   4 days ago      1.88GB
openliberty/open-liberty                  kernel-slim-java11-openj9-ubi-minimal   7ce09d7f1e0e   4 days ago      471MB
openliberty/open-liberty                  kernel-slim-java11-openj9-ubi           cf779178c0b6   4 days ago      1.13GB
redis                                     latest                                  fbdbaea47b9a   3 months ago    195MB
```

Nous constatons que l'image construite a diminué en taille. Elle est passée de 1.41 Go à 574 Mo.

## Avez-vous bien compris ?

Pour continuer sur les concepts présentés dans cet exercice, nous proposons l'expérimentation suivante :

- proposer un fichier _Dockerfile_ permettant d'ajouter directement le fichier _server.xml_ et le fichier _war_ sans passer par une phase de compilation lors de la construction de l'image (hypothèse de départ : un fichier _war_ a été construit préalablement).

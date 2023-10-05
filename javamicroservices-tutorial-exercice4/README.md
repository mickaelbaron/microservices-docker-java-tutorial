# Exercice 4 : préparer le microservice Rest (écrire un Dockerfile et créer sa propre image Docker)

Le microservice **Rest** a pour objectif d'isoler dans un conteneur Docker le projet Java du service web *HelloWorld*. Une image à base de Java et de [Maven](https://maven.apache.org/) sera utilisée. Elle sera construite en s'assurant que le projet [Maven](https://maven.apache.org/) compile correctement.

## But

* Écrire un fichier Dockerfile.
* Construire une image.
* Isoler un programme Java.
* Réduire la taille d'une image avec le *Multi-Stage Build*.

## Étapes à suivre

* Créer un fichier *Dockerfile* à la racine du projet *helloworldrestmicroservice*.

* Ouvrir un éditeur de texte et saisir le contenu présenté ci-dessous.

```yaml
FROM openjdk:11-slim
LABEL MAINTAINER="Mickael BARON"

ENV MAVEN_VERSION 3.8.5
RUN apt-get update -y && apt-get install -y curl && curl -fsSLk https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
    && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven

ADD pom.xml /work/pom.xml
WORKDIR /work
RUN ["mvn", "dependency:go-offline"]

ADD ["src", "/work/src"]
RUN ["mvn", "package"]

EXPOSE 8080
ENTRYPOINT ["java", "-cp", "target/classes:target/dependency/*", "com.kumuluz.ee.EeApplication"]
```

Donnons quelques détails sur le contenu de ce fichier. À la ligne 1, il est précisé que l'image que nous souhaitons construire se basera sur une image fournissant Java 11. De la ligne 4 à la ligne 8 nous précisons comment installer [curl](https://curl.se/) et [Maven](https://maven.apache.org/). La ligne 10 précise que le fichier *pom.xml* sera copié dans le répertoire */work/pom.xml* de l'image. La ligne 11 indique que le répertoire courant sera */work*. La ligne 12 demande à [Maven](https://maven.apache.org/) de télécharger tout ce dont il a besoin. L'avantage de procéder de cette manière c'est que si vous modifier le code source de votre projet, les dépendances n'auront pas à être de nouveau téléchargées puisqu'elles auront été faites avant. Toutefois, si vous modifiez le fichier *pom.xml*, la reconstruction de l'image se fera à partir de la ligne 10. La ligne 14 précise que le répertoire *src* sera copié dans le répertoire */work/src* de l'image. À la ligne 15 il est demandé de faire une construction de package via Maven. À la ligne 17, il est indiqué que le port 8080 sera exposé. Enfin à la ligne 18, il est indiqué comment démarrer le programme Java via l'exécution d'une ligne de commande identique à celle que nous avions vu dans l'exercice 1.

* Nous allons construire l'image à partir du fichier *Dockerfile*, exécuter la ligne de commande suivante.

```console
$ docker build -t mickaelbaron/helloworldrestmicroservice .
[+] Building 64.1s (13/13) FINISHED
 => [internal] load build definition from Dockerfile                                                                             0.0s
 => => transferring dockerfile: 84B                                                                                              0.0s
 => [internal] load .dockerignore                                                                                                0.0s
 => => transferring context: 2B                                                                                                  0.0s
 => [internal] load metadata for docker.io/library/openjdk:11-slim                                                               1.3s
 => [auth] library/openjdk:pull token for registry-1.docker.io                                                                   0.0s
 => CACHED [1/7] FROM docker.io/library/openjdk:11-slim@sha256:ab805a51840ed54ccdc93604277684158a182fca1b49ff0dddac1df4b9d09ab9  0.0s
 => => resolve docker.io/library/openjdk:11-slim@sha256:ab805a51840ed54ccdc93604277684158a182fca1b49ff0dddac1df4b9d09ab9         0.0s
 => [internal] load build context                                                                                                0.0s
 => => transferring context: 3.79kB                                                                                              0.0s
 => [2/7] RUN apt-get update -y && apt-get install -y curl && curl -fsSLk                                                        4.3s
 => [3/7] ADD pom.xml /work/pom.xml                                                                                              0.0s
 => [4/7] WORKDIR /work                                                                                                          0.0s
 => [5/7] RUN ["mvn", "dependency:go-offline"]                                                                                  45.7s
 => [6/7] ADD [src, /work/src]                                                                                                   0.1s
 => [7/7] RUN ["mvn", "package"]                                                                                                10.8s
 => exporting to image                                                                                                           1.6s
 => => exporting layers                                                                                                          1.6s
 => => writing image sha256:f2e9bfe76f628c6ac54af093242a30f0b7cac83dc03ada7abd1a9e2d38a1ef0b                                     0.0s
 => => naming to docker.io/mickaelbaron/helloworldrestmicroservice                                                               0.0s

```

> Remarquer le téléchargement des dépendances Java (les fichiers Jar) réalisées après la commande `$ mvn dependency:go-offline`.

* S'assurer que l'image a été correctement construite en exécutant la ligne de commande suivante.

```console
$ docker images
REPOSITORY                                TAG                  IMAGE ID       CREATED          SIZE
mickaelbaron/helloworldrestmicroservice   latest               36a8790c9361   14 minutes ago   540MB
redis                                     latest               a10f849e1540   45 hours ago     117MB
openjdk                                   11-slim              07f23505db21   4 days ago       429MB
```

Vous remarquerez que l'image *openjdk:11-slim* est disponible puisque l'image que nous venons de construire est basée sur celle-ci. Noter également la taille de notre image 540 Mo. En fait l'image *mickaelbaron/helloworldrestmicroservice* ne pèse que ~ 131 Mo car toute la partie Java 11 est déjà présente dans l'image de base *openjdk:11-slim*.

* Vérifions que les dépendances Java (les fichiers Jar) ont correctement été stockées dans les couches de notre image. Exécuter la ligne de commande suivante.

```console
$ docker history mickaelbaron/helloworldrestmicroservice
IMAGE          CREATED          CREATED BY                                      SIZE      COMMENT
36a8790c9361   15 minutes ago   ENTRYPOINT ["java" "-cp" "target/classes:tar…   0B        buildkit.dockerfile.v0
<missing>      15 minutes ago   EXPOSE map[8080/tcp:{}]                         0B        buildkit.dockerfile.v0
<missing>      15 minutes ago   RUN mvn package # buildkit                      20.3MB    buildkit.dockerfile.v0
<missing>      16 minutes ago   ADD src /work/src # buildkit                    66.5kB    buildkit.dockerfile.v0
<missing>      16 minutes ago   RUN mvn dependency:go-offline # buildkit        58.3MB    buildkit.dockerfile.v0
<missing>      22 minutes ago   WORKDIR /work                                   0B        buildkit.dockerfile.v0
<missing>      22 minutes ago   ADD pom.xml /work/pom.xml # buildkit            2.72kB    buildkit.dockerfile.v0
<missing>      22 minutes ago   ENV MAVEN_HOME=/usr/share/maven                 0B        buildkit.dockerfile.v0
<missing>      22 minutes ago   RUN /bin/sh -c apt-get update -y && apt-get …   32.5MB    buildkit.dockerfile.v0
<missing>      22 minutes ago   ENV MAVEN_VERSION=3.8.5                         0B        buildkit.dockerfile.v0
<missing>      22 minutes ago   LABEL MAINTAINER=Mickael BARON                  0B        buildkit.dockerfile.v0
<missing>      4 days ago       /bin/sh -c #(nop)  CMD ["jshell"]               0B
<missing>      4 days ago       /bin/sh -c set -eux;   arch="$(dpkg --print-…   344MB
<missing>      4 days ago       /bin/sh -c #(nop)  ENV JAVA_VERSION=11.0.15     0B
<missing>      9 days ago       /bin/sh -c #(nop)  ENV LANG=C.UTF-8             0B
<missing>      9 days ago       /bin/sh -c #(nop)  ENV PATH=/usr/local/openj…   0B
<missing>      9 days ago       /bin/sh -c { echo '#/bin/sh'; echo 'echo "$J…   27B
<missing>      9 days ago       /bin/sh -c #(nop)  ENV JAVA_HOME=/usr/local/…   0B
<missing>      9 days ago       /bin/sh -c set -eux;  apt-get update;  apt-g…   4.88MB
<missing>      9 days ago       /bin/sh -c #(nop)  CMD ["bash"]                 0B
<missing>      9 days ago       /bin/sh -c #(nop) ADD file:8b1e79f91081eb527…   80.4MB
```

Nous remarquons à ligne 7, l'exécution de notre commande `$ mvn dependency:go-offline` dont le résultat a fait augmenter la taille de l'image de 58 Mo. Ceci est dû aux dépendances Java qui ont été téléchargées. Par conséquent, si nous modifions uniquement le code source de notre projet, seule la compilation des sources sera réalisée.

* Éditer la classe `fr.mickaelbaron.helloworldrestmicroservice.service.HelloWorldResource` et faites une modification (ajouter par exemple un espace), puis relancer la construction de l'image.

```console
$ docker build -t mickaelbaron/helloworldrestmicroservice .
[+] Building 9.2s (13/13) FINISHED
 => [internal] load build definition from Dockerfile                                                                       0.0s
 => => transferring dockerfile: 84B                                                                                        0.0s
 => [internal] load .dockerignore                                                                                          0.0s
 => => transferring context: 2B                                                                                            0.0s
 => [internal] load metadata for docker.io/library/openjdk:11-slim                                                         1.3s
 => [auth] library/openjdk:pull token for registry-1.docker.io                                                             0.0s
 => [1/7] FROM docker.io/library/openjdk:11-slim@sha256:ab805a51840ed54ccdc93604277684158a182fca1b49ff0dddac1df4b9d09ab9   0.0s
 => => resolve docker.io/library/openjdk:11-slim@sha256:ab805a51840ed54ccdc93604277684158a182fca1b49ff0dddac1df4b9d09ab9   0.0s
 => [internal] load build context                                                                                          0.0s
 => => transferring context: 5.14kB                                                                                        0.0s
 => CACHED [2/7] RUN apt-get update -y && apt-get install -y curl && curl -fsSLk                                           0.0s
 => CACHED [3/7] ADD pom.xml /work/pom.xml                                                                                 0.0s
 => CACHED [4/7] WORKDIR /work                                                                                             0.0s
 => CACHED [5/7] RUN ["mvn", "dependency:go-offline"]                                                                      0.0s
 => [6/7] ADD [src, /work/src]                                                                                             0.1s
 => [7/7] RUN ["mvn", "package"]                                                                                           7.5s
 => exporting to image                                                                                                     0.2s
 => => exporting layers                                                                                                    0.2s
 => => writing image sha256:71f74d32e4fcf471a2d6d8767d7416421aadad1ed1ed04496dced47c08de17c2                               0.0s
 => => naming to docker.io/mickaelbaron/helloworldrestmicroservice                                                         0.0s
```

Vous noterez que la construction de l'image se fait plus rapidement, car les étapes précédant `$ mvn dependency:go-offline` sont en cache.

À cet étape, nous constatons que nous venons de construire une image qui fournit Java dans sa version JDK, l'outil [Maven](https://maven.apache.org/), les dépendances [Maven](https://maven.apache.org/) nécessaires à son fonctionnement, les classes compilées et les dépendances [Maven](https://maven.apache.org/) pour l'exécution du projet *helloworldrestmicroservice*. Or, seuls ces deux deniers sont nécessaires pour la suite. Par ailleurs, la taille de l'image est quand même assez imposante : 540 Mo.

Pour résoudre ces problèmes, nous allons utiliser le *Multi-Stage Build* qui permet de découper un fichier *Dockerfile* en plusieurs phases. Dans notre cas, nous aurons ainsi une phase de compilation et une phase d'exécution. La phase exécution qui sera finale fournira une image plus réduite puisqu'elle sera basée sur une image JRE et non JDK.

* Créer un fichier *DockerfileMSB* à la racine du projet *helloworldrestmicroservice*, éditer le fichier et saisir le contenu présenté ci-dessous.

```yaml
# Build env
FROM maven:3-jdk-11-slim AS build-java-stage
LABEL maintainer="Mickael BARON"

ADD pom.xml /work/pom.xml
WORKDIR /work
RUN ["mvn", "dependency:go-offline"]

ADD ["src", "/work/src"]
RUN ["mvn", "package"]

# Run env
FROM openjdk:11-jre-slim
COPY --from=build-java-stage /work/target/classes /classes/
COPY --from=build-java-stage /work/target/dependency/*.jar /dependency/

EXPOSE 8080
ENTRYPOINT ["java", "-cp", "classes:dependency/*", "com.kumuluz.ee.EeApplication"]
```

Donnons quelques détails sur le contenu de ce fichier. De la ligne 1 à la ligne 10, les instructions décrivent la phase de compilation qui porte le nom de `build-java-stage`. De la ligne 12 à la ligne 18, les instructions décrivent la phase d'exécution. Pour la phase de compilation, contrairement à notre premier fichier *Dockerfile*, nous sommes partis d'une image de base [Maven](https://maven.apache.org/). Il n'est donc pas nécessaire d'installer [Maven](https://maven.apache.org/). À la fin de cette phase de compilation, les classes compilées et les dépendances seront disponibles. Dans la phase d'exécution, ces fichiers et dépendances seront copiés depuis la première phase vers les répertoires _classes/_ et _dependency/_ (lignes 14 et 15).

* Nous allons construire l'image à partir du fichier *DockerfileMSB*, exécuter la ligne de commande suivante.

```console
$ docker build -t mickaelbaron/helloworldrestmicroservice . -f DockerfileMSB
[+] Building 12.8s (16/16) FINISHED
 => [internal] load build definition from DockerfileMSB                                                                                        0.0s
 => => transferring dockerfile: 575B                                                                                                           0.0s
 => [internal] load .dockerignore                                                                                                              0.0s
 => => transferring context: 2B                                                                                                                0.0s
 => [internal] load metadata for docker.io/library/openjdk:11-jre-slim                                                                         1.3s
 => [internal] load metadata for docker.io/library/maven:3-jdk-11-slim                                                                         2.0s
 => [auth] library/maven:pull token for registry-1.docker.io                                                                                   0.0s
 => [build-java-stage 1/6] FROM docker.io/library/maven:3-jdk-11-slim@sha256:6b6505195afeda7a01257f8e9d9124c9f8feba4ed1d661c3af454142470fce  400.0s
 => => resolve docker.io/library/maven:3-jdk-11-slim@sha256:6b6505195afeda7a01257f8e9d9124c9f8feba4ed1d661c3af454142470fce40                   0.0s
 => [internal] load build context                                                                                                              0.0s
 => => transferring context: 3.79kB                                                                                                            0.0s
 => CACHED [stage-1 1/3] FROM docker.io/library/openjdk:11-jre-slim@sha256:7b50fd28cd524b2abd57a3406d3df087aa6d2817469841e01b2e8e125089202c    0.0s
 => => resolve docker.io/library/openjdk:11-jre-slim@sha256:7b50fd28cd524b2abd57a3406d3df087aa6d2817469841e01b2e8e125089202c                   0.0s
 => CACHED [build-java-stage 2/6] ADD pom.xml /work/pom.xml                                                                                    0.0s
 => CACHED [build-java-stage 3/6] WORKDIR /work                                                                                                0.0s
 => CACHED [build-java-stage 4/6] RUN ["mvn", "dependency:go-offline"]                                                                         0.0s
 => [build-java-stage 5/6] ADD [src, /work/src]                                                                                                0.0s
 => [build-java-stage 6/6] RUN ["mvn", "package"]                                                                                              9.8s
 => [stage-1 2/3] COPY --from=build-java-stage /work/target/classes /classes/                                                                  0.0s
 => [stage-1 3/3] COPY --from=build-java-stage /work/target/dependency/*.jar /dependency/                                                      0.1s
 => exporting to image                                                                                                                         0.1s
 => => exporting layers                                                                                                                        0.1s
 => => writing image sha256:8a6a9f5d78217be633cc49cf161b264b6ed59a79d9e40b5c34188bc8bae9de95                                                   0.0s
 => => naming to docker.io/mickaelbaron/helloworldrestmicroservice                                                                             0.0s
 ```

* S'assurer que l'image a été correctement construite en exécutant la ligne de commande suivante.

```console
$ docker images
REPOSITORY                                TAG                  IMAGE ID       CREATED          SIZE
mickaelbaron/helloworldrestmicroservice   latest               1c2d8a144779   5 minutes ago    246MB
redis                                     latest               a10f849e1540   46 hours ago     117MB
openjdk                                   11-jre-slim          7f281170d66d   4 days ago       227MB
```

Nous constatons que l'image construite a diminué en taille. Elle est passée de 540 Mo à 246 Mo.

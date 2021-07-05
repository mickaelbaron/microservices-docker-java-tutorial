# Exercice 4 : préparer le microservice Rest (écrire un Dockerfile et créer sa propre image Docker)

Le microservice **Rest** a pour objectif d'isoler dans un conteneur Docker le projet Java du service web *HelloWorld*. Une image Docker à base de Java et de Maven sera utilisée. Elle sera construite en s'assurant que le projet Maven compile correctement.

## But

* Écrire un fichier Dockerfile.
* Construire une image Docker.
* Isoler un programme Java.
* Réduire la taille d'une image Docker avec le Multi-Stage Build.

## Étapes à suivre

* Créer un fichier *Dockerfile* à la racine du projet *helloworldrestmicroservice*.

* Ouvrir un éditeur de texte et saisir le contenu présenté ci-dessous.

```yaml
FROM java:openjdk-8-jdk
LABEL maintener="Mickael BARON"

ENV MAVEN_VERSION 3.3.9
RUN curl -fsSLk https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
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

Donnons quelques détails sur le contenu de ce fichier. À la ligne 1, il est précisé que l'image que nous souhaitons construire se basera sur une image fournissant Java 8. De la ligne 4 à la ligne 8 nous précisons comment doit être installer Maven. La ligne 10 précise que le fichier *pom.xml* sera copié dans le répertoire */work/pom.xml* de l'image. La ligne 11 indique que le répertoire courant sera */work*. La ligne 12 demande à Maven de télécharger tout ce dont il a besoin. L'avantage de procéder de cette manière c'est que si vous modifier le code source de votre projet, les dépendances n'auront pas à être de nouveau téléchargées puisqu'elles auront été faites avant. Toutefois, si vous modifiez le fichier *pom.xml*, la reconstruction de l'image se fera à partir de la ligne 10. La ligne 14 précise que le répertoire *src* sera copié dans le répertoire */work/src* de l'image. À la ligne 15 il est demandé de faire une construction de package via Maven. À la ligne 17, il est indiqué que le port 8080 sera exposé. Enfin à la ligne 18, il est indiqué comment démarrer le programme Java via l'exécution d'une ligne de commande identique à celle que nous avions vu dans l'exercice 1.

* Nous allons construire l'image à partir du fichier *Dockerfile*, exécuter la ligne de commande suivante.

```console
$ docker build -t mickaelbaron/helloworldrestmicroservice .
...
Step 8/12 : RUN ["mvn", "dependency:go-offline"]
 ---> Running in 32928666e2c3
[INFO] Scanning for projects...
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-dependency-plugin/2.10/maven-dependency-plugin-2.10.pom
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-dependency-plugin/2.10/maven-dependency-plugin-2.10.pom (12 KB at 15.6 KB/sec)
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-plugins/27/maven-plugins-27.pom
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-plugins/27/maven-plugins-27.pom (12 KB at 217.6 KB/sec)
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/maven-parent/26/maven-parent-26.pom
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/maven-parent/26/maven-parent-26.pom (39 KB at 607.1 KB/sec)
Downloading: https://repo.maven.apache.org/maven2/org/apache/apache/16/apache-16.pom
Downloaded: https://repo.maven.apache.org/maven2/org/apache/apache/16/apache-16.pom (16 KB at 300.7 KB/sec)
Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-dependency-plugin/2.10/maven-dependency-plugin-2.10.jar
Downloaded: https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-dependency-plugin/2.10/maven-dependency-plugin-2.10.jar (157 KB at 1174.3 KB/sec)
...
Step 11/12 : EXPOSE 8080
 ---> Running in 6f6725702850
Removing intermediate container 6f6725702850
 ---> 2dc65427ed4f
Step 12/12 : ENTRYPOINT ["java", "-cp", "target/classes:target/dependency/*", "com.kumuluz.ee.EeApplication"]
 ---> Running in a4290ac7fa93
Removing intermediate container a4290ac7fa93
 ---> e04b1b2fc0aa
Successfully built e04b1b2fc0aa
Successfully tagged mickaelbaron/helloworldrestmicroservice:latest
```

Remarquer le téléchargement des dépendances Java (les fichiers Jar) réalisées après la commande `$ mvn dependency:go-offline`.

* S'assurer que l'image a été correctement construite en exécutant la ligne de commande suivante.

```console
$ docker images
REPOSITORY                                       TAG                 IMAGE ID            CREATED             SIZE
mickaelbaron/helloworldrestmicroservice          latest              e04b1b2fc0aa        9 minutes ago       706MB
redis                                            latest              ce25c7293564        2 weeks ago         95MB
java                                             openjdk-8-jdk       d23bdf5b1b1b        23 months ago       643MB
```

Vous remarquerez que l'image *java* est disponible puisque l'image que nous venons de construire est basée sur celle-ci. Noter également la taille de notre image 706 Mo. En fait l'image *mickaelbaron/helloworldrestmicroservice* ne pèse que ~ 60 Mo car toute la partie Java 8 est déjà présente dans l'image de base *java*.

* Vérifions que les dépendances Java (les fichiers Jar) ont correctement été stockées dans les couches de notre image. Exécuter la ligne de commande suivante.

```console
$ docker history mickaelbaron/helloworldrestmicroservice
IMAGE               CREATED             CREATED BY                                      SIZE                COMMENT
e04b1b2fc0aa        13 minutes ago      /bin/sh -c #(nop)  ENTRYPOINT ["java" "-cp" …   0B
2dc65427ed4f        13 minutes ago      /bin/sh -c #(nop)  EXPOSE 8080                  0B
f2be75fce3d1        13 minutes ago      mvn package                                     18.4MB
7ecda19aca78        14 minutes ago      /bin/sh -c #(nop) ADD dir:6c964c0442e33433ff…   11.1kB
0f237d437944        14 minutes ago      mvn dependency:go-offline                       34.1MB
55ee0829d3eb        15 minutes ago      /bin/sh -c #(nop) WORKDIR /work                 0B
194da6ab61a8        15 minutes ago      /bin/sh -c #(nop) ADD file:3ac8f7547512fa916…   2.44kB
0fcad87e84f6        15 minutes ago      /bin/sh -c #(nop)  ENV MAVEN_HOME=/usr/share…   0B
0da24f3c9e32        15 minutes ago      /bin/sh -c curl -fsSLk https://archive.apach…   10MB
6ba73ad7b2d7        15 minutes ago      /bin/sh -c #(nop)  ENV MAVEN_VERSION=3.3.9      0B
e7bd770a86bd        15 minutes ago      /bin/sh -c #(nop)  LABEL maintener=Mickael B…   0B
d23bdf5b1b1b        23 months ago       /bin/sh -c /var/lib/dpkg/info/ca-certificate…   419kB
<missing>           23 months ago       /bin/sh -c set -x  && apt-get update  && apt…   352MB
```

Nous remarquons à ligne 7 (`0f237d437944`), l'exécution de notre commande `$ mvn dependency:go-offline` dont le résultat a fait augmenter la taille de l'image de 34 Mo. Ceci est dû aux dépendances Java qui ont été téléchargées. Par conséquent, si nous modifions uniquement le code source de notre projet, seule la compilation des sources sera réalisée.

* Éditer la classe `fr.mickaelbaron.helloworldrestmicroservice.service.HelloWorldResource` et faites une modification (ajouter par exemple un espace), puis relancer la construction de l'image.

```console
$ docker build -t mickaelbaron/helloworldrestmicroservice .
...
Step 9/12 : ADD ["src", "/work/src"]
 ---> 0b5eb5a40241
Step 10/12 : RUN ["mvn", "package"]
 ---> Running in 39c1681dcef3
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building helloworldrestmicroservice 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ helloworldrestmicroservice ---
...
```

Vous noterez que la construction de l'image se fait plus rapidement, car les étapes précédant l'étape 9 sont en cache.

À cet étape, nous constatons que nous venons de construire une image Docker qui fournit Java dans sa version JDK, l'outil Maven, les dépendances Maven nécessaires à son fonctionnement, les classes compilées et les dépendances Maven pour l'exécution du projet *helloworldrestmicroservice*. Or, seuls ces deux deniers sont nécessaires pour la suite. Par ailleurs, la taille de l'image est quand même assez imposante : 706 Mo.

Pour résoudre ces problèmes, nous allons utiliser le Multi-Stage Build de Docker qui permet de découper un fichier *Dockerfile* en plusieurs phases. Dans notre cas, nous aurons ainsi une phase de compilation et une phase d'exécution. La phase exécution qui sera finale fournira une image Docker plus réduite puisqu'elle sera basée sur une image JRE et non JDK.

* Créer un fichier *DockerfileMSB* à la racine du projet *helloworldrestmicroservice*, éditer le fichier et saisir le contenu présenté ci-dessous.

```yaml
# Build env
FROM maven:3-jdk-8 AS build-java-stage
LABEL maintainer="Mickael BARON"

ADD pom.xml /work/pom.xml
WORKDIR /work
RUN ["mvn", "dependency:go-offline"]

ADD ["src", "/work/src"]
RUN ["mvn", "package"]

# Run env
FROM openjdk:8-jre-slim
COPY --from=build-java-stage /work/target/classes /classes/
COPY --from=build-java-stage /work/target/dependency/*.jar /dependency/

EXPOSE 8080
ENTRYPOINT ["java", "-cp", "classes:dependency/*", "com.kumuluz.ee.EeApplication"]
```

Donnons quelques détails sur le contenu de ce fichier. De la ligne 1 à la ligne 10, les instructions décrivent la phase de compilation qui porte le nom de `build-java-stage`. De la ligne 12 à la ligne 18, les instructions décrivent la phase d'exécution. Pour la phase de compilation, contrairement à notre premier fichier *Dockerfile*, nous sommes partis d'une image de base Maven. Il n'est donc pas nécessaire d'installer Maven. À la fin de cette phase de compilation, les classes compilées et les dépendances seront disponibles. Dans la phase d'exécution, ces fichiers et dépendances seront copiés depuis la première phase vers les répertoires _classes/_ et _dependency/_ (lignes 14 et 15).

* Nous allons construire l'image à partir du fichier *DockerfileMSB*, exécuter la ligne de commande suivante.

```console
$ docker build -t mickaelbaron/helloworldrestmicroservice -f DockerfileMSB .
Sending build context to Docker daemon  110.6kB
Step 1/12 : FROM maven:3-jdk-8 AS build-java-stage
 ---> c77985f27eaf
Step 2/12 : LABEL maintainer="Mickael BARON"
 ---> Using cache
 ---> 18f92fa6c3db
Step 3/12 : ADD pom.xml /work/pom.xml
 ---> Using cache
 ---> 3fe1ae36df19
Step 4/12 : WORKDIR /work
 ---> Using cache
 ---> 5e8d3303b024
Step 5/12 : RUN ["mvn", "dependency:go-offline"]
 ---> Using cache
 ---> cf79c4db5ad8
Step 6/12 : ADD ["src", "/work/src"]
 ---> Using cache
 ---> f414fc12fca2
Step 7/12 : RUN ["mvn", "package"]
 ---> Using cache
 ---> 77d18dc14f6b
Step 8/12 : FROM openjdk:8-jre-slim
 ---> 7c6b62cf60ee
Step 9/12 : COPY --from=build-java-stage /work/target/classes /classes/
 ---> Using cache
 ---> e5c2fcda00f0
Step 10/12 : COPY --from=build-java-stage /work/target/dependency/*.jar /dependency/
 ---> Using cache
 ---> 973604af82e1
Step 11/12 : EXPOSE 8080
 ---> Using cache
 ---> 78950f5169e1
Step 12/12 : ENTRYPOINT ["java", "-cp", "classes:dependency/*", "com.kumuluz.ee.EeApplication"]
 ---> Using cache
 ---> 4f6445d0688f
Successfully built 4f6445d0688f
Successfully tagged mickaelbaron/helloworldrestmicroservice:latest
```

* S'assurer que l'image a été correctement construite en exécutant la ligne de commande suivante.

```console
$ docker images
REPOSITORY                                       TAG                 IMAGE ID            CREATED             SIZE
mickaelbaron/helloworldrestmicroservice          latest              e04b1b2fc0aa        1 minutes ago       184MB
redis                                            latest              ce25c7293564        2 weeks ago         95MB
java                                             openjdk-8-jdk       d23bdf5b1b1b        23 months ago       643MB
```

Nous constatons que l'image construite a diminué en taille. Elle est passée de 706 Mo à 184 Mo.

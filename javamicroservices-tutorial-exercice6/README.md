# Exercice 6 : émettre et recevoir des événements

Nous allons enrichir le projet Java du service web *HelloWorld* de façon à ce qu'un message « HelloWorld » puisse être publié sur le bus d'événements de RabbitMQ à chaque fois que le service web de création est appelé. Nous allons également créer un nouveau microservice appelé **Log** (contenu dans le projet *helloworldlogmicroservice*) qui se chargera de réceptionner les événements envoyés au bus d'événements de RabbitMQ. Pour cela, nous allons utiliser un nouveau projet Java pour l'affichage des logs sur la console.

## But

* Publier un HelloWorld vers [RabbitMQ](https://www.rabbitmq.com/).
* Afficher le contenu d'un conteneur.

## Étapes à suivre

* Avant de continuer, nous allons arrêter et supprimer les conteneurs *rest* et *redis*. Exécuter les lignes de commande suivantes.

```bash
$ docker rm -f rest
rest
$ docker rm -f redis
redis
```

* Depuis l'environnement de développement Eclipse, modifier la classe `HelloWorldResource` pour ajouter l'attribut `currentProducer` et le contenu dans la méthode `addHelloWorld`.

```java
@Path("/helloworld")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    @Inject
    @Named("redis")
    private IHelloWorldDAO currentDAO;

    @Inject
    private IHelloWorldEventProducer currentProducer;

    @GET
    public Response getHelloWorlds() {
        return Response.ok(currentDAO.getHelloWorlds()).build();
    }

    @POST
    public Response addHelloWorld(HelloWorld newHelloWorld) {
        if (newHelloWorld != null) {
            newHelloWorld.setStartDate(new Date().toString());
        }

        currentDAO.addHelloWorld(newHelloWorld);
        currentProducer.sendMessage(newHelloWorld);

        return Response.status(Status.CREATED).build();
    }
}
```

* Mettre à jour l'image du microservice **Rest** défini dans le projet *helloworldrestmicroservice*, se placer à la racine du projet *helloworldrestmicroservice* et exécuter la ligne de commande suivante.

```console
$ docker build -t mickaelbaron/helloworldrestmicroservice -f DockerfileMSB .
[+] Building 10.4s (16/16) FINISHED
 => [internal] load build definition from DockerfileMSB                                                                                              0.0s
 => => transferring dockerfile: 87B                                                                                                                  0.0s
 => [internal] load .dockerignore                                                                                                                    0.0s
 => => transferring context: 2B                                                                                                                      0.0s
 => [internal] load metadata for docker.io/library/openjdk:11-jre-slim                                                                               0.0s
 => [internal] load metadata for docker.io/library/maven:3-jdk-11-slim                                                                               1.4s
 => [auth] library/maven:pull token for registry-1.docker.io                                                                                         0.0s
 => [stage-1 1/3] FROM docker.io/library/openjdk:11-jre-slim                                                                                         0.0s
 => [internal] load build context                                                                                                                    0.0s
 => => transferring context: 5.13kB                                                                                                                  0.0s
 => [build-java-stage 1/6] FROM docker.io/library/maven:3-jdk-11-slim@sha256:6b6505195afeda7a01257f8e9d9124c9f8feba4ed1d661c3af454142470fce40        0.0s
 => => resolve docker.io/library/maven:3-jdk-11-slim@sha256:6b6505195afeda7a01257f8e9d9124c9f8feba4ed1d661c3af454142470fce40                         0.0s
 => CACHED [build-java-stage 2/6] ADD pom.xml /work/pom.xml                                                                                          0.0s
 => CACHED [build-java-stage 3/6] WORKDIR /work                                                                                                      0.0s
 => CACHED [build-java-stage 4/6] RUN ["mvn", "dependency:go-offline"]                                                                               0.0s
 => [build-java-stage 5/6] ADD [src, /work/src]                                                                                                      0.0s
 => [build-java-stage 6/6] RUN ["mvn", "package"]                                                                                                    8.4s
 => CACHED [stage-1 2/3] COPY --from=build-java-stage /work/target/classes /classes/                                                                 0.0s
 => CACHED [stage-1 3/3] COPY --from=build-java-stage /work/target/dependency/*.jar /dependency/                                                     0.0s
 => exporting to image                                                                                                                               0.0s
 => => exporting layers                                                                                                                              0.0s
 => => writing image sha256:782a077b217c8eb11cd4d728c9ccd7561ac6e6384f9c72f059abde67ce0cf5c7                                                         0.0s
 => => naming to docker.io/mickaelbaron/helloworldrestmicroservice                                                                                   0.0s
```

* Importer le projet Maven *helloworldlogmicroservice* (**File -> Import -> General -> Existing Maven Projects**, choisir le répertoire du projet puis faire **Finish**).

* Examiner la classe `HelloWorldLogMicroservice`. Les événements reçus sont récupérés et affichés sur la sortie console.

* Créer un fichier *Dockerfile* à la racine du projet *helloworldlogmicroservice*.

* Ouvrrir un éditeur de texte et saisir le contenu présenté ci-dessous.

```yaml
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

ENTRYPOINT ["java", "-cp", "target/classes:target/dependency/*", "fr.mickaelbaron.helloworldlogmicroservice.HelloWorldLogMicroservice"]
CMD [localhost]
```

* Nous allons construire l'image à partir de fichier *Dockerfile*, exécuter la ligne de commande suivante depuis la racine du projet *helloworldlogmicroservice*.

```console
$ docker build -t mickaelbaron/helloworldlogmicroservice .
[+] Building 12.5s (15/15) FINISHED
 => [internal] load build definition from Dockerfile                                                                                               0.0s
 => => transferring dockerfile: 614B                                                                                                               0.0s
 => [internal] load .dockerignore                                                                                                                  0.0s
 => => transferring context: 2B                                                                                                                    0.0s
 => [internal] load metadata for docker.io/library/maven:3-jdk-11-slim                                                                             0.7s
 => [internal] load metadata for docker.io/library/openjdk:11-jre-slim                                                                             0.0s
 => [build-java-stage 1/6] FROM docker.io/library/maven:3-jdk-11-slim@sha256:6b6505195afeda7a01257f8e9d9124c9f8feba4ed1d661c3af454142470fce40      0.0s
 => => resolve docker.io/library/maven:3-jdk-11-slim@sha256:6b6505195afeda7a01257f8e9d9124c9f8feba4ed1d661c3af454142470fce40                       0.0s
 => [stage-1 1/3] FROM docker.io/library/openjdk:11-jre-slim                                                                                       0.0s
 => [internal] load build context                                                                                                                  0.0s
 => => transferring context: 35.46kB                                                                                                               0.0s
 => CACHED [build-java-stage 2/6] ADD pom.xml /work/pom.xml                                                                                        0.0s
 => CACHED [build-java-stage 3/6] WORKDIR /work                                                                                                    0.0s
 => CACHED [build-java-stage 4/6] RUN ["mvn", "dependency:go-offline"]                                                                             0.0s
 => [build-java-stage 5/6] ADD [src, /work/src]                                                                                                    0.0s
 => [build-java-stage 6/6] RUN ["mvn", "package"]                                                                                                 11.4s
 => CACHED [stage-1 2/3] COPY --from=build-java-stage /work/target/classes /classes/                                                               0.0s
 => CACHED [stage-1 3/3] COPY --from=build-java-stage /work/target/dependency/*.jar /dependency/                                                   0.0s
 => exporting to image                                                                                                                             0.0s
 => => exporting layers                                                                                                                            0.0s
 => => writing image sha256:816158c8043960d08ad31749c8c204a32fb8392cda2079836e6593980c88cad9                                                       0.0s
 => => naming to docker.io/mickaelbaron/helloworldlogmicroservice                                                                                  0.0s
```

* Nous ne pouvons pas continuer tant que le serveur [RabbitMQ](https://www.rabbitmq.com/) n’est pas mis en place. Nous allons donc l'installer en l'isolant dans un conteneur. Le microservice résultat s'appellera **Rabbitmq**. Nous utiliserons une image contenant une interface web pour la gestion des événements reçus et envoyés (*rabbitmq:management*).

```console
$ docker pull rabbitmq:management
management: Pulling from library/rabbitmq
...
68a28bc3dcb8: Pull complete
5ca5230423e6: Pull complete
Digest: sha256:fad7c1c55ac888e6037655b90bd00e1611faca38b080efa9e887cfe40bcfa6e1
Status: Downloaded newer image for rabbitmq:management
docker.io/library/rabbitmq:management
```

* Il ne nous reste plus qu'à créer tous les conteneurs **rest**, **redis** et **rabbitmq** et de les connecter au réseau Docker *helloworldnetwork*. Exécuter les lignes de commande suivantes en faisant attention d'être à la racine du répertoire _workspace_.

```console
$ docker run --name redis -d --network helloworldnetwork -v $(pwd)/data:/data redis redis-server --appendonly yes
$ docker run --name rabbitmq -d --network helloworldnetwork -p 5672:5672 -p 15672:15672 --hostname my-rabbit rabbitmq:management
$ docker run --name log -d --network helloworldnetwork mickaelbaron/helloworldlogmicroservice rabbitmq
$ docker run --name rest -d --network helloworldnetwork -p 8080:8080 --env REDIS_HOST=tcp://redis:6379 --env RABBITMQ_HOST=rabbitmq  mickaelbaron/helloworldrestmicroservice
```

* Assurons-nous que tous les conteneurs soient opérationnels en affichant le statut des conteneurs.

```console
$ docker ps
CONTAINER ID   IMAGE                                     COMMAND                  CREATED          STATUS          PORTS                                              NAMES
55f43670fcda   mickaelbaron/helloworldrestmicroservice   "java -cp classes:de…"   4 seconds ago    Up 4 seconds    0.0.0.0:8080->8080/tcp                             rest
3838dc67f40d   mickaelbaron/helloworldlogmicroservice    "java -cp classes:de…"   21 seconds ago   Up 21 seconds                                                      log
7279024ad548   rabbitmq:management                       "docker-entrypoint.s…"   5 minutes ago    Up 5 minutes    0.0.0.0:5672->5672/tcp, 0.0.0.0:15672->15672/tcp   rabbitmq
5fe0b069c901   redis                                     "docker-entrypoint.s…"   5 minutes ago    Up 5 minutes    6379/tcp                                           redis
```

* Assurons-nous également que l'interface d'administration de RabbitMQ fonctionne. Ouvrir un navigateur web et saisir l'adresse (utilisateur : *guest*, mot de passe : *guest*) <http://localhost:15672>.

![Interface d'administration RabbitMQ](./images/rabbitmq.png "Interface d'administration RabbitMQ")

* Appeler le service web *HelloWorld* pour tester la chaîne complète des microservices en exécutant les deux lignes de commandes.

```console
# Création d'un message « HelloWorld » à partir d'un contenu JSON
$ curl -H "Content-Type: application/json" -X POST -d '{"message":"Mon HelloWorld"}' http://localhost:8080/helloworld

# Lister les messages « HelloWorld »
$ curl http://localhost:8080/helloworld
[{"rid":3,"message":"Mon HelloWorld","startDate":"Sat Apr 30 06:41:24 UTC 2022"},{"rid":2,"message":"Mon HelloWorld","startDate":"Fri Apr 29 17:22:31 CEST 2022"},{"rid":1,"message":"Mon HelloWorld","startDate":"Fri Apr 29 17:21:06 CEST 2022"}]
```

* Afficher le contenu des logs du conteneur *log* en exécutant la commande suivante.

```console
$ docker logs log
 [x] Received '{"rid":4,"message":"Mon HelloWorld","startDate":"Sat Apr 30 06:41:24 UTC 2022"}'
```
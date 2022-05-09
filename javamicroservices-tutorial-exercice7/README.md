# Exercice 7 : composer tous les microservices avec DockerCompose

Dans les précédents exercices, nous avons procédé à la création de chaque image et nous avons ensuite créé les conteneurs associés. Toutes ces tâches ont été réalisées via les commandes `$ docker pull`, `$ docker build` et `$ docker run`. Comme nous avons pu le constater, cela reste utilisable quand il y a peu de conteneur, mais lorsqu'il y a plus de deux conteneurs cela devient difficile de tout gérer. C'est pour cette raison que nous allons employer la commande `compose` de l'outil **docker**.

## But

* Écrire un fichier _docker-compose.yml_.
* Utiliser la commande `$ docker compose` pour composer des conteneurs.

## Étapes à suivre

* Avant de commencer faire « table rase » en supprimant tous les conteneurs précédemment créés, exécuter la ligne de commande suivante.

```console
$ docker rm -f $(docker ps -q)
55f43670fcda
3838dc67f40d
7279024ad548
5fe0b069c901
```

* S'assurer que tous les conteneurs ont été supprimés en exécutant la ligne commande de suivante.

```console
$ docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
```

Nous allons également introduire un nouveau microservice qui se chargera de fournir une interface web à notre application : le microservice **Web**. Le code de cette application est dans le projet *helloworldwebmicroservice*. Le contenu du code est réalisé en HTML et [Vue.js](https://vuejs.org/). Les dépendances des bibliothèques JavaScript sont gérées par l'outil [Vite](https://vitejs.dev/).

* Parcourir les fichiers contenus dans le projet *helloworldwebmicroservice*. Vous remarquerez dans le fichier *App.vue* l'URL du microservice **Rest**. Dans notre cas il s'agit de l'URL http://localhost:8080/helloworld.

* Créer un fichier *docker-compose.yml* à la racine du répertoire *workspace*.

* Éditer le fichier et le compléter comme ci-dessous.

```yml
services:
  redis:
    image: redis:latest
    command: redis-server --appendonly yes
    volumes: 
      - ./data:/data
    networks:
      - helloworldnet

  rabbitmq:
    image: rabbitmq:management
    hostname: my-rabbit
    ports:
      - 5672:5672
      - 15672:15672
    networks:
      - helloworldnet
 
  rest:
    build:
      context: helloworldrestmicroservice/
      dockerfile: DockerfileMSB
    image: mickaelbaron/helloworldrestmicroservice:latest
    depends_on: 
      - rabbitmq
      - redis
    ports:
      - 8080:8080
    environment:
      REDIS_HOST: tcp://redis:6379
      RABBITMQ_HOST: rabbitmq
    networks:
      - helloworldnet

  web:
    build: helloworldwebmicroservice
    image: mickaelbaron/helloworldwebmicroservice:latest
    ports:
      - 80:80
    networks:
      - helloworldnet

  log:
    build: helloworldlogmicroservice/
    image: mickaelbaron/helloworldlogmicroservice:latest
    depends_on:
      - rabbitmq
    command: rabbitmq
    networks:
      - helloworldnet

networks:
  helloworldnet:
    name: helloworldnetwork
```

Veuillez noter deux choses :

1. toutes les options passées en paramètres de la commande `$ docker run` des exercices précédents sont réutilisées ;
2. le paramètre `build` permet de construire l'image en se basant sur les fichiers *Dockerfile* présents dans les répertoires des projets.

* Nous pouvons donc exécuter ce fichier en utilisant la commande `$ docker compose up -d` comme précisé ci-dessous (s'assurer d'être à la racine du répertoire _workspace_).

```console
$ docker compose up -d
[+] Running 0/1
 ⠿ web Error                                                                                     2.1s
[+] Building 1.6s (17/17) FINISHED
 => [internal] load build definition from Dockerfile                                             0.0s
 => => transferring dockerfile: 378B                                                             0.0s
 => [internal] load .dockerignore                                                                0.0s
 => => transferring context: 2B                                                                  0.0s
 => [internal] load metadata for docker.io/library/node:slim                                     1.4s
 => [internal] load metadata for docker.io/library/nginx:latest                                  0.0s
 => [auth] library/node:pull token for registry-1.docker.io                                      0.0s
 => [build-npm-stage 1/8] FROM docker.io/library/node:slim@sha256:12e96de019870095e76f9a101e86   0.0s
 => [stage-1 1/2] FROM docker.io/library/nginx:latest                                            0.0s
 => [internal] load build context                                                                0.0s
 => => transferring context: 19.81kB                                                             0.0s
 => CACHED [build-npm-stage 2/8] WORKDIR /web                                                    0.0s
 => CACHED [build-npm-stage 3/8] COPY package.json ./                                            0.0s
 => CACHED [build-npm-stage 4/8] RUN npm install                                                 0.0s
 => CACHED [build-npm-stage 5/8] COPY index.html ./                                              0.0s
 => CACHED [build-npm-stage 6/8] COPY src ./src                                                  0.0s
 => CACHED [build-npm-stage 7/8] COPY vite.config.js ./                                          0.0s
 => CACHED [build-npm-stage 8/8] RUN npm run build                                               0.0s
 => CACHED [stage-1 2/2] COPY --from=build-npm-stage /web/dist /usr/share/nginx/html             0.0s
 => exporting to image                                                                           0.0s
 => => exporting layers                                                                          0.0s
 => => writing image sha256:2b3ca420eb019a865a0f3a922be1e0b2d5477ab28684e9975348dfe8b5f87d7e     0.0s
 => => naming to docker.io/mickaelbaron/helloworldwebmicroservice:latest                         0.0s
[+] Running 5/5
 ⠿ Container workspace-rabbitmq-1  Started                                                       1.0s
 ⠿ Container workspace-web-1       Started                                                       1.1s
 ⠿ Container workspace-redis-1     Started                                                       1.2s
 ⠿ Container workspace-log-1       Started                                                       8.0s
 ⠿ Container workspace-rest-1      Started                                                       7.7s
```

Vous remarquerez que l'image correspondant au microservice **Web** est construite, car elle n'existe pas encore.

* Afficher les logs des conteneurs en exécutant la ligne de commande suivante.

```console
$ docker compose logs
...
workspace-web-1  | 2022/05/09 14:06:53 [notice] 1#1: start worker processes
workspace-web-1  | 2022/05/09 14:06:53 [notice] 1#1: start worker process 31
workspace-web-1  | 2022/05/09 14:06:53 [notice] 1#1: start worker process 32
workspace-log-1  | SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
workspace-log-1  | SLF4J: Defaulting to no-operation (NOP) logger implementation
workspace-log-1  | SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
workspace-log-1  | Exception in thread "main" java.net.ConnectException: Connection refused (Connection refused)
...
```

Pour différencier de quelle sortie les logs sont issus, il est précisé sur la zone de gauche le nom du conteneur en suivant la convention `<répertoire courant>-<nom>-<indice>`. `<nom>` précise le nom du conteneur et `<indice>` précise de quelle instance du conteneur il s'agit. Cette information est importante quand vous faites usage de l'outil `$ docker compose scale` pour augmenter le nombre d'instance d'un conteneur.

* Vérifier que tous les conteneurs ont été correctement créés en exécutant la ligne commande suivante.

```console
$ docker compose ps
NAME                   COMMAND                  SERVICE             STATUS              PORTS
workspace-log-1        "java -cp classes:de…"   log                 exited (1)
workspace-rabbitmq-1   "docker-entrypoint.s…"   rabbitmq            running (healthy)   0.0.0.0:5672->5672/tcp, 0.0.0.0:15672->15672/tcp
workspace-redis-1      "docker-entrypoint.s…"   redis               running (healthy)   6379/tcp
workspace-rest-1       "java -cp classes:de…"   rest                running             0.0.0.0:8080->8080/tcp
workspace-web-1        "/docker-entrypoint.…"   web                 running             0.0.0.0:80->80/tcp
```

Nous remarquons que le conteneur `microservices_log_1` est arrêté (State = Exit 1).

* Examinons les logs du conteneur du microservice **Log** pour comprendre la raison de son arrêt. Exécuter la ligne de commande suivante.

```console
$ docker logs workspace_log_1
Exception in thread "main" java.net.ConnectException: Connection refused (Connection refused)
	at java.base/java.net.PlainSocketImpl.socketConnect(Native Method)
	at java.base/java.net.AbstractPlainSocketImpl.doConnect(Unknown Source)
	at java.base/java.net.AbstractPlainSocketImpl.connectToAddress(Unknown Source)
	at java.base/java.net.AbstractPlainSocketImpl.connect(Unknown Source)
	at java.base/java.net.SocksSocketImpl.connect(Unknown Source)
	at java.base/java.net.Socket.connect(Unknown Source)
	at com.rabbitmq.client.impl.SocketFrameHandlerFactory.create(SocketFrameHandlerFactory.java:60)
	at com.rabbitmq.client.impl.recovery.RecoveryAwareAMQConnectionFactory.newConnection(RecoveryAwareAMQConnectionFactory.java:62)
	at com.rabbitmq.client.impl.recovery.AutorecoveringConnection.init(AutorecoveringConnection.java:156)
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1104)
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1063)
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1021)
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1180)
	at fr.mickaelbaron.helloworldlogmicroservice.HelloWorldLogMicroservice.<init>(HelloWorldLogMicroservice.java:26)
	at fr.mickaelbaron.helloworldlogmicroservice.HelloWorldLogMicroservice.main(HelloWorldLogMicroservice.java:72)
```

Le microservice **Log** n'arrive pas à se connecter au serveur RabbitMQ puisque ce dernier a un temps de démarrage relativement long. Par conséquent la connexion au serveur RabbitMQ est faite trop tôt. Pour pallier à ce problème, nous allons modifier le programme Java afin de pouvoir tenter une nouvelle connexion en cas d'échec. Cette solution est connue sous le nom de *healthcheck*.

* Ouvrir et éditer la classe `HelloWorldLogMicroservice` et compléter/ajouter le code suivant.

```java
public class HelloWorldLogMicroservice {

    ...

    public HelloWorldLogMicroservice(String rabbitMQHosts) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQHosts);

        final Connection connection = createConnection(factory);
        final Channel channel = connection.createChannel();

        ...
    }

    private Connection createConnection(ConnectionFactory factory) throws InterruptedException {
        // We implement an healthcheck.

        boolean connectionIsReady = false;
        Connection connection = null;
        while (!connectionIsReady) {
            try {
                connection = factory.newConnection();
                connectionIsReady = true;
            } catch (Exception e) {
                System.out.println("Problem:" + e.getMessage());
                System.out.println("We will try to connect to RabbitMQ in 5s.");
                Thread.sleep(5000);
            }
        }

        System.out.println("Great !! Connected to RabbitMQ.");

        return connection;
    }

    ...
}
```

* Pour recompiler uniquement l'image de **Log**, exécuter la ligne de commande suivante.

```console
$ docker compose build log
[+] Building 2.4s (16/16) FINISHED
```

* Pour recréer les conteneurs, exécuter la ligne de commande suivante.

```console
$ docker compose up -d
```

* Vérifier que les conteneurs ont correctement été créés en exécutant la ligne de commande suivante.

```console
$ docker compose ps
NAME                   COMMAND                  SERVICE             STATUS              PORTS
workspace-log-1        "java -cp classes:de…"   log                 running
workspace-rabbitmq-1   "docker-entrypoint.s…"   rabbitmq            running (healthy)   0.0.0.0:5672->5672/tcp, 0.0.0.0:15672->15672/tcp
workspace-redis-1      "docker-entrypoint.s…"   redis               running (healthy)   6379/tcp
workspace-rest-1       "java -cp classes:de…"   rest                running             0.0.0.0:8080->8080/tcp
workspace-web-1        "/docker-entrypoint.…"   web                 running             0.0.0.0:80->80/tcp
```

* Il ne nous reste plus qu'à tester. Ouvrir un navigateur et rendez-vous à cette adresse : <http://localhost>.

# Exercice 7 : composer tous les microservices avec Docker Compose

Dans les précédents exercices, nous avons procédé à la création de chaque image et nous avons ensuite créé les conteneurs associés. Toutes ces tâches ont été réalisées via les commandes `docker pull`, `docker build` et `docker run`. Comme nous avons pu le constater, cela reste utilisable quand il y a peu de conteneur, mais lorsqu'il y a plus de deux conteneurs cela devient répétifif et long de tout gérer. C'est pour cette raison que nous allons employer la commande `compose` de l'outil **docker**.

## But

- Écrire un fichier _compose.yaml_.
- Utiliser la commande `docker compose` pour créer des conteneurs.

## Étapes à suivre

- Avant de commencer, faire « table rase » en supprimant tous les conteneurs précédemment créés.

```bash
docker rm -f $(docker ps -q)
```

La sortie console attendue :

```bash
5d3d9a7dbc69
05a5018805aa
83eb979c7bea
72598456d104
```

- S'assurer que tous les conteneurs ont été supprimés en exécutant la ligne commande de suivante.

```bash
docker ps
```

La sortie console attendue :

```
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
```

- Supprimer également le réseau Docker appelé _helloworldnetwork_.

```bash
docker network rm helloworldnetwork
```

Nous allons également introduire un nouveau microservice qui se chargera de fournir une interface web à notre application : le microservice **Web**. Le code de cette application est dans le projet _helloworldwebmicroservice_. Le contenu du code est réalisé en HTML et [Vue.js](https://vuejs.org/). Les dépendances des bibliothèques JavaScript sont gérées par l'outil [Vite](https://vitejs.dev/).

- Parcourir les fichiers contenus dans le projet _helloworldwebmicroservice_. Vous remarquerez dans le fichier _App.vue_ l'URL du microservice **Rest**. Dans notre cas il s'agit de l'URL <http://localhost:9080/helloworld>.

- Créer un fichier _compose.yaml_ à la racine du répertoire _workspace_.

- Éditer le fichier et le compléter comme ci-dessous.

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
    image: mickaelbaron/helloworldrestmicroservice:msb
    depends_on: 
      - rabbitmq
      - redis
    ports:
      - 9080:9080
    environment:
      REDIS_HOST: tcp://redis:6379
      RABBITMQ_HOST: amqp://rabbitmq:5672
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
    command: amqp://rabbitmq:5672
    networks:
      - helloworldnet

networks:
  helloworldnet:
    name: helloworldnetwork
```

Veuillez noter deux choses :

1. toutes les options passées en paramètres de la commande `docker run` des exercices précédents sont réutilisées ;
2. le paramètre `build` permet de construire l'image en se basant sur les fichiers _Dockerfile_ présents dans les répertoires des projets.

3. Nous pouvons donc exécuter ce fichier en utilisant la commande `docker compose up -d` comme précisé ci-dessous (s'assurer d'être à la racine du répertoire _workspace_).

```bash
docker compose up -d
```

La sortie console attendue :

```bash
[+] Running 1/1
 ! web Warning failed to resolve reference "docker.io/mickaelbaron/helloworldwebmicroservice:latest": not found                   2.5s
[+] Building 28.5s (17/17) FINISHED                                                                               docker:desktop-linux
 => [web internal] load build definition from Dockerfile                                                                          0.0s
 => => transferring dockerfile: 378B                                                                                              0.0s
 => WARN: FromAsCasing: 'as' and 'FROM' keywords' casing do not match (line 2)                                                    0.0s
 => [web internal] load metadata for docker.io/library/nginx:latest                                                               1.5s
 => [web internal] load metadata for docker.io/library/node:slim                                                                  1.3s
 => [web internal] load .dockerignore                                                                                             0.0s
 => => transferring context: 2B                                                                                                   0.0s
 => [web build-npm-stage 1/8] FROM docker.io/library/node:slim@sha256:...                                                        14.2s
 => => resolve docker.io/library/node:slim@sha256:...                                                                             0.0s
 => => sha256:2ecca7445a6029810336f53d64dcc2200a7f7c7ef152b722c08ec4578a86bb4b 1.71MB / 1.71MB                                    0.8s
 => => sha256:c44344f6dcc7710f22f451fa87d14f3688af426021184d765a60f0b05d0b8fe9 446B / 446B                                        0.3s
 => => sha256:4f94b241558a269157c87a3c504b0d5de1ebf1678a3c51786509e22e336c4b8c 3.31kB / 3.31kB                                    0.3s
 => => sha256:7ce63c5f638907c71bd39fc62c98ec65f8cd31df5c77e403586d4a9921646a89 49.56MB / 49.56MB                                 13.6s
 => => extracting sha256:4f94b241558a269157c87a3c504b0d5de1ebf1678a3c51786509e22e336c4b8c                                         0.0s
 => => extracting sha256:7ce63c5f638907c71bd39fc62c98ec65f8cd31df5c77e403586d4a9921646a89                                         0.5s
 => => extracting sha256:2ecca7445a6029810336f53d64dcc2200a7f7c7ef152b722c08ec4578a86bb4b                                         0.0s
 => => extracting sha256:c44344f6dcc7710f22f451fa87d14f3688af426021184d765a60f0b05d0b8fe9                                         0.0s
 => [web stage-1 1/2] FROM docker.io/library/nginx:latest@sha256:...                                                             13.4s
 => => resolve docker.io/library/nginx:latest@sha256:...                                                                          0.0s
 ...
 => [web internal] load build context                                                                                             0.0s
 => => transferring context: 19.86kB                                                                                              0.0s
 => [web build-npm-stage 2/8] WORKDIR /web                                                                                        0.1s
 => [web build-npm-stage 3/8] COPY package.json ./                                                                                0.0s
 => [web build-npm-stage 4/8] RUN npm install                                                                                    12.0s
 => [web build-npm-stage 5/8] COPY index.html ./                                                                                  0.0s
 => [web build-npm-stage 6/8] COPY src ./src                                                                                      0.0s
 => [web build-npm-stage 7/8] COPY vite.config.js ./                                                                              0.0s
 => [web build-npm-stage 8/8] RUN npm run build                                                                                   0.5s
 => [web stage-1 2/2] COPY --from=build-npm-stage /web/dist /usr/share/nginx/html                                                 0.0s
 => [web] exporting to image                                                                                                      0.0s
 => => exporting layers                                                                                                           0.0s
 => => exporting manifest sha256:560e6cf9845291767ce73be1e0af230c531a52b44246cdf709b8a67f032c29e9                                 0.0s
 => => exporting config sha256:bebec5be71a6dc03a25ae7e3ebca0f305dbdb5a3312299da9119e48a63b00026                                   0.0s
 => => exporting attestation manifest sha256:ab078aea049e7daf978267c4db850328acabfd7d7dfc620967734ad01a6157ca                     0.0s
 => => exporting manifest list sha256:2b2cccfb0b02b32a4ee95337551b429703fffc9405555f54574daf6981b1e1c0                            0.0s
 => => naming to docker.io/mickaelbaron/helloworldwebmicroservice:latest                                                          0.0s
 => => unpacking to docker.io/mickaelbaron/helloworldwebmicroservice:latest                                                       0.0s
 => [web] resolving provenance for metadata file                                                                                  0.0s
[+] Running 7/7
 ✔ web                             Built                                                                                          0.0s
 ✔ Network helloworldnetwork       Created                                                                                        0.0s
 ✔ Container workspace-redis-1     Started                                                                                        3.7s
 ✔ Container workspace-web-1       Started                                                                                        0.2s
 ✔ Container workspace-rabbitmq-1  Started                                                                                        5.7s
 ✔ Container workspace-log-1       Started                                                                                        5.7s
 ✔ Container workspace-rest-1      Started                                                                                        5.7s
```

Vous remarquerez que l'image correspondant au microservice **Web** est construite, car elle n'existait pas encore.

- Afficher les logs des conteneurs en exécutant la ligne de commande suivante.

```bash
docker compose logs
```

La sortie console attendue :

```bash
...
log-1  | SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
log-1  | SLF4J: Defaulting to no-operation (NOP) logger implementation
log-1  | SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
log-1  | Exception in thread "main" java.net.ConnectException: Connection refused (Connection refused)
...
```

Pour différencier de quelle sortie les logs sont issus, il est précisé sur la zone de gauche le nom du conteneur en suivant la convention `<nom>-<indice>`. `<nom>` précise le nom du conteneur défini dans le fichier _compose.yaml_ et `<indice>` précise de quelle instance du conteneur il s'agit. Cette information est importante quand vous faites usage de l'outil `$ docker compose scale` pour augmenter le nombre d'instance d'un conteneur.

- Vérifier que tous les conteneurs ont été correctement créés en exécutant la ligne commande suivante. L'option `-a` permet d'afficher également les conteneurs qui sont dans l'état terminé.

```bash
docker compose ps -a
```

La sortie console attendue :

```bash
NAME                   IMAGE                                           COMMAND                  SERVICE    CREATED         STATUS                   PORTS
workspace-log-1        mickaelbaron/helloworldlogmicroservice:latest   "java -cp classes:de…"   log        6 minutes ago   Exited (1) 6 minutes ago
workspace-rabbitmq-1   rabbitmq:management                             "docker-entrypoint.s…"   rabbitmq   5 minutes ago   Up 5 minutes (healthy)   ..., 0.0.0.0:15672->15672/tcp
workspace-redis-1      redis:latest                                    "docker-entrypoint.s…"   redis      5 minutes ago   Up 5 minutes (healthy)   6379/tcp
workspace-rest-1       mickaelbaron/helloworldrestmicroservice:msb     "/opt/ol/helpers/run…"   rest       5 minutes ago   Up 5 minutes             0.0.0.0:9080->9080/tcp, 9443/tcp
workspace-web-1        mickaelbaron/helloworldwebmicroservice:latest   "/docker-entrypoint.…"   web        5 minutes ago   Up 5 minutes             0.0.0.0:80->80/tcp
```

Nous remarquons que le conteneur `workspace-log-1` est arrêté (State = Exit 1). Le préfixe `workspace` est donné en fonction du nom du dossier courant.

- Examinons les logs du conteneur du microservice **Log** pour comprendre la raison de son arrêt.

```bash
docker compose logs log
```

La sortie console attendue :

```bash
log-1  | SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
log-1  | SLF4J: Defaulting to no-operation (NOP) logger implementation
log-1  | SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
log-1  | Exception in thread "main" java.net.ConnectException: Connection refused (Connection refused)
log-1  | 	at java.base/java.net.PlainSocketImpl.socketConnect(Native Method)
log-1  | 	at java.base/java.net.AbstractPlainSocketImpl.doConnect(Unknown Source)
log-1  | 	at java.base/java.net.AbstractPlainSocketImpl.connectToAddress(Unknown Source)
log-1  | 	at java.base/java.net.AbstractPlainSocketImpl.connect(Unknown Source)
log-1  | 	at java.base/java.net.SocksSocketImpl.connect(Unknown Source)
log-1  | 	at java.base/java.net.Socket.connect(Unknown Source)
log-1  | 	at com.rabbitmq.client.impl.SocketFrameHandlerFactory.create(SocketFrameHandlerFactory.java:61)
log-1  | 	at com.rabbitmq.client.impl.recovery.RecoveryAwareAMQConnectionFactory.newConnection(RecoveryAwareAMQConnectionFactory.java:63)
log-1  | 	at com.rabbitmq.client.impl.recovery.AutorecoveringConnection.init(AutorecoveringConnection.java:160)
log-1  | 	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1227)
log-1  | 	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1184)
log-1  | 	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1142)
log-1  | 	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1306)
log-1  | 	at fr.mickaelbaron.helloworldlogmicroservice.HelloWorldLogMicroservice.<init>(HelloWorldLogMicroservice.java:37)
log-1  | 	at fr.mickaelbaron.helloworldlogmicroservice.HelloWorldLogMicroservice.main(HelloWorldLogMicroservice.java:95)
```

> À noter que la commande `docker logs` peut également s'appliquer à condition d'utiliser le nom explicite du conteneur à savoir `workspace-log-1`.

Le microservice **Log** n’arrive pas à se connecter au serveur [RabbitMQ](https://www.rabbitmq.com/), car celui-ci met un certain temps à démarrer. Contrairement au microservice **Rest**, qui établit sa connexion uniquement lors de l’exécution d’une requête, **Log** tente de se connecter dès le démarrage de l’application. Par conséquent la connexion au serveur [RabbitMQ](https://www.rabbitmq.com/) est faite trop tôt. Pour pallier à ce problème, deux possibilités s'offrent à nous :

1. modifier le programme Java afin de pouvoir tenter une nouvelle connexion en cas d'échec. Cette solution est connue sous le nom de _healthcheck_.
2. modifier le fichier `compose.yaml` pour introduire une condition `service_healthy` au niveau de la dépendance des services. 

- Ouvrir et éditer la classe `HelloWorldLogMicroservice` et compléter/ajouter le code suivant.

```java
public class HelloWorldLogMicroservice {

    ...

    public HelloWorldLogMicroservice(String rabbitMQUri) {
		    try {
			    ConnectionFactory factory = new ConnectionFactory();
			    factory.setUri(rabbitMQUri);

			    Connection connection = createConnection(factory);
		      // Connection connection = createConnection(factory); 
        ...
    }

	  private Connection createConnection(ConnectionFactory factory) throws InterruptedException {
		  // We implement an healthcheck.
		  boolean connectionIsReady = false;
		  Connection connection = null;
		  int attempt = 0;

		  while (!connectionIsReady && attempt < maxAttempts) {
			  try {
				  connection = factory.newConnection();
				  connectionIsReady = true;
			  } catch (Exception e) {
				  attempt++;
				  System.out.println("Attempt " + attempt + " failed: " + e.getMessage());
				  if (attempt < maxAttempts) {
					  System.out.println("Retrying to connect to RabbitMQ in 5s...");
					  try {
						  Thread.sleep(5000);
					  } catch (InterruptedException ie) {
						  Thread.currentThread().interrupt(); // Restore interrupt status
						  break;
					  }
				  } else {
					  System.out.println("Max connection attempts reached. Aborting.");
					  System.exit(-1);
				  }
			  }
		  }

		  System.out.println("Great !! Connected to RabbitMQ.");

		  return connection;
	  }
    ...
}
```

- Arrêter tous les services avant de continuer.

```bash
docker compose down
```

La sortie console attendue :

```bash
[+] Running 6/6
 ✔ Container workspace-web-1       Removed      0.2s
 ✔ Container workspace-rest-1      Removed      1.4s
 ✔ Container workspace-log-1       Removed      0.0s
 ✔ Container workspace-rabbitmq-1  Removed      1.2s
 ✔ Container workspace-redis-1     Removed      0.1s
 ✔ Network helloworldnetwork       Removed
```

- Pour recompiler uniquement l'image du microservice **Log**, exécuter la ligne de commande suivante.

```bash
docker compose build log
```

La sortie console attendue :

```bash
[+] Building 2.4s (16/16) FINISHED        docker:desktop-linux
...
[+] Building 1/1
 ✔ log  Built                                              0.0s
```

- Pour recréer les conteneurs, exécuter la ligne de commande suivante.

```bash
docker compose up -d
```

- Vérifier que les conteneurs ont correctement été créés.

```bash
docker compose ps
```

La sortie console attendue :

```bash
NAME                   COMMAND                  SERVICE             STATUS              PORTS
workspace-log-1        "java -cp classes:de…"   log                 running
workspace-rabbitmq-1   "docker-entrypoint.s…"   rabbitmq            running (healthy)   0.0.0.0:5672->5672/tcp, 0.0.0.0:15672->15672/tcp
workspace-redis-1      "docker-entrypoint.s…"   redis               running (healthy)   6379/tcp
workspace-rest-1       "java -cp classes:de…"   rest                running             0.0.0.0:9080->9080/tcp
workspace-web-1        "/docker-entrypoint.…"   web                 running             0.0.0.0:80->80/tcp
```

- Examinons de nouveau les logs du conteneur du microservice **Log** pour vérifier que le problème est résolu.

```bash
docker compose logs
```

La sortie console attendue :

```bash
...
log-1  | SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
log-1  | SLF4J: Defaulting to no-operation (NOP) logger implementation
log-1  | SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
log-1  | Attempt 1 failed: Connection refused (Connection refused)
log-1  | Retrying to connect to RabbitMQ in 5s...
log-1  | Great !! Connected to RabbitMQ.
...
```

Le microservice **Log** a dû effectuer deux tentatives avant d'établir la connexion. Voyons maintenant comment le fichier `compose.yaml` peut être utilisé pour éviter ce type de problème lié à un service non encore démarré.

- Arrêter tous les services.

```bash
docker compose down
```

- Créer un nouveau fichier nommé `composewithsh.yaml` et copier le contenu suivant.

```bash
services:
  redis:
    image: redis:latest
    command: redis-server --appendonly yes
    volumes: 
      - ./data:/data
    healthcheck:
      test: ["CMD", "redis-cli","ping"]
      interval: 3s
      timeout: 5s
      retries: 3
    networks:
      - helloworldnet

  rabbitmq:
    image: rabbitmq:management
    hostname: my-rabbit
    ports:
      - 15672:15672
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics","-q", "ping"]
      interval: 5s
      timeout: 5s
      retries: 3
    networks:
      - helloworldnet
 
  rest:
    build:
      context: helloworldrestmicroservice/
      dockerfile: DockerfileMSB
    image: mickaelbaron/helloworldrestmicroservice:msb
    depends_on: 
      rabbitmq:
        condition: service_healthy
      redis:
        condition: service_healthy
    ports:
      - 9080:9080
    environment:
      REDIS_HOST: tcp://redis:6379
      RABBITMQ_HOST: amqp://rabbitmq:5672
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
      rabbitmq:
        condition: service_healthy
    command: amqp://rabbitmq:5672
    networks:
      - helloworldnet

networks:
  helloworldnet:
    name: helloworldnetwork
```

Le paramètre `depends_on` des services `rest` et `log` permet de déclarer des dépendances entre service. Il est possible de définir des conditions. Dans ce cas la valeur `service_healthy` permet d'attendre que le conteneur relatif au service `rabbitmq` est démarré et son état est passé à l'état `healthcheck`. Cela permet d'assurer que le serveur [RabbitMQ](https://www.rabbitmq.com/) est démarré.

- Créer tous les conteneurs à partir de ce nouveau fichier 

```bash
docker compose -f composewithsh.yaml up -d
```

La sortie console attendue :

```bash
[+] Running 6/6
 ✔ Network helloworldnetwork       Created      0.0s
 ✔ Container workspace-web-1       Started      0.3s
 ✔ Container workspace-redis-1     Healthy      3.8s
 ✔ Container workspace-rabbitmq-1  Healthy      5.8s
 ✔ Container workspace-rest-1      Started      5.8s
 ✔ Container workspace-log-1       Started      5.8s
 ```

Lors de la création des conteneurs les conteneurs `workspace-rest-1` et `workspace-log-1` sont mis en attente tant que `workspace-rabbitmq-1` et, pour `workspace-rest-1`, `workspace-redis-1` soient dans l'état `Healthy`.

- Il ne nous reste plus qu'à tester. Ouvrir un navigateur et rendez-vous à cette adresse : <http://localhost>.

## Avez-vous bien compris ?

Pour continuer sur les concepts présentés dans cet exercice, nous proposons les expérimentations suivantes :

- compléter le paramètre `depends_on` pour redémarer les services `rest` et `log` dés lors que le service `rabbitmq` redémarre ;

- ajouter un nouveau service relatif au microservice **Email** que vous aurez développé dans le cadre de la section _Avez-vous bien compris ?_ de l'exercice précédent.
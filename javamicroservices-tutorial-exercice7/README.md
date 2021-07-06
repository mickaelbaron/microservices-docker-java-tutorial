# Exercice 7 : composer tous les microservices avec DockerCompose

Dans les précédents exercices, nous avons procédé à la création de chaque image et nous avons ensuite créé les conteneurs associés. Toutes ces tâches ont été réalisées via les commandes **docker pull**, **docker build** et **docker run**. Comme nous avons pu le constater, cela reste utilisable quand il y a peu de conteneur, mais lorsqu'il y a plus de deux conteneurs cela devient difficile de tout gérer. C'est pour cette raison que nous allons employer l'outil **docker-compose**.

## But

* Écrire un fichier *docker-compose.yml*.

* Utiliser l'outil **docker-compose** pour composer des conteneurs Docker.

## Étapes à suivre

* Avant de commencer faire « table rase » en supprimant tous les conteneurs précédemment créés, exécuter la ligne de commande suivante.

```console
$ docker rm -f $(docker ps -q)
d3a1e7f0594b
3b6430dd7c62
ac2314ccc4bf
57f69c0deabe
```

* S'assurer que tous les conteneurs ont été supprimés en exécutant la ligne commande de suivante.

```console
$ docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
```

Nous allons également introduire un nouveau microservice qui se chargera de fournir une interface web à notre application : le microservice **Web**. Le code de cette application est dans le projet *helloworldwebmicroservice*. Le contenu du code est réalisé en HTML, JavaScript, Bootstrap et JQuery. Les dépendances des bibliothèques JavaScript sont gérées par l'outil **Bower** (<http://bower.io/>) et la version distribuable du projet est obtenue par des tâches **Grunt** (<http://gruntjs.com/>).

* Parcourir les fichiers contenus dans le projet *helloworldwebmicroservice*. Vous remarquerez dans le fichier *resthosts.js* une variable *restHostUrl* qui prend la valeur de l'URL du microservice **Rest**. Dans notre cas il s'agit de l'URL http://localhost:8080/helloworld. Modifier la valeur de cette variable.

* Créer un fichier *docker-compose.yml* à la racine du répertoire *workspace*.

* Éditer le fichier et le compléter comme ci-dessous.

```yml
version: '3'

services:
  redis:
    image: redis
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
    build: helloworldrestmicroservice/
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
    build: helloworldwebmicroservice/
    image: mickaelbaron/helloworldwebmicroservice:latest
    ports:
      - 80:8080
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
    external:
      name: helloworldnetwork
```

Veuillez noter deux choses :

1. toutes les options passées en paramètres de la commande `docker run` des exercices précédents sont réutilisées ;
2. le paramètre `build` permet de construire l'image en se basant sur les fichiers *Dockerfile* présents dans les répertoires des projets.

* Nous pouvons donc exécuter ce fichier en utilisant la commande `docker-compose up` comme précisé ci-dessous (s'assurer d'être à la racine du répertoire *workspace*).

```console
$ docker-compose up -d
Building web
Step 1/10 : FROM node:latest
latest: Pulling from library/node
cd8eada9c7bb: Pull complete
c2677faec825: Pull complete
fcce419a96b1: Pull complete
045b51e26e75: Pull complete
83aa5374cd04: Pull complete
bb9752e24b3a: Pull complete
04b2f7baa231: Pull complete
4dad5ba692b4: Pull complete
Digest: sha256:fa9ac2ee6b4bab070e669f5a88da97cad75e9ed6fa785a5b3dc22b42d6c41149
Status: Downloaded newer image for node:latest
 ---> 2718f90558b7
Step 2/10 : LABEL MAINTAINER="Mickael BARON"
 ---> Running in 8875e6cecd05
Removing intermediate container 8875e6cecd05
 ---> e8634d57c589
Step 3/10 : RUN npm install -g grunt-cli && npm install -g http-server
 ---> Running in 95993e32cc3f
/usr/local/bin/grunt -> /usr/local/lib/node_modules/grunt-cli/bin/grunt
...
Creating workspace_web_1      ... done
Creating workspace_redis_1    ... done
Creating workspace_rabbitmq_1 ... done
Creating workspace_log_1      ... done
Creating workspace_rest_1     ... done
```

* Afficher les logs des conteneurs en exécutant la ligne de commande suivante.

```console
$ docker-compose logs
redis_1     | 1:M 31 Dec 2018 14:34:55.652 * Ready to accept connections
web_1       | Starting up http-server, serving /workdir/site
web_1       | Available on:
web_1       |   http://127.0.0.1:8080
web_1       |   http://172.19.0.4:8080
web_1       | Hit CTRL-C to stop the server
...
```

Pour différencier de quelle sortie les logs sont issus, il est précisé sur la zone de gauche le nom du conteneur en suivant la convention `<nom>_<indice>`. `<name>` précise le nom du conteneur et `<indice>` précise de quelle instance du conteneur il s'agit. Cette information est importante quand vous faites usage de l'outil `docker-compose scale` pour augmenter le nombre d'instance d'un conteneur. Nous étudierons ce point dans un prochain tutoriel.

* Vérifier que tous les conteneurs ont été correctement créés en exécutant la ligne commande suivante.

```console
$ docker-compose ps
        Name                      Command               State                                 Ports
---------------------------------------------------------------------------------------------------------------------------------
workspace_log_1        java -cp target/classes:ta ...   Exit 1
workspace_rabbitmq_1   docker-entrypoint.sh rabbi ...   Up       15671/tcp, 0.0.0.0:15672->15672/tcp, ..., 0.0.0.0:5672->5672/tcp
workspace_redis_1      docker-entrypoint.sh redis ...   Up       6379/tcp
workspace_rest_1       java -cp target/classes:ta ...   Up       0.0.0.0:8080->8080/tcp
workspace_web_1        http-server /workdir/site  ...   Up       0.0.0.0:80->8080/tcp
```

Nous remarquons que le conteneur `microservices_log_1` est arrêté (State = Exit 1).

* Examinons les logs du conteneur du microservice **Log** pour comprendre la raison de son arrêt. Exécuter la ligne de commande suivante.

```console
$ docker-compose logs log
Attaching to workspace_log_1
log_1       | Exception in thread "main" java.net.ConnectException: Connection refused (Connection refused)
log_1       | 	at java.net.PlainSocketImpl.socketConnect(Native Method)
log_1       | 	at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:350)
log_1       | 	at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:206)
log_1       | 	at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188)
log_1       | 	at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392)
log_1       | 	at java.net.Socket.connect(Socket.java:589)
log_1       | 	at com.rabbitmq.client.impl.FrameHandlerFactory.create(FrameHandlerFactory.java:32)
log_1       | 	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:676)
log_1       | 	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:722)
log_1       | 	at fr.mickaelbaron.helloworldlogmicroservice.HelloWorldLogMicroservice.<init>(HelloWorldLogMicroservice.java:26)
log_1       | 	at fr.mickaelbaron.helloworldlogmicroservice.HelloWorldLogMicroservice.main(HelloWorldLogMicroservice.java:72)
```

Le microservice **Log** n'arrive pas à se connecter au serveur RabbitMQ puisque ce dernier a un temps de démarrage relativement long. Par conséquent la connexion au serveur RabbitMQ est faite trop tôt. Cette problématique est connue chez les utilisateurs de l'outil **docker-compose** <https://docs.docker.com/compose/faq/#how-do-i-get-compose-to-wait-for-my-database-to-be-ready-before-starting-my-application>. Pour pallier à ce problème, nous allons modifier le programme Java afin de pouvoir tenter une nouvelle connexion en cas d'échec. Cette solution est connue sous le nom de *healthcheck*.

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

* Pour recompiler uniquement l'image de *log*, exécuter la ligne de commande suivante.

```console
$ docker-compose build log
Building log
Step 1/12 : FROM java:openjdk-8-jdk
 ---> d23bdf5b1b1b
Step 2/12 : LABEL MAINTAINER="Mickael BARON"
 ---> Using cache
 ---> 0daf28a8fb12
Step 3/12 : ENV MAVEN_VERSION 3.3.9
 ---> Using cache
 ---> e9a99812c33e
Step 4/12 : RUN curl -fsSLk https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share 	&& mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven 	&& ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
 ---> Using cache
 ---> 72d4cf747bb2
...
Step 11/12 : ENTRYPOINT ["java", "-cp", "target/classes:target/dependency/*", "fr.mickaelbaron.helloworldlogmicroservice.HelloWorldLogMicroservice"]
 ---> Running in 0f31033988a7
Removing intermediate container 0f31033988a7
 ---> 048016e32f90
Step 12/12 : CMD [localhost]
 ---> Running in 2cab3b4ecd76
Removing intermediate container 2cab3b4ecd76
 ---> 95abc3ec1549
Successfully built 95abc3ec1549
Successfully tagged mickaelbaron/helloworldlogmicroservice:latest
```

* Pour recréer les conteneurs, exécuter la ligne de commande suivante.

```console
$ docker-compose up -d
workspace_web_1 is up-to-date
workspace_redis_1 is up-to-date
workspace_rabbitmq_1 is up-to-date
workspace_rest_1 is up-to-date
Recreating workspace_log_1 ... done
```

* Vérifier que les conteneurs ont correctement été créés en exécutant la ligne de commande suivante.

```console
$ docker-compose ps
        Name                      Command               State                                Ports
--------------------------------------------------------------------------------------------------------------------------------
workspace_log_1        java -cp target/classes:ta ...   Up
workspace_rabbitmq_1   docker-entrypoint.sh rabbi ...   Up      15671/tcp, 0.0.0.0:15672->15672/tcp, ..., 0.0.0.0:5672->5672/tcp
workspace_redis_1      docker-entrypoint.sh redis ...   Up      6379/tcp
workspace_rest_1       java -cp target/classes:ta ...   Up      0.0.0.0:8080->8080/tcp
workspace_web_1        http-server /workdir/site  ...   Up      0.0.0.0:80->8080/tcp
```

* Il ne nous reste plus qu'à tester. Ouvrir un navigateur et rendez-vous à cette adresse : <http://localhost>.

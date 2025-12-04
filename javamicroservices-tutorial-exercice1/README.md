# Exercice 1 : préparer le programme Java du service web HelloWorld (Rest)

Le projet Java _helloworldrestmicroservice_, celui qui servira pour le microservice **Rest** du service web _HelloWorld_, a été développé en utilisant les spécifications JAX-RS et CDI. Ces deux spécifications font parties du modèle de programmation [MicroProfile](https://microprofile.io/) permettant le développement de microservice avec le langage Java. Nous utiliserons [Open Liberty](https://openliberty.io/) comme solution d'implémentation à [MicroProfile](https://microprofile.io/).

Le code du projet Java est assez commun. Un package `service` pour la gestion des services web REST, un package `dao` pour la gestion des données avec la base de données [Redis](https://redis.io/) et un package `model` pour représenter les entités manipulées. Dans cet exercice on partira d'un code déjà tout prêt et nous nous attacherons à le configurer pour l'utiliser avec [Open Liberty](https://openliberty.io/).

## But

- Configurer un microservice respectant [MicroProfile](https://microprofile.io/) avec [Open Liberty](https://openliberty.io/).
- Configurer un fichier _pom.xml_ pour gérer les dépendances [Maven](https://maven.apache.org/).
- Exécuter un microservice Java en ligne de commande.

## Étapes à suivre

- Démarrer l'éditeur [VSCode](https://code.visualstudio.com/ "Visual Studio Code").

- Ouvrir le dossier du projet Maven _helloworldrestmicroservice_ disponible dans le répertoire _workspace_.

- Examiner les différents packages et classes. Vous remarquerez que le projet contient des erreurs de compilation dues à l'absence des dépendances vers [Open Liberty](https://openliberty.io/).

- Ouvrir le fichier _pom.xml_ et compléter le contenu de la balise `<dependencies>` par les dépendances suivantes.

```xml
...
<dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-api</artifactId>
    <version>${jarkartaee-api.version}</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.eclipse.microprofile</groupId>
    <artifactId>microprofile</artifactId>
    <version>${microprofile.version}</version>
    <type>pom</type>
    <scope>provided</scope>
</dependency>
...
```

- Compléter dans la balise `<properties>` le numéro de version de [Jakarta EE](https://jakarta.ee/) et le numéro de version de [MicroProfile](https://microprofile.io/).

```xml
...
<properties>
    <jarkartaee-api.version>10.0.0</jarkartaee-api.version>
    <microprofile.version>7.0</microprofile.version>
    ...
</properties>
...
```

Le projet ne contient plus d'erreurs et peut être démarré. Toutefois, [Open Liberty](https://openliberty.io/) a besoin de plugins Maven pour que le projet puisse s'exécuter.

- Au niveau de la balise `<plugins>` ajouter les éléments suivants.

```xml
<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin>
            <groupId>io.openliberty.tools</groupId>
            <artifactId>liberty-maven-plugin</artifactId>
            <version>3.11.2</version>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <version>3.4.0</version>
        </plugin>
    </plugins>
</build>
```

- Exécuter la commande Maven suivante en se plaçant à la racine du dossier _helloworldrestmicroservice_ :

```bash
mvn clean liberty:dev
```

La sortie console attendue :

```bash
...
Le serveur defaultServer est prêt pour une planète plus intelligente. Il a démarré en 1,099 secondes..
[INFO] ************************************************************************
[INFO] *    Liberty is running in dev mode.
[INFO] *        Automatic generation of features: [ Off ]
[INFO] *        h - see the help menu for available actions, type 'h' and press Enter.
[INFO] *        q - stop the server and quit dev mode, press Ctrl-C or type 'q' and press Enter.
[INFO] *        
[INFO] *    Liberty server port information:
[INFO] *        Liberty server HTTP port: [ 9080 ]
[INFO] *        Liberty debug port: [ 7777 ]
[INFO] ************************************************************************
[INFO] Source compilation was successful.
[INFO] [AUDIT   ] CWWKT0017I: Application Web supprimée (default_host) : http://192.0.0.2:9080/
[INFO] [AUDIT   ] CWWKZ0009I: L'application helloworldrestmicroservice s'est arrêtée correctement.
[INFO] [AUDIT   ] CWWKT0016I: Application Web disponible, (default_host) : http://192.0.0.2:9080/
[INFO] [AUDIT   ] CWWKZ0003I: Application helloworldrestmicroservice mise à jour en 0,137 secondes.
```

Le mode développement (`liberty:dev`) est utilisé et cela permet de coder, déployer, tester et déboguer directement depuis [VSCode](https://code.visualstudio.com/ "Visual Studio Code"). Ce mode ne sera pas utilisé quand le projet sera déployé avec les autres microservices.

Votre programme s'exécute par l'intermédiaire de [Open Liberty](https://openliberty.io/). Pour tester nous pourrions utiliser l'adresse <http://localhost:9080/helloworld>, mais comme le serveur [Redis](https://redis.io/) n'est pas encore opérationnel nous ne pourrons pas à cet instant aller plus loin dans les tests.

- Avant de continuer, arrêter l'exécution du programme (**CTRL+C**).

Votre microservice **Rest** est désormais opérationnel et l'instruction pour la démarrer fonctionne.

- Avant de passer à l'exercice suivant qui nous permettra de disposer d'un serveur [Redis](https://redis.io/), essayons de comprendre comment la communication est réalisée entre le microservice **Rest** et le serveur [Redis](https://redis.io/). Ouvrir la classe `fr.mickaelbaron.helloworldrestmicroservice.dao.redis.JedisFactory` et examiner la méthode `URI getRedisURI()`.

```java
@ApplicationScoped
public class JedisFactory {
    private static final String REDIS_HOST_ENV = "REDIS_HOST";
    ...
    private URI getRedisURI() {
        String redisHost = System.getenv(REDIS_HOST_ENV);
        return URI.create(redisHost != null && !redisHost.isEmpty() ? redisHost : "tcp://localhost:6379");
    }
```

Vous remarquerez que l'accès à l'hôte de [Redis](https://redis.io/) se fait par une variable d'environnement `REDIS_HOST` ou via l'adresse `localhost`. Ainsi, en dehors d'une nouvelle compilation, le seul moyen de changer l'adresse de [Redis](https://redis.io/) est de passer par des variables d'environnement. 

## Avez-vous bien compris ?

Pour continuer sur les concepts présentés dans cet exercice, nous proposons les expérimentations suivantes :

- mettre en pratique ce projet avec d'autres implémentations de [**MicroProfile**](https://microprofile.io/), comme [**WildFly**](https://www.wildfly.org), ou avec des versions antérieures de la spécification puisque les fonctionnalités utilisées ici sont prises en charge dès la version **MicroProfile 4**.

- passer la valeur du hôte de [Redis](https://redis.io/) autrement que par variable d'environnement (par exemple via une variable introduite dans le fichier _server.xml_ de [Open Liberty](https://openliberty.io/)).

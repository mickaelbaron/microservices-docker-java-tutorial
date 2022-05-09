# Exercice 1 : préparer le programme Java du service web HelloWorld (Rest)

Le projet Java (celui qui servira pour le microservice **Rest**) du service web *HelloWorld* a été développé en utilisant les spécifications JAX-RS et CDI. Ces deux spécifications font parties du modèle de programmation [MicroProfile](https://microprofile.io/) permettant le développement de microservice avec le langage Java. Nous utiliserons [KumuluzEE](https://ee.kumuluz.com/) comme solution d'implémentation à [MicroProfile](https://microprofile.io/). À noter que KumuluzEE utilise l'implémentation JERSEY pour JAX-RS.

Le code du projet Java est assez commun. Un package *service* pour la gestion des services web REST et un package *dao* pour la gestion des données avec la base de données [Redis](https://redis.io/). Dans cet exercice on va partir d'un code déjà tout prêt et nous allons nous attacher à le configurer pour l'utiliser avec [KumuluzEE](https://ee.kumuluz.com/).

## But

* Configurer un microservice respectant MicroProfile avec [KumuluzEE](https://ee.kumuluz.com/).
* Configurer un fichier *pom.xml* pour gérer les dépendances [Maven](https://maven.apache.org/).
* Exécuter un microservice Java en ligne de commande.

## Étapes à suivre

* Démarrer l'environnement de développement Eclipse.

* Importer le projet Maven *helloworldrestmicroservice* (**File -> Import -> General -> Existing Maven Projects**, choisir le répertoire du projet puis faire **Finish**).

* Examiner les différents packages et classes. Vous remarquerez que le projet contient des erreurs de compilation dues à l'absence des dépendances vers [KumuluzEE](https://ee.kumuluz.com/) et indirectement vers [Jersey](https://eclipse-ee4j.github.io/jersey/).

* Ouvrir le fichier *pom.xml* et compléter le contenu de la balise `<dependencies>` par les dépendances suivantes.

```xml
...
    <dependencies>
        <dependency>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-core</artifactId>
            <version>${kumuluzee.version}</version>
        </dependency>
        <dependency>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-servlet-jetty</artifactId>
            <version>${kumuluzee.version}</version>
        </dependency>
        <dependency>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-jax-rs-jersey</artifactId>
            <version>${kumuluzee.version}</version>
        </dependency>
        <dependency>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-cdi-weld</artifactId>
            <version>${kumuluzee.version}</version>
        </dependency>
    ...
```

* Compléter dans la balise `<properties>` le numéro de version de KumuluzEE.

```xml
...
    <properties>
        <kumuluzee.version>4.0.0-beta.1</kumuluzee.version>
        ...
    </properties>
...
```

> La version `4.0.0-beta.1` de [KumuluzEE](https://ee.kumuluz.com/) supporte la version Java 17 LTS et utilise les artefacts JakartaEE en remplacement des artefacts JavaEE. Bien que cette version soit en béta, l'utilisation pour ce tutoriel n'a pas posé de problème. **Il faudra toutefois attendre une version officielle pour une utilisation en production**.

Désormais le projet ne contient plus d'erreurs et peut être compilé en totalité (Eclipse s'en charge via la compilation incrémentale).

* Pour exécuter le projet depuis Eclipse, créer une configuration d'exécution que vous appellerez *HelloworldRESTMicroservice* et dont la classe principale (Main class) sera `com.kumuluz.ee.EeApplication` puis faire **Run**.

Votre programme s'exécute par l'intermédiaire de [KumuluzEE](https://ee.kumuluz.com/). Pour tester nous pourrions utiliser l'adresse <http://localhost:8080/helloworld>, mais comme le serveur [Redis](https://redis.io/) n'est pas encore opérationnel nous ne pourrons pas à cet instant aller plus loin dans les tests.

* Avant de continuer, arrêter l'exécution du programme depuis le bouton **Terminate** (bouton rouge) de la console Eclipse.

Notre programme Java doit s'exécuter en ligne de commande et non pas depuis Eclipse quand il sera exécuté depuis un conteneur [Docker](https://www.docker.com/). Nous allons donc réaliser une dernière modification sur le fichier *pom.xml* afin de préparer le terrain. Nous allons préciser à Maven que l'on souhaite que toutes les dépendances soient présentes dans le répertoire *target* du projet.

* Ouvrir le fichier *pom.xml*.

* Ajouter dans la balise `<plugins>` le plugin *maven-dependency-plugin* comme montré sur le code suivant.

```xml
...
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>${maven-dependency-plugin.version}</version>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

* Depuis Eclipse, exécuter la configuration d'exécution appelée *all dependencies (package)*. Si vous rencontrez des soucis avec l'intégration Maven sous Eclipse, exécuter la ligne de commande suivante à la racine de votre projet `$ mvn package`. Toutes les dépendances seront copiées par le plugin *maven-dependency-plugin* et localisées dans le répertoire *helloworldrestmicroservice/target/dependency*.

* Ouvrir une invite de commande à la racine du projet *helloworldrestmicroservice*, puis exécuter la ligne de commande suivante.

```console
$ java -cp 'target/classes:target/dependency/*' com.kumuluz.ee.EeApplication
2022-04-29 08:45:07.643 INFO -- com.kumuluz.ee.configuration.sources.FileConfigurationSource -- Unable to load configuration from file. No configuration files were found.
2022-04-29 08:45:07.678 INFO -- com.kumuluz.ee.EeApplication -- Initialized configuration source: SystemPropertyConfigurationSource
2022-04-29 08:45:07.679 INFO -- com.kumuluz.ee.EeApplication -- Initialized configuration source: EnvironmentConfigurationSource
2022-04-29 08:45:07.679 INFO -- com.kumuluz.ee.EeApplication -- Initialized configuration source: FileConfigurationSource
2022-04-29 08:45:07.679 INFO -- com.kumuluz.ee.EeApplication -- Initializing KumuluzEE
2022-04-29 08:45:07.679 INFO -- com.kumuluz.ee.EeApplication -- Checking for requirements
2022-04-29 08:45:07.681 INFO -- com.kumuluz.ee.EeApplication -- KumuluzEE running in an exploded class and dependency runtime.
...
```

Votre microservice **Rest** est désormais opérationnel et l'instruction pour la démarrer fonctionne.

* Arrêter l'exécution du programme en faisant simplement un **CTRL-C**.

* Avant de passer à l'exercice suivant qui nous permettra de disposer d'un serveur [Redis](https://redis.io/), essayons de comprendre comment la communication est réalisée entre le microservice **Rest** et le serveur [Redis](https://redis.io/). Ouvrir la classe `fr.mickaelbaron.helloworldrestmicroservice.dao.redis.JedisFactory` et examiner la méthode `URI getRedisURI()`.

```java
public class JedisFactory {
    private static final String REDIS_HOST_ENV = "REDIS_HOST";

    private URI getRedisURI() {
        String redisHost = System.getenv(REDIS_HOST_ENV);
        return URI.create(redisHost != null && !redisHost.isEmpty() ? redisHost : "tcp://localhost:6379");
    }
```

Vous remarquerez que l'accès à l'hôte de [Redis](https://redis.io/) se fait par une variable d'environnement `REDIS_HOST` (qui sera utilisée plus tard quand le projet sera un microservice) ou se fait via l'adresse `localhost` (pratique pour les tests).

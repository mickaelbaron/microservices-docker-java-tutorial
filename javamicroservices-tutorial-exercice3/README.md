# Exercice 3 : tester le service web HelloWorld (Rest) avec le microservice Redis

Revenons un instant sur le projet _helloworldrestmicroservice_ qui implémente le microservice **Rest** (voir premier exercice). Pour assurer la communication entre ce projet (pas encore isolé dans un conteneur Docker) et le microservice **Redis** la solution est de rediriger le port `6379` de l'hôte vers le port `6379` du conteneur (le port par défaut de [Redis](https://redis.io/)).

> Une autre solution aurait été de récupérer l'adresse IP du conteneur nommé _redis_. Toutefois cette solution ne fonctionne que sous Linux puisque sous **Docker Desktop pour Windows** et **Docker Desktop pour Mac** le réseau _docker0_ (celui des conteneurs) est disponible dans la machine virtuelle Linux qui sert à faire fonctionner [Docker](https://www.docker.com/) sur des systèmes non Linux. C'est acutellement une limite aux versions Windows et macOS de l'outil Docker Desktop.

## But

- Rediriger des ports d'un conteneur.
- Inspecter des métadonnées d'un conteneur.

## Étapes à suivre

Pour la redirection du port `6379` de l'hôte vers le port `6379` du conteneur vous devez ajouter un paramètre lors de la construction du conteneur **Redis** : `-p 6379:6379`, mais il faut avant tout supprimer le conteneur existant créé depuis l'exercice 2.

- Supprimer le conteneur nommé _redis_ via la ligne de commande suivante.

```bash
docker rm -f redis
```

La sortie console attendue :

```bash
redis
```

- Créer une nouvelle fois le conteneur _redis_ en ajoutant le paramètre `-p 6379:6379` permettant de rediriger le port `6379` de l'hôte vers le port `6379` du conteneur.

```bash
docker run --name redis -v $(pwd)/data:/data -p 6379:6379 -d redis redis-server --appendonly yes
```

- Exécuter la ligne de commande suivante pour vérifier que le conteneur a été créé et que le port `6379` a été redirigé. Une nouvelle colonne `PORTS` fait son apparition et précise que le port de l'hôte est redirigé vers le port 6379 du conteneur.

```bash
docker ps
```

La sortie console attendue :

```bash
CONTAINER ID   IMAGE     COMMAND                  CREATED          STATUS          PORTS                    NAMES
97248d56c671   redis     "docker-entrypoint.s…"   20 seconds ago   Up 19 seconds   0.0.0.0:6379->6379/tcp   redis
```

- Exécuter la ligne de commande suivante pour obtenir plus d'information sur le conteneur nommé _redis_.

```bash
docker inspect redis
```

La sortie console attendue :

```json
[
    {
        "Id": "97248d56c6712bab1981ba5218abfbaf0772af7d73779018ce746027443559a6",
        "Created": "2022-04-29T08:16:55.4471978Z",
        "Path": "docker-entrypoint.sh",
        "Args": [
            "redis-server",
            "--appendonly",
            "yes"
        ],
        "State": {
            "Status": "running",
            "Running": true,
            "Paused": false,
            "Restarting": false,
            "OOMKilled": false,
            "Dead": false,
            "Pid": 4553,
            "ExitCode": 0,
            "Error": "",
            "StartedAt": "2022-04-29T08:16:55.9663997Z",
            "FinishedAt": "0001-01-01T00:00:00Z"
        },
        ...
        "NetworkSettings": {
            "Bridge": "",
            "SandboxID": "0751a627a8701aa2c92d316cb97f7001b68fb94e649cb605c750ca998238ea03",
            "HairpinMode": false,
            "LinkLocalIPv6Address": "",
            "LinkLocalIPv6PrefixLen": 0,
            "Ports": {
                "6379/tcp": [
                    {
                        "HostIp": "0.0.0.0",
                        "HostPort": "6379"
                    }
                ]
            },
        ...
    }
]
```

Désormais nous pouvons démarrer le projet Java _helloworldrestmicroservice_ afin qu'il se connecte au serveur Redis.

- Depuis le terminal sous [VSCode](https://code.visualstudio.com/ "Visual Studio Code"), créer une variable d'environnement `REDIS_HOST` avec la valeur `tcp://0.0.0.0:6379`.

```
export REDIS_HOST=tcp://0.0.0.0:6379
```

> L'adresse IP `0.0.0.0` signifie que toutes les adresses IPv4 de la machine locale sont accessibles.

- Démarrer le microservice **Rest** comme expliqué dans l'exercice 1 (à noter qu'il faut être placé à la racine du dossier _helloworldrestmicroservice_) :

```bash
mvn clean liberty:dev
```

Pour tester le service web _HelloWorld_, nous utiliserons l'outil **cURL**. 

- Exécuter la première commande qui poste un message « HelloWorld ».

```bash
# Création d'un message « HelloWorld » à partir d'un contenu JSON.
curl -H "Content-Type: application/json" -X POST -d '{"message":"Mon HelloWorld"}' http://localhost:9080/helloworld
```

- Exécuter la seconde commande qui récupère les messages « HelloWorld » envoyés.

```bash
# Lister les messages « HelloWorld ».
curl http://localhost:9080/helloworld
```

La sortie console attendue :

```bash
[{"message":"Mon HelloWorld","rid":4,"startDate":"Wed Apr 23 13:33:52 CEST 2025"}]
```

Tout fonctionne parfaitement. Notre programme Java du service web _HelloWorld_ est prêt à être isolé dans un conteneur Docker afin de devenir le microservice **Rest**.
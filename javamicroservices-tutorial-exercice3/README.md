# Exercice 3 : tester le service web HelloWorld (Rest) avec le microservice Redis

Revenons un instant sur le projet *helloworldrestmicroservice* qui implémente le microservice **Rest** (voir premier exercice). Pour assurer la communication entre ce projet (pas encore isolé dans un conteneur Docker) et le microservice **Redis** la solution est de rediriger le port 6379 de l'hôte vers le port 6379 du conteneur (le port par défaut de Redis).

> Une autre solution aurait été de récupérer l'adresse IP du conteneur nommé *redis*. Toutefois cette solution ne fonctionne que sous Linux puisque sous **Docker for Windows** et **Docker for Mac** le réseau *docker0* (celui des conteneurs) est disponible dans la machine virtuelle Linux qui sert à faire fonctionner Docker sur des systèmes non Linux. C'est acutellement une limite aux versions Windows et macOS de Docker.

## But

* Rediriger des ports avec Docker.
* Inspecter des métadonnées d'un conteneur Docker.

## Étapes à suivre

Pour la redirection du port 6379 de l'hôte vers le port 6379 du conteneur vous devez ajouter un paramètre lors de la construction du conteneur **Redis** : `-p 6379:6379`, mais il faut avant tout supprimer le conteneur existant créé depuis l'exercice 2.

* Supprimer le conteneur nommé *redis* via la ligne de commande suivante.

```console
$ docker rm -f redis
redis
```

* Créer un nouvelle fois le conteneur *redis* en ajoutant le paramètre `-p 6379:6379` permettant de rediriger le port 6379 de l'hôte vers le port 6379 du conteneur.

```console
$ docker run --name redis -v $(pwd)/data:/data -p 6379:6379 -d redis redis-server --appendonly yes
a1f4e49c2ea5af796012ca5665bb57daba47158bb855cf6dcd4bca79382d025f
```

* Exécuter la ligne de commande suivante pour vérifier que le conteneur a été créé et que le port 6379 a été redirigé. Une nouvelle colonne `PORTS` fait son apparition et précise que le port de l'hôte est redirigé vers le port 6379 du conteneur.

```console
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
a1f4e49c2ea5        redis               "docker-entrypoint.s…"   8 hours ago         Up 8 hours          0.0.0.0:6379->6379/tcp   redis
```

* Exécuter la ligne de commande suivante pour obtenir plus d'information sur le conteneur nommé *redis*.

```console
$ docker inspect redis
[
    {
        "Id": "a1f4e49c2ea5af796012ca5665bb57daba47158bb855cf6dcd4bca79382d025f",
        "Created": "2018-12-28T21:49:21.1928527Z",
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
            "Pid": 14589,
            "ExitCode": 0,
            "Error": "",
            "StartedAt": "2018-12-28T21:49:21.83339Z",
            "FinishedAt": "0001-01-01T00:00:00Z"
        },
        ...
        "NetworkSettings": {
            "Bridge": "",
            "SandboxID": "aa5a1e2b2b1937acf1cf9f43ea1851a350481197620a628a15cd89fd32f299bb",
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

Désormais nous pouvons démarrer le projet Java *helloworldrestmicroservice* afin qu'il se connecte au serveur Redis.

* Depuis la configuration d'exécution, ajouter une variable d'environnement appelée (onglet Environment) `REDIS_HOST` avec la valeur `tcp://0.0.0.0:6379`, puis faire **Run**.

* Pour tester le service web *HelloWorld*, nous utiliserons l'outil **cURL**. Exécuter les deux lignes de commandes suivantes afin de poster un message « HelloWorld » et de récupérer les messages « HelloWorld » envoyés.

```console
# Création d'un message « HelloWorld » à partir d'un contenu JSON.
$ curl -H "Content-Type: application/json" -X POST -d '{"message":"Mon HelloWorld"}' http://localhost:8080/helloworld

# Lister les messages « HelloWorld ».
$ curl http://localhost:8080/helloworld
[{"rid":1,"message":"Mon HelloWorld","startDate":"Sat Dec 29 07:38:01 CET 2018"}]
```

Tout fonctionne parfaitement. Notre programme Java du service web *HelloWorld* est prêt à être isolé dans un conteneur Docker afin de devenir le microservice **Rest**.
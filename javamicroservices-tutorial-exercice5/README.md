# Exercice 5 : lier les microservices Redis et Rest

À cette étape nous disposons d'un conteneur correspondant au microservice **Redis** et d'une image pour le futur microservice **Rest** dont le code se trouve dans le projet *helloworldrestmicroservice*. Nous allons nous intéresser dans cet exercice à créer le conteneur du microservice **Rest** et lui associer le conteneur du microservice **Redis**. Pour réaliser cette association, nous utilisons des réseaux Docker.

## But

* Créer un réseau Docker.
* Associer un conteneur existant à un réseau Docker.
* Créer un conteneur en spécifiant un réseau Docker.
* Définir des variables d'environnement à la création d'un conteneur.
* Exécuter une commande Linux à partir d'un conteneur en cours.

## Étapes à suivre

* Créer un réseau Docker appelé *helloworldnetwork* en exécutant la ligne de commande suivante.

```console
$ docker network create helloworldnetwork
ffc205a2246eb4e3b02d138628a1c85d0533504840b772e3f8b5073eefd0c969
```

* Afficher la liste des réseaux Docker afin d'assurer que votre réseau précédent créé à bien été créé.

```console
$ docker network ls
NETWORK ID     NAME                        DRIVER    SCOPE
e5cbac22f44e   bridge                      bridge    local
ffc205a2246e   helloworldnetwork           bridge    local
6fc763d7ede1   host                        host      local
8864d55ff2b1   none                        null      local
```

Vous remarquerez que des réseaux Docker sont déjà existants (*bridge*, *host* et *none*). Ils sont créés par défaut lors de l'installation de [Docker](https://www.docker.com/). Par exemple le réseau *bridge* est le réseau par défaut. Si aucun réseau n’est spécifié à la construction d’un conteneur, le conteneur se connectera sur le réseau par défaut *bridge*. Le conteneur nommé *redis* créé dans les précédents exercices utilise ce réseau. Les conteneurs connectés sur le réseau par défaut *bridge* se voient tous mais sont **UNIQUEMENT** accessibles en utilisant les IPs (pas les noms). Le réseau par défaut bridge n’est pas **RECOMMANDÉ** pour la mise en production.

> Chaque conteneur connecté à un réseau Docker se verra identifié dans ce réseau par son nom. Dans le réseau Docker *helloworldnetwork* le conteneur *redis* sera identifié par *redis*.

* Nous allons connecter le conteneur *redis* précédemment créé au réseau Docker *helloworldnetwork*.

```console
$ docker network connect helloworldnetwork redis
```

> Dans le cas où le conteneur *redis* avait été supprimé, voici la commande pour créer le conteneur en l'attachant directement au réseau Docker *helloworldnetwork, pensez à vous placer à la racine du répertoire _workspace_ : `$ docker run --name redis --network helloworldnetwork -v $(pwd)/data:/data -p 6379:6379 -d redis redis-server --appendonly yes`.

* Pour vérifier que le conteneur Redis est bien connecté au réseau Docker *helloworldnetwork* deux (2) solutions sont possibles : en interrogeant soit le conteneur soit le réseau Docker.

1. Afficher les informations du conteneur *redis* via l'option `inspect`.

```console
$ docker inspect redis
...
            "Networks": {
                "bridge": {
                    "IPAMConfig": null,
                    "Links": null,
                    "Aliases": null,
                    "NetworkID": "e5cbac22f44edcef2baa399c2dee4b3e0ac98511bd3f6fff5acf2d49cfe244fe",
                    "EndpointID": "1b010eafa7df0108520d11b60788a03f8c76f957d1a8d783e40eafcb4a5af58d",
                    "Gateway": "172.17.0.1",
                    "IPAddress": "172.17.0.2",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "MacAddress": "02:42:ac:11:00:02",
                    "DriverOpts": null
                },
                "helloworldnetwork": {
                    "IPAMConfig": {},
                    "Links": null,
                    "Aliases": [
                        "1a7489cad180"
                    ],
                    "NetworkID": "ffc205a2246eb4e3b02d138628a1c85d0533504840b772e3f8b5073eefd0c969",
                    "EndpointID": "28be01f897cfdf896924f2df805ae7dd7cb67dea5ab1940c1a94455481815da0",
                    "Gateway": "172.21.0.1",
                    "IPAddress": "172.21.0.2",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "MacAddress": "02:42:ac:15:00:02",
                    "DriverOpts": {}
                }
            }
        }
    }
]
```

2. Afficher les informations du réseau Docker *helloworldnetwork*.

```console
$ docker network inspect helloworldnetwork
[
    {
        "Name": "helloworldnetwork",
        "Id": "ffc205a2246eb4e3b02d138628a1c85d0533504840b772e3f8b5073eefd0c969",
        "Created": "2022-04-29T20:48:22.0783369Z",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "172.21.0.0/16",
                    "Gateway": "172.21.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "1a7489cad180b8fe33c04cbed90d26bce7a7c5ec8524db324c41362033b7d0fc": {
                "Name": "redis",
                "EndpointID": "28be01f897cfdf896924f2df805ae7dd7cb67dea5ab1940c1a94455481815da0",
                "MacAddress": "02:42:ac:15:00:02",
                "IPv4Address": "172.21.0.2/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
```

* Créer un conteneur pour le microservice **Rest** en exécutant la ligne de commande suivante.

```console
$ docker run --name rest -p 8080:8080 -d --network helloworldnetwork --env REDIS_HOST=tcp://redis:6379 mickaelbaron/helloworldrestmicroservice
9bd91396cce2daf4c3d8428f9749202e187bb2c5d5f229ed666f9e565d644117
```

Cette instruction crée un conteneur Docker appelé **Rest** (`--name rest`), dont le port `8080` est redirigé sur le port `8080` du hôte (`-p 8080:8080`), lancé en tâche de fond (`-d`), connecté au réseau Docker `helloworldnetwork` à partir de l'image appelée *mickaelbaron/helloworldrestmicroservice*. Enfin, l'option `--env REDIS_HOST=tcp://redis:6379` permet de créer une variable d'environnement lors de la création du conteneur. Cette variable utilisée dans notre projet *helloworldrestmicroservice* sert à identifer l'accès au serveur Redis. Ici la valeur donnée est `redis` puisque les conteneurs dans un réseau Docker sont identifiés par leur nom de création.

* Assurons-nous que les deux conteneurs *redis* et *rest* sont connectés dans le réseau Docker *helloworldnetwork*.

```console
$ docker network inspect helloworldnetwork
...
        "Containers": {
            "1a7489cad180b8fe33c04cbed90d26bce7a7c5ec8524db324c41362033b7d0fc": {
                "Name": "redis",
                "EndpointID": "28be01f897cfdf896924f2df805ae7dd7cb67dea5ab1940c1a94455481815da0",
                "MacAddress": "02:42:ac:15:00:02",
                "IPv4Address": "172.21.0.2/16",
                "IPv6Address": ""
            },
            "9bd91396cce2daf4c3d8428f9749202e187bb2c5d5f229ed666f9e565d644117": {
                "Name": "rest",
                "EndpointID": "a542d1fffc6f5e1bbf2ec3caadf47a23bb1e4da119ef7b1dd662dc72951c29c4",
                "MacAddress": "02:42:ac:15:00:03",
                "IPv4Address": "172.21.0.3/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
```

* Vérifions également que depuis le conteneur *rest* le dialogue peut s'opérer via le conteneur *redis*, exécuter la ligne de commande suivante.

```console
$ docker exec -it rest /bin/sh -c 'apt-get update && apt-get -y install iputils-ping && ping redis'
PING redis (172.19.0.2): 56 data bytes
64 bytes from 172.19.0.2: icmp_seq=0 ttl=64 time=0.115 ms
64 bytes from 172.19.0.2: icmp_seq=1 ttl=64 time=0.213 ms
64 bytes from 172.19.0.2: icmp_seq=2 ttl=64 time=0.212 ms
...
```

Comme nous disposons d'une image minimaliste pour le microservice **Rest**, la plupart des outils réseaux ne sont pas installés. Il est donc nécessaire d'installer l'utilitaire **ping** avant de l'utiliser.

* Il nous reste plus qu'à tester le service web contenu dans le conteneur *rest*. Exécuter les deux lignes de commandes suivantes afin de poster un message « HelloWorld » et de récupérer les messages « HelloWorld » envoyés.

```console
# Création d'un message « HelloWorld » à partir d'un contenu JSON
$ curl -H "Content-Type: application/json" -X POST -d '{"message":"Mon HelloWorld"}' http://localhost:8080/helloworld

# Lister les messages « HelloWorld »
$ curl http://localhost:8080/helloworld
[{"rid":2,"message":"Mon HelloWorld","startDate":"Mon May 09 12:58:56 UTC 2022"},{"rid":1,"message":"Mon HelloWorld","startDate":"Mon May 09 14:26:39 CEST 2022"}]
```
# Exercice 5 : lier les microservices Redis et Rest

À cette étape nous disposons d'un conteneur correspondant au microservice **Redis** et d'une image Docker pour le futur microservice **Rest** dont le code se trouve dans le projet *helloworldrestmicroservice*. Nous allons nous intéresser dans cet exercice à créer le conteneur du microservice **Rest** et lui associer le conteneur du microservice **Redis**. Pour réaliser cette association, nous utilisons des réseaux Docker.

## But

* Créer un réseau Docker.
* Associer un conteneur existant à un réseau Docker.
* Créer un conteneur en spécifiant un réseau Docker.
* Définir des variables d'environnement à la création d'un conteneur Docker.
* Exécuter une commande Linux à partir d'un conteneur Docker en cours.

## Étapes à suivre

* Créer un réseau Docker appelé *helloworldnetwork* en exécutant la ligne de commande suivante.

```console
$ docker network create helloworldnetwork
14109f963b014b322fbbd3094af191517cd95d42244115d491e9abf8063d1d16
```

* Afficher la liste des réseaux Docker afin d'assurer que votre réseau précédent créé à bien été créé.

```console
$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
61f6b732863b        bridge              bridge              local
3017d43b85a0        helloworldnetwork   bridge              local
4523411e4251        host                host                local
50afa90b1502        none                null                local
```

Vous remarquerez que des réseaux Docker sont déjà existants (*bridge*, *host* et *none*). Ils sont créés par défaut lors de l'installation de Docker. Par exemple le réseau *bridge* est le réseau par défaut. Si aucun réseau n’est spécifié à la construction d’un conteneur, le conteneur se connectera sur le réseau par défaut *bridge*. Le conteneur nommé *redis* créé dans les précédents exercices utilise ce réseau. Les conteneurs connectés sur le réseau par défaut *bridge* se voient tous mais sont **UNIQUEMENT** accessibles en utilisant les IPs (pas les noms). Le réseau par défaut bridge n’est pas **RECOMMANDÉ** pour la mise en production.

> Chaque conteneur connecté à un réseau Docker se verra identifié dans ce réseau par son nom. Dans le réseau Docker *helloworldnetwork* le conteneur *redis* sera identifié par *redis*.

* Nous allons connecter le conteneur *redis* précédemment créé au réseau Docker *helloworldnetwork*.

```console
docker network connect helloworldnetwork redis
```

* Pour vérifier que le conteneur Redis est bien connecté au réseau Docker *helloworldnetwork* deux solutions sont possibles : en interrogeant soit le conteneur soit le réseau Docker.

1. Afficher les informations du conteneur *redis* via l'option `inspect`.

```console
$ docker inspect redis
...
"Networks": {
                "bridge": {
                    "IPAMConfig": null,
                    "Links": null,
                    "Aliases": null,
                    "NetworkID": "61f6b732863bb4652e386b60eea6ae1b1c06a76ced9f6d0b2e81779c275ef5f7",
                    "EndpointID": "302fb6762520789065c3b7430fe3ec63fac1db90dae1487bd13829c6c46e3ce4",
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
                        "f9a0f6d81967"
                    ],
                    "NetworkID": "3017d43b85a088fa605e74dc9122b69a02ea4bd2f4fb0cd3286f60a11ddee7df",
                    "EndpointID": "feb4cd85c146b58d728b47848de6f000d7c23e74a5a550fdc06cff8d5220b5c9",
                    "Gateway": "172.19.0.1",
                    "IPAddress": "172.19.0.2",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "MacAddress": "02:42:ac:13:00:02",
                    "DriverOpts": null
                }
            }
...
```

2. Afficher les informations du réseau Docker *helloworldnetwork*.

```console
$ docker network inspect helloworldnetwork
[
    {
        "Name": "helloworldnetwork",
        "Id": "3017d43b85a088fa605e74dc9122b69a02ea4bd2f4fb0cd3286f60a11ddee7df",
        "Created": "2018-12-30T15:49:18.1104193Z",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "172.19.0.0/16",
                    "Gateway": "172.19.0.1"
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
            "f9a0f6d81967414672aba2edecf7e570010f1de7a61f17424287430753212d8a": {
                "Name": "redis",
                "EndpointID": "feb4cd85c146b58d728b47848de6f000d7c23e74a5a550fdc06cff8d5220b5c9",
                "MacAddress": "02:42:ac:13:00:02",
                "IPv4Address": "172.19.0.2/16",
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
28eba01ed267a1618019df724cbc7686b574c2f73b0575ea1352f58cdaa4afc5
```

Cette instruction crée un conteneur Docker appelé **Rest** (`--name rest`), dont le port `8080` est redirigé sur le port `8080` du hôte (`-p 8080:8080`), lancé en tâche de fond (`-d`), connecté au réseau Docker `helloworldnetwork` à partir de l'image appelée *mickaelbaron/helloworldrestmicroservice*. Enfin, l'option `--env REDIS_HOST=tcp://redis:6379` permet de créer une variable d'environnement lors de la création du conteneur. Cette variable utilisée dans notre projet *helloworldrestmicroservice* sert à identifer l'accès au serveur Redis. Ici la valeur donnée est `redis` puisque les conteneurs dans un réseau Docker sont identifiés par leur nom de création.

* Assurons-nous que les deux conteneurs *redis* et *rest* sont connectés dans le réseau Docker *helloworldnetwork*.

```console
$ docker network inspect helloworldnetwork
...
        "Containers": {
            "28eba01ed267a1618019df724cbc7686b574c2f73b0575ea1352f58cdaa4afc5": {
                "Name": "rest",
                "EndpointID": "79c6e4ce51b6d57caddd25c9bf9f2e5bcb83d298b42477840cfeb479af1bf249",
                "MacAddress": "02:42:ac:13:00:03",
                "IPv4Address": "172.19.0.3/16",
                "IPv6Address": ""
            },
            "f9a0f6d81967414672aba2edecf7e570010f1de7a61f17424287430753212d8a": {
                "Name": "redis",
                "EndpointID": "feb4cd85c146b58d728b47848de6f000d7c23e74a5a550fdc06cff8d5220b5c9",
                "MacAddress": "02:42:ac:13:00:02",
                "IPv4Address": "172.19.0.2/16",
                "IPv6Address": ""
            }
        },
...
```

* Vérifions également que depuis le conteneur *rest* le dialogue peut s'opérer via le conteneur *redis*, exécuter la ligne de commande suivante.

```console
$ docker exec -it rest /bin/sh -c 'ping redis'
PING redis (172.19.0.2): 56 data bytes
64 bytes from 172.19.0.2: icmp_seq=0 ttl=64 time=0.115 ms
64 bytes from 172.19.0.2: icmp_seq=1 ttl=64 time=0.213 ms
64 bytes from 172.19.0.2: icmp_seq=2 ttl=64 time=0.212 ms
...
```

* Il nous reste plus qu'à tester le service web contenu dans le conteneur *rest*. Exécuter les deux lignes de commandes suivantes afin de poster un message « HelloWorld » et de récupérer les messages « HelloWorld » envoyés.

```console
# Création d'un message « HelloWorld » à partir d'un contenu JSON
$ curl -H "Content-Type: application/json" -X POST -d '{"message":"Mon HelloWorld"}' http://localhost:8080/helloworld

# Lister les messages « HelloWorld »
$ curl http://localhost:8080/helloworld
[{"rid":1,"message":"Mon HelloWorld","startDate":"Sat Dec 29 07:38:01 CET 2018"},{"rid":1,"message":"Mon HelloWorld","startDate":"Sat Dec 29 07:38:01 CET 2018"}]
```
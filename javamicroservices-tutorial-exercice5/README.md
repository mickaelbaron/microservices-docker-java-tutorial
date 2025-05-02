# Exercice 5 : lier les microservices Redis et Rest

À cette étape nous disposons d'un conteneur correspondant au microservice **Redis** et d'une image pour le futur microservice **Rest** dont le code se trouve dans le projet _helloworldrestmicroservice_. Nous allons nous intéresser dans cet exercice à créer le conteneur du microservice **Rest** et lui associer le conteneur du microservice **Redis**. Pour réaliser cette association, nous utilisons des réseaux [Docker](https://www.docker.com/).

## But

- Créer un réseau [Docker](https://www.docker.com/).
- Associer un conteneur existant à un réseau [Docker](https://www.docker.com/).
- Créer un conteneur en spécifiant un réseau [Docker](https://www.docker.com/).
- Définir des variables d'environnement à la création d'un conteneur.
- Exécuter une commande Linux à partir d'un conteneur en cours.

## Étapes à suivre

- Créer un réseau [Docker](https://www.docker.com/) appelé _helloworldnetwork_ en exécutant la ligne de commande suivante.

```bash
docker network create helloworldnetwork
```

- Afficher la liste des réseaux [Docker](https://www.docker.com/) afin d'assurer que votre réseau a bien été créé.

```bash
docker network ls
```

La sortie console attendue :

```bash
NETWORK ID     NAME                DRIVER    SCOPE
c363f85f7abc   bridge              bridge    local
6d067acad768   helloworldnetwork   bridge    local
020781e944e9   host                host      local
021fa4148b09   none                null      local
```

Vous remarquerez que des réseaux [Docker](https://www.docker.com/) sont déjà existants (_bridge_, _host_ et _none_). Ils sont créés par défaut lors de l'installation de [Docker](https://www.docker.com/). Par exemple le réseau _bridge_ est le réseau par défaut. Si aucun réseau n'est spécifié à la construction d'un conteneur, le conteneur se connectera sur le réseau par défaut _bridge_. Le conteneur nommé _redis_ créé dans les précédents exercices utilise ce réseau. Les conteneurs connectés sur le réseau par défaut _bridge_ se voient tous mais sont **UNIQUEMENT** accessibles en utilisant les IPs (pas les noms). Le réseau par défaut bridge n'est pas **RECOMMANDÉ** pour la mise en production.

> Chaque conteneur connecté à un réseau [Docker](https://www.docker.com/) se verra identifié dans ce réseau par son nom. Dans le réseau [Docker](https://www.docker.com/) _helloworldnetwork_ le conteneur _redis_ sera identifié par _redis_.

- Nous allons connecter le conteneur _redis_ précédemment créé au réseau Docker _helloworldnetwork_.

```bash
docker network connect helloworldnetwork redis
```

- Pour vérifier que le conteneur _redis_ est bien connecté au réseau [Docker](https://www.docker.com/) _helloworldnetwork_ deux (2) solutions sont possibles : en interrogeant le conteneur ou le réseau [Docker](https://www.docker.com/).

- (1) Afficher les informations du conteneur _redis_ via l'option `inspect`.

```bash
docker inspect redis
```

La sortie console attendue :

```bash
...
            "Networks": {
                "bridge": {
                    "IPAMConfig": null,
                    "Links": null,
                    "Aliases": null,
                    "MacAddress": "86:da:d5:4f:05:16",
                    "DriverOpts": null,
                    "GwPriority": 0,
                    "NetworkID": "c363f85f7abcca1923839e589a48250deb3a19134b44c77146034f3dbb859a73",
                    "EndpointID": "38a4ad78a8e2c179a20e5a21c45fffcb832776b9896028790eb661734efe1f12",
                    "Gateway": "172.17.0.1",
                    "IPAddress": "172.17.0.2",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "DNSNames": null
                },
                "helloworldnetwork": {
                    "IPAMConfig": {},
                    "Links": null,
                    "Aliases": [],
                    "MacAddress": "9a:ad:b9:48:93:ed",
                    "DriverOpts": {},
                    "GwPriority": 0,
                    "NetworkID": "6d067acad7684439b5a0f57c4e84b764480dd4793ff5544312655f0e809ca714",
                    "EndpointID": "d3f1e2c511042c4486325b48a41eddf9d6a98d330afd8ad8813219ad4d2b95d6",
                    "Gateway": "172.18.0.1",
                    "IPAddress": "172.18.0.2",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "DNSNames": [
                        "redis",
                        "248bfe934963"
                    ]
                }
            }
...
```

Nous vérifions bien que deux réseaux sont associés puisque le conteneur _redis_ a été ajouté à postériori au réseau _helloworldnetwork_.

- (2) Afficher les informations du réseau [Docker](https://www.docker.com/) _helloworldnetwork_.

```bash
docker network inspect helloworldnetwork
```

La sortie console attendue :

```bash
[
    {
        "Name": "helloworldnetwork",
        "Id": "6d067acad7684439b5a0f57c4e84b764480dd4793ff5544312655f0e809ca714",
        "Created": "2025-04-26T19:23:08.747523252Z",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv4": true,
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "172.18.0.0/16",
                    "Gateway": "172.18.0.1"
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
            "248bfe9349631c6b46ae0cdd5db1a6ea125a821e7f3f0f4b7c4ae13aa8b4c458": {
                "Name": "redis",
                "EndpointID": "d3f1e2c511042c4486325b48a41eddf9d6a98d330afd8ad8813219ad4d2b95d6",
                "MacAddress": "9a:ad:b9:48:93:ed",
                "IPv4Address": "172.18.0.2/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
```

- Créer un conteneur pour le microservice **Rest** en exécutant la ligne de commande suivante.

```bash
docker run --name rest -p 9080:9080 -d --network helloworldnetwork --env REDIS_HOST=tcp://redis:6379 mickaelbaron/helloworldrestmicroservice
```

Cette instruction crée un conteneur Docker appelé **Rest** (`--name rest`), dont le port `9080` est redirigé sur le port `9080` du hôte (`-p 9080:9080`), lancé en tâche de fond (`-d`), connecté au réseau [Docker](https://www.docker.com/) `helloworldnetwork` à partir de l'image appelée _mickaelbaron/helloworldrestmicroservice_. Enfin, l'option `--env REDIS_HOST=tcp://redis:6379` permet de créer une variable d'environnement lors de la création du conteneur. Cette variable utilisée dans notre projet _helloworldrestmicroservice_ sert à identifer l'accès au serveur [Redis](https://redis.io/). Ici la valeur donnée est `redis` puisque les conteneurs dans un réseau Docker sont identifiés par leur nom de création.

- Assurons-nous que les deux conteneurs _redis_ et _rest_ sont connectés dans le réseau Docker _helloworldnetwork_.

```bash
docker network inspect helloworldnetwork
```

La sortie console attendue :

```bash
...
        "Containers": {
            "248bfe9349631c6b46ae0cdd5db1a6ea125a821e7f3f0f4b7c4ae13aa8b4c458": {
                "Name": "redis",
                "EndpointID": "d3f1e2c511042c4486325b48a41eddf9d6a98d330afd8ad8813219ad4d2b95d6",
                "MacAddress": "9a:ad:b9:48:93:ed",
                "IPv4Address": "172.18.0.2/16",
                "IPv6Address": ""
            },
            "4f9f5b5f89caa6d9821b36aacb4835d2d093ff707349cde21875a66c173d0ffa": {
                "Name": "rest",
                "EndpointID": "1a3bfdb36058eef0da8904d3459ea6f4b72f5ad8b0ec50cc19cbf58519b84fa9",
                "MacAddress": "6e:3a:d4:4f:51:58",
                "IPv4Address": "172.18.0.3/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
```

- Vérifions également que depuis le conteneur _rest_ le dialogue peut s'opérer via le conteneur _redis_, exécuter la ligne de commande suivante.

```bash
docker exec -it rest /bin/sh -c 'ping redis'
```

La sortie console attendue :

```
PING redis (172.18.0.2) 56(84) bytes of data.
64 bytes from redis.helloworldnetwork (172.18.0.2): icmp_seq=1 ttl=64 time=0.041 ms
64 bytes from redis.helloworldnetwork (172.18.0.2): icmp_seq=2 ttl=64 time=0.164 ms
64 bytes from redis.helloworldnetwork (172.18.0.2): icmp_seq=3 ttl=64 time=0.138 ms
64 bytes from redis.helloworldnetwork (172.18.0.2): icmp_seq=4 ttl=64 time=0.141 ms
64 bytes from redis.helloworldnetwork (172.18.0.2): icmp_seq=5 ttl=64 time=0.157 ms
64 bytes from redis.helloworldnetwork (172.18.0.2): icmp_seq=6 ttl=64 time=0.230 ms
...
```

> L'outil **ping** avait été installé lors de la construction de l'image puisque cet outil n'était pas disponible sur l'image de base.

- Il nous reste plus qu'à tester le service web contenu dans le conteneur _rest_. Exécuter les deux lignes de commandes suivantes afin de poster un message « HelloWorld » et de récupérer les messages « HelloWorld » envoyés.

```bash
# Création d'un message « HelloWorld » à partir d'un contenu JSON
curl -H "Content-Type: application/json" -X POST -d '{"message":"Mon HelloWorld"}' http://localhost:9080/helloworld

# Lister les messages « HelloWorld »
curl http://localhost:9080/helloworld
[{"message":"Mon HelloWorld","rid":2,"startDate":"Wed Apr 23 13:33:51 CEST 2025"},{"message":"Mon HelloWorld","rid":1,"startDate":"Wed Apr 23 13:33:37 CEST 2025"}]
```

## Avez-vous bien compris ?

Pour continuer sur les concepts présentés dans cet exercice, nous proposons l'expérimentation suivante :

- créer des conteneurs _rest_ basés sur les images `mickaelbaron/helloworldrestmicroservic:slim` et `mickaelbaron/helloworldrestmicroservice:msb`.
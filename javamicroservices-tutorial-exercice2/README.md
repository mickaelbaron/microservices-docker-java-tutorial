# Exercice 2 : préparer le microservice Redis (créer un conteneur à partir d'une image existante)

Le microservice **Redis** a pour objectif de conserver l'état des différents messages *HelloWorld* traités par le microservice **Rest**. Cette conservation de données se fera par l'intermédiaire d'un serveur de données NoSQL Redis. Ce serveur permettra au microservice **Rest** de s'y connecter afin de conserver la création des messages *HelloWorld*.

## But

* Récupérer depuis Docker Hub (<https://hub.docker.com>) une image prête à l'emploi.
* Créer un conteneur depuis une image avec un volume.
* Lister des images Docker.
* Lister les conteneurs disponibles et inspecter un conteneur.

## Étapes à suivre

* Aller sur le site [Docker Hub](https://hub.docker.com) et faire une recherche avec *Redis*. Vous remarquerez un nombre important de dépôts (*Repositories*) destiné à Redis. De manière générale, veuillez privilégier les dépôts officiels.

* Ouvrir une invite de commande et se placer à la racine du dossier *workspace*.

* Saisir la ligne de commande suivante pour télécharger la dernière version de l'image Docker *Redis*. Cela prendra un petit peu de temps, car toutes les couches (*layers*) de l'image doivent être téléchargées. À noter que chaque couche est une image qui fait référence à une image parente.

```console
$ docker pull redis
Using default tag: latest
latest: Pulling from library/redis
1fe172e4850f: Already exists
6fbcd347bf99: Pull complete
993114c67627: Pull complete
90ee703e9ece: Pull complete
80d8b53e83a7: Pull complete
a09780893e15: Pull complete
Digest: sha256:96c3e4dfe047ba9225a7d36fc92b5a5cff9e047daf41a1e0122e2bd8174c839e
Status: Downloaded newer image for redis:latest
docker.io/library/redis:latest
```

* S'assurer que l'image a été correctement téléchargée en utilisant la commande images de l'outil **docker**.

```console
$ docker images
REPOSITORY          TAG           IMAGE ID          CREATED          SIZE
redis               latest        a10f849e1540      32 hours ago     117MB
```

* Les différentes couches de l'image peuvent être consultées en utilisant la commande `history` de l'outil **docker**.

```console
$ docker history redis
IMAGE          CREATED        CREATED BY                                      SIZE      COMMENT
a10f849e1540   32 hours ago   /bin/sh -c #(nop)  CMD ["redis-server"]         0B
<missing>      32 hours ago   /bin/sh -c #(nop)  EXPOSE 6379                  0B
<missing>      32 hours ago   /bin/sh -c #(nop)  ENTRYPOINT ["docker-entry…   0B
<missing>      32 hours ago   /bin/sh -c #(nop) COPY file:df205a0ef6e6df89…   374B
...
<missing>      8 days ago     /bin/sh -c set -eux;  savedAptMark="$(apt-ma…   4.13MB
<missing>      8 days ago     /bin/sh -c #(nop)  ENV GOSU_VERSION=1.14        0B
<missing>      8 days ago     /bin/sh -c groupadd -r -g 999 redis && usera…   329kB
<missing>      9 days ago     /bin/sh -c #(nop)  CMD ["bash"]                 0B
<missing>      9 days ago     /bin/sh -c #(nop) ADD file:8b1e79f91081eb527…   80.4MB
```

* Se placer à la racine du répertoire _workspace_ et créer un conteneur à partir de l'image [Docker](https://www.docker.com/) de [Redis](https://redis.io/) en saisissant la ligne de commande suivante.

```console
$ docker run --name redis -v $(pwd)/data:/data -d redis redis-server --appendonly yes
c95b96730f1e3ff7e99d5d380b7d871fcd1a7dab2883be41b50e237f8012763c
```

Un conteneur appelé *redis* sera créé. L'option `-v $(pwd)/data:/data` permet de partager le répertoire *$(pwd)/data* de l'hôte avec le répertoire */data* du conteneur. L'option `-d` permet de créer un conteneur en mode détaché (similaire au classique *&*). `redis` permet de désigner l'image et `redis-server --apprendonly yes` sont des options pour démarrer le serveur [Redis](https://redis.io/).

* Exécuter la ligne de commande suivante pour vérifier que le conteneur a été créé.

```console
$ docker ps
CONTAINER ID   IMAGE     COMMAND                  CREATED         STATUS         PORTS      NAMES
070145c677bf   redis     "docker-entrypoint.s…"   2 seconds ago   Up 2 seconds   6379/tcp   redis
```

Nous venons de créer dans un conteneur une instance d'un serveur [Redis](https://redis.io/). Nous allons pouvoir l'utiliser pour tester notre programme Java.
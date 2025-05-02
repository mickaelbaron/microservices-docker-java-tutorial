# Exercice 2 : préparer le microservice Redis (créer un conteneur à partir d'une image existante)

Le microservice **Redis** a pour objectif de conserver l'état des différents messages _HelloWorld_ traités par le microservice **Rest**. Cette conservation de données se fera par l'intermédiaire d'un serveur de données NoSQL [Redis](https://redis.io/). Ce serveur permettra au microservice **Rest** de s'y connecter afin de conserver la création des messages _HelloWorld_.

## But

- Récupérer depuis [Docker Hub](https://hub.docker.com) une image prête à l'emploi.
- Créer un conteneur depuis une image avec un volume.
- Lister des images Docker.
- Lister les conteneurs disponibles et inspecter un conteneur.

## Étapes à suivre

- Aller sur le site [Docker Hub](https://hub.docker.com) et faire une recherche avec _redis_. Vous remarquerez un nombre important de dépôts (_Repositories_) destiné à [Redis](https://redis.io/). De manière générale, veuillez privilégier les dépôts officiels.

- Ouvrir une invite de commande et se placer à la racine du dossier _workspace_.

- Saisir la ligne de commande suivante pour télécharger la dernière version de l'image Docker [Redis](https://redis.io/). Cela prendra un petit peu de temps, car toutes les couches (_layers_) de l'image doivent être téléchargées. À noter que chaque couche est une image qui fait référence à une image parente.

```bash
docker pull redis
```

La sortie console attendue :

```bash
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

- S'assurer que l'image a été correctement téléchargée en utilisant la commande `images` de l'outil **docker**.

```bash
docker images
```

La sortie console attendue :

```bash
REPOSITORY          TAG           IMAGE ID          CREATED          SIZE
redis               latest        a10f849e1540      32 hours ago     117MB
```

- Les différentes couches de l'image peuvent être consultées en utilisant la commande `history` de l'outil **docker**.

```bash
docker history redis
```

La sortie console attendue :

```bash
IMAGE          CREATED        CREATED BY                                      SIZE      COMMENT
fbdbaea47b9a   3 months ago   CMD ["redis-server"]                            0B        buildkit.dockerfile.v0
<missing>      3 months ago   EXPOSE map[6379/tcp:{}]                         0B        buildkit.dockerfile.v0
<missing>      3 months ago   ENTRYPOINT ["docker-entrypoint.sh"]             0B        buildkit.dockerfile.v0
<missing>      3 months ago   COPY docker-entrypoint.sh /usr/local/bin/ # …   20.5kB    buildkit.dockerfile.v0
<missing>      3 months ago   WORKDIR /data                                   4.1kB     buildkit.dockerfile.v0
<missing>      3 months ago   VOLUME [/data]                                  0B        buildkit.dockerfile.v0
<missing>      3 months ago   RUN /bin/sh -c mkdir /data && chown redis:re…   8.19kB    buildkit.dockerfile.v0
<missing>      3 months ago   RUN /bin/sh -c set -eux;   savedAptMark="$(a…   38.4MB    buildkit.dockerfile.v0
<missing>      3 months ago   ENV REDIS_DOWNLOAD_SHA=4ddebbf09061cbb589011…   0B        buildkit.dockerfile.v0
<missing>      3 months ago   ENV REDIS_DOWNLOAD_URL=http://download.redis…   0B        buildkit.dockerfile.v0
<missing>      3 months ago   ENV REDIS_VERSION=7.4.2                         0B        buildkit.dockerfile.v0
<missing>      3 months ago   RUN /bin/sh -c set -eux;  savedAptMark="$(ap…   4.28MB    buildkit.dockerfile.v0
<missing>      3 months ago   ENV GOSU_VERSION=1.17                           0B        buildkit.dockerfile.v0
<missing>      3 months ago   RUN /bin/sh -c set -eux;  apt-get update;  a…   41kB      buildkit.dockerfile.v0
<missing>      3 months ago   RUN /bin/sh -c set -eux;  groupadd -r -g 999…   41kB      buildkit.dockerfile.v0
<missing>      3 months ago   # debian.sh --arch 'arm64' out/ 'bookworm' '…   108MB     debuerreotype 0.15
```

- Se placer à la racine du répertoire _workspace_ et créer un conteneur à partir de l'image [Docker](https://www.docker.com/) de [Redis](https://redis.io/) en saisissant la ligne de commande suivante.

```bash
docker run --name redis -v $(pwd)/data:/data -d redis redis-server --appendonly yes
```

Un conteneur appelé _redis_ sera créé. L'option `-v $(pwd)/data:/data` permet de partager le répertoire _$(pwd)/data_ de l'hôte avec le répertoire _/data_ du conteneur. L'option `-d` permet de créer un conteneur en mode détaché (similaire au classique _&_). `redis` permet de désigner l'image et `redis-server --apprendonly yes` sont des options pour démarrer le serveur [Redis](https://redis.io/).

- Exécuter la ligne de commande suivante pour vérifier que le conteneur a été créé.

```bash
docker ps
```

La sortie console attendue :

```bash
CONTAINER ID   IMAGE     COMMAND                  CREATED         STATUS         PORTS      NAMES
070145c677bf   redis     "docker-entrypoint.s…"   2 seconds ago   Up 2 seconds   6379/tcp   redis
```

Nous venons de créer dans un conteneur une instance d'un serveur [Redis](https://redis.io/). Nous allons pouvoir l'utiliser pour tester notre programme Java.

## Avez-vous bien compris ?

Pour continuer sur les concepts présentés dans cet exercice, nous proposons les expérimentations suivantes :

- déterminer quelle version explicite de [Redis](https://redis.io/) est utilisée derrière le tag `latest`.

- supprimer le conteneur _redis_ et télécharger les images [Redis](https://redis.io/) pour les version 6 et 5.

# Exercice 2 : préparer le microservice Redis (créer un conteneur à partir d'une image existante)

Le microservice **Redis** a pour objectif de conserver l'état des différents messages *HelloWorld* traités par le microservice **Rest**. Cette conservation de données se fera par l'intermédiaire d'un serveur de données NoSQL Redis. Ce serveur permettra au microservice **Rest** de s'y connecter afin de conserver la création des messages *HelloWorld*.

## But

* Récupérer depuis Docker Hub (<https://hub.docker.com>) une image prête à l'emploi.
* Créer un conteneur depuis une image avec un volume.
* Lister des images Docker.
* Lister les conteneurs disponibles et inspecter un conteneur.

## Étapes à suivre

* Aller sur le site Docker Hub (<https://hub.docker.com>) et faire une recherche avec *Redis*. Vous remarquerez un nombre important de dépôts (*Repositories*) destiné à Redis. De manière générale, veuillez privilégier les dépôts officiels.

* Ouvrir une invite de commande et se placer à la racine du dossier *workspace*.

* Saisir la ligne de commande suivante pour télécharger la dernière version de l'image Docker *Redis*. Cela prendra un petit peu de temps, car toutes les couches (*layers*) de l'image doivent être téléchargées. À noter que chaque couche est une image qui fait référence à une image parente.

```console
$ docker pull redis
Using default tag: latest
latest: Pulling from library/redis
a5a6f2f73cd8: Pull complete
a6d0f7688756: Pull complete
53e16f6135a5: Pull complete
f52b0cc4e76a: Pull complete
e841feee049e: Pull complete
ccf45e5191d0: Pull complete
Digest: sha256:bf65ecee69c43e52d0e065d094fbdfe4df6e408d47a96e56c7a29caaf31d3c35
Status: Downloaded newer image for redis:latest
```

* S'assurer que l'image a été correctement téléchargée en utilisant la commande images de l'outil Docker.

```console
$ docker images
REPOSITORY          TAG           IMAGE ID          CREATED          SIZE
redis               latest        ce25c7293564      2 weeks ago      95MB
```

* Les différentes couches de l'image peuvent être consultées en utilisant la commande `history` de l'outil Docker.

```console
$ docker history redis
ce25c7293564        2 weeks ago         /bin/sh -c #(nop)  CMD ["redis-server"]         0B
<missing>           2 weeks ago         /bin/sh -c #(nop)  EXPOSE 6379/tcp              0B
<missing>           2 weeks ago         /bin/sh -c #(nop)  ENTRYPOINT ["docker-entry…   0B
<missing>           2 weeks ago         /bin/sh -c #(nop) COPY file:b63bb2d2b8d09598…   374B
...
<missing>           6 weeks ago         /bin/sh -c groupadd -r redis && useradd -r -…   329kB
<missing>           6 weeks ago         /bin/sh -c #(nop)  CMD ["bash"]                 0B
<missing>           6 weeks ago         /bin/sh -c #(nop) ADD file:dab9baf938799c515…   55.3MB
```

* Créer un conteneur à partir de l'image Docker de Redis en saisissant la ligne de commande suivante.

```console
$ docker run --name redis -v $(pwd)/data:/data -d redis redis-server --appendonly yes
c95b96730f1e3ff7e99d5d380b7d871fcd1a7dab2883be41b50e237f8012763c
```

Un conteneur appelé *redis* sera créé. L'option `-v $(pwd)/data:/data` permet de partager le répertoire *$(pwd)/data* de l'hôte avec le répertoire */data* du conteneur. L'option `-d` permet de créer un conteneur en mode détaché (similaire au classique *&*). `redis` permet de désigner l'image et `redis-server --apprendonly yes` sont des options pour démarrer le serveur Redis.

* Exécuter la ligne de commande suivante pour vérifier que le conteneur a été créé.

```console
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS               NAMES
c95b96730f1e        redis               "docker-entrypoint.s…"   2 minutes ago       Up 2 minutes        6379/tcp            redis
```

Nous venons de créer dans un conteneur une instance d'un serveur Redis. Nous allons pouvoir l'utiliser pour tester notre programme Java.
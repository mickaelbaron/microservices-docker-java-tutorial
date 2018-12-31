FROM node:latest
LABEL MAINTAINER="Mickael BARON"

RUN npm install -g grunt-cli && npm install -g http-server

WORKDIR /workdir
ADD ["bower.json","Gruntfile.js","package.json","/workdir/"]
RUN npm install && grunt dependencies

ADD src /workdir/src
RUN grunt package

EXPOSE 8080

ENTRYPOINT ["http-server","/workdir/site", "-p", "8080"]

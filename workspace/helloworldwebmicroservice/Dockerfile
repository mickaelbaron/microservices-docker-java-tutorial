# Build env
FROM node:slim as build-npm-stage
LABEL maintainer="Mickael BARON"

WORKDIR /web
COPY package.json ./
RUN npm install
COPY index.html ./
COPY src ./src
COPY vite.config.js ./

RUN npm run build

# Run env
FROM nginx:latest
COPY --from=build-npm-stage /web/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
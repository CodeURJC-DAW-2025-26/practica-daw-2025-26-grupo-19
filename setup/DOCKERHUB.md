# Publicar imagen en Docker Hub

Scripts disponibles para publicar la imagen del backend en Docker Hub.

## Uso recomendado

```sh
# 1. Iniciar sesion en Docker Hub
./docker_login.sh aaluc

# 2. Construir la imagen
./docker_build.sh aaluc futbolmanager latest

# 3. Subir la imagen
./docker_push.sh aaluc futbolmanager latest
```

Tambien se puede construir y subir en un solo paso:

```sh
./docker_publish.sh aaluc futbolmanager latest
```

Para publicar usando Docker Compose:

```sh
./docker_compose_publish.sh aaluc latest
```

## Valores por defecto

Si no se indican argumentos, los scripts usan estos valores:

```sh
DOCKERHUB_USER=aaluc
IMAGE_NAME=futbolmanager
TAG=latest
```

Por ejemplo, estos dos comandos son equivalentes:

```sh
./docker_publish.sh
./docker_publish.sh aaluc futbolmanager latest
```

Tambien se pueden usar variables de entorno:

```sh
DOCKERHUB_USER=miusuario IMAGE_NAME=futbolmanager TAG=v1 ./docker_publish.sh
```


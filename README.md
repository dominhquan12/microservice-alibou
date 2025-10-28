# Spring boot microservice for learning

## link youtube
https://www.youtube.com/watch?v=jdeSV0GRvwI&t=4s

## link github
https://github.com/ali-bouali/microservices-full-code

## import postman
direct to postman/microsevice.postman_collection.json

## run docker
direct to docker-compose.yml and click run or run command line below
```
docker compose -p microservices-full-code up -d
```

## export realm keycloak in powershell docker
```
docker exec -it keycloak /opt/keycloak/bin/kc.sh export --realm micro-services --dir /opt/keycloak/data/import
```

## create database
```
open pgadmin in docker http://localhost:5050/browser/
enter the password develop
create a server with config
name: microservice(optional)
hostname: ms_pg_sql(container name)
database: postgres
username: develop
password: develop
```
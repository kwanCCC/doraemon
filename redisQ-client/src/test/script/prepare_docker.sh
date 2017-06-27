#!/bin/bash

BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $BASE_DIR/pull_newest_images.sh || exit 1
source $BASE_DIR/container.sh
localAddr=$(ip addr | awk 'BEGIN{a=1;}/state UP/ && a==1{getline;getline;split($2, arr, "/");print arr[1];a=0;}')

random_docker_name doko-TestRedisQ
NAME_PREFIX=$RANDOM_NAME

# start redis
echo "start redis..."
REDIS_NAME="${NAME_PREFIX}-redis"
docker run -d --name=$REDIS_NAME -P docker.oneapm.me/oneapm/redis:3.0
get_container_id_by_name $REDIS_NAME
REDIS_CONTAINER=$CONTAINER
wait_container_ok_by_log 60 $REDIS_CONTAINER "The server is now ready to accept connections on port 6379" $REDIS_NAME

REDIS_PORT=$(docker inspect -f '{{(index (index .NetworkSettings.Ports "6379/tcp") 0).HostPort}}' $REDIS_NAME)

farther="$( cd "$( dirname $( dirname "${BASH_SOURCE[0]}" ))" && pwd )"

echo $farther

echo "REDIS_PORT=${REDIS_PORT}" > ${farther}/resources/env.properties
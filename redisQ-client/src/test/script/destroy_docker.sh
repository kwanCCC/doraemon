#!/bin/bash

if [ -z $DOCKER_NAME_PREFIX ]; then
 DOCKER_NAME_PREFIX="doko-TestRedisQ"
fi

num=`docker ps -a |grep $DOCKER_NAME_PREFIX|wc -l`
if [ $num -gt 0 ]; then
   echo "will kill $DOCKER_NAME_PREFIX"
   docker ps -a|grep $DOCKER_NAME_PREFIX|grep -v CONTAINER|awk '{print $1}'|xargs docker rm -f
else
   echo "no existing container"
   exit 0
fi

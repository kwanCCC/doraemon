#!/bin/bash

# 根据容器名称获取容器的id
function get_container_id_by_name {
  NAME=$1
  CONTAINER=`docker ps|grep "$NAME"|awk '{print $1}'`
  if [[ -z "$CONTAINER" ]]; then
    echo "get container ${NAME} failed."
    exit 1
  fi
}

# 为将要启动的容器生成一个唯一的名称
function random_docker_name {
  EXISTED=1
  while [[ -n "$EXISTED" ]]; do
   RANDOM_NAME="$1-$RANDOM"
   EXISTED=$(docker ps -a|grep "$RANDOM_NAME")
  done
}

# 根据日志内容,判断并等待容器中应用启动
function wait_container_ok_by_log {
  STARTED=""
  COUNT=0
  NAME=$4
  if [ -z $NAME ]; then NAME=$2; fi

  while [[ -z "$STARTED" && COUNT -lt $1 ]]; do
    STARTED=$(docker logs $2 2>&1 | grep "$3")
    echo "waiting startup of $NAME"
    sleep 1
    COUNT=$(($COUNT+1))
  done

  if [[ -z STARTED ]]; then
    #启动失败
    echo "timeout waiting startup of $2"
    exit 1
  fi
}
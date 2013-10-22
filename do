#!/bin/sh

action=$1

simple_build() {
  mvn install
}

clean() {
  mvn clean
}

case $action in
build)
  simple_build
  ;;
clean)
  clean
  ;;
rebuild)
  clean && simple_build
  ;;
esac


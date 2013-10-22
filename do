#!/bin/sh

simple_build() {
  mvn install
}

clean() {
  mvn clean
}

for action in $@
do
  echo Performing $action
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
  *)
    echo No action $action
    exit 1
  esac
done


#!/bin/bash

if [ ! -d "compile" ]; then
  echo "Création du dossier 'compile' qui comprendra le code compilé et évitera les conflits de compilation avec Eclipse..."
  mkdir compile
fi

if [ "0" != "$1" ]; then
  echo "Compiling scala code..."
  scalac -d compile -cp bin:lib/json-simple-1.1.1.jar -sourcepath src src/*.scala
fi

echo "Start executing code..."
scala -cp compile:lib/json-simple-1.1.1.jar Main

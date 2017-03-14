#!/bin/bash


if [ "$1" == "build" ]; then
    if [ ! -d "utilities/compile" ]; then
      echo "---------------------------------------------------------------------------"
      echo "-------- Creation of 'compile' directory to keep bytecode files... --------"
      echo "---------------------------------------------------------------------------"
      mkdir utilities/compile
    fi

    echo "-----------------------------------------------------------------------------"
    echo "------------------------ Compiling scala code... ----------------------------"
    echo "-----------------------------------------------------------------------------"
    scalac -d utilities/compile -cp utilities/lib/json-simple-1.1.1.jar -sourcepath src src/*.scala

    cd www/

    echo "-----------------------------------------------------------------------------"
    echo "------------------- Download all npm and bower modules... -------------------"
    echo "-----------------------------------------------------------------------------"

    npm install && bower install

elif [ "$1" == "compile" ]; then
    scalac -d ../utilities/compile -cp ../utilities/lib/json-simple-1.1.1.jar -sourcepath src ../src/*.scala
else
    cd www/
fi

# Run node js application
if lsof -Pi :3000 -sTCP:LISTEN -t >/dev/null ; then
    echo "running"
else
    node app.js
fi
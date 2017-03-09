# RO : Photo album project

### Synopsis

The aim of the project is to generate photo album automatically with some criteria. Multiple criteria can be used for that, with some algorithms shown during courses.

Algorithms used for mono-objective :
- Hill Climber First Improvement
- Iterated Local Search
- Evolutionary Algorithm

Algorithms used for multi-objective :
- Pareto Local Search
- Multi Objective Evolutionary Algorithm with Decomposition (MOEA/D)

The project is mainly developed with Scala language to get benefit of functional paradigm.

### Description

-------------------
* Author      : [S. Verel] (http://www-lisic.univ-littoral.fr/~verel/)
* Date        : 8/11/2015

-------------------
Files list :
- utilities/buildAlbum.py               : code python permettant de créer les pages web avec l'album à partir d'un fichier de solution
- utilities/buildGraph.R                : code R qui permet de générer le front de pareto pour un fichier score
- utilities/run.sh                      : script bash permettant l'exécution du programme
- albums/album-*/html                   : dossier pour recevoir les pages web d'un album
- albums/album-*/html/img/\*.jpg        : les 55 photos au format jpg de l'album photo
- albums/album-*/html/styleAlbum.css    : feuille de style associée aux pages web de l'album
- resources/data  : information sur les 55 photos au format json
- resources/docs		                : dossier comprenant les différents scores enregistrés afin de les comparer
- resources/scores		            : l'une des meilleures solution trouvée pour chaque fonction objectif
- resources/solutions
- src                                   : dossier du code scala donnant un exemple de lecture des données et de fonction d'évaluation. Il comprendra également l'ensemble des nouvelles fonctions d'évaluation développées.

-------------------


##Required

First of all, to compile et run scala, you need the java JDK 1.8 and [download] (https://www.scala-lang.org/download/install.html) scala library.

You also need to install [node.js] (https://nodejs.org/en/) and later install bower package :

**If you have Linux or MacOS, you can do it with package manager on your terminal**
#Installation & run

Build and run application :

```
./run.sh build
```

Just run application : 

```
./run.sh
```

### Contributors

* [jbuisine] (https://github.com/jbuisine)

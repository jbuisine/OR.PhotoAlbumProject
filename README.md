# M1.I2L.TD.RO.2016.11.09

### Synopsis

Le but du projet est de permettre la création automatique d'un album photo. Pour cela, plusieurs critères ont été évalué dans le but d'être optimisé. Il faut donc au travers d'algorithmes de recherche locale notamment ceux vus en cours, obtenir une solution proche d'un rendu d'une solution optimale.

Le projet sera développé en Scala dans le but de profiter du paradigme fonctionnel composé par ce langage.

### Description

-------------------
* Description : Projet de création d'album photo automatique par optimisation
* Auteur      : [S. Verel] (http://www-lisic.univ-littoral.fr/~verel/)
* Date        : 8/11/2015


-------------------
Liste des fichiers :
- prj1-ro.pdf                 : fichier comprennant la description plus détaillée du projet
- buildAlbum.py               : code python permettant de créer les pages web avec l'album à partir d'un fichier de solution
- html                        : dossier pour recevoir les pages web avec l'album
- html/img/*.jpg              : les 55 photos au format jpg de l'album photo
- html/styleAlbum.css         : feuille de style associée aux pages web de l'album
- data/info-photo.json        : information sur les 55 photos au format json
- data/info-album.json        : information sur les 9 pages de l'album
- data/chronologic-order.sol  : fichier contenant une solution de disposition des photos de l'album (par ordre chronologique)
- src : dossier du code scala donnant un exemple de lecture des données et de fonction d'évaluation. Il comprendra également l'ensemble des nouvelles fonctions d'évaluation développées.


-------------------
Création des pages :

python buildAlbum.py fichier.sol
où fichier.sol est le fichier contenant une solution de disposition

python buildAlbum.py
utilise par défaut le fichier data/chronologic-order.sol


### Installation

Il s'agit d'un projet Eclipse, pour cela il vous suffit de cloner le Repository au sein d'Eclipse et d'importer le projet.

Il faudra ensuite installer le plugin **Scala** IDE disponible dans le marketplace et configurer le projet :


```
Properties > Java Build Path > Libraries > Add library > JRE System Library
```

Mettre en place la library Scala :

```
Properties > Java Build Path > Libraries > Add library > Scala Library Container
```

Ajout du jar permettant la lecture de fichier JSON :

```
Properties > Java Build Path > Libraries > Add external jar > ./lib/json-simple-1.1.1.jar
```

Pour lancer l'application, il suffira d'exécuter le fichier **Main.scala** en tant que Scala Application.

### Command Line

Création du dossier 'compile' qui comprendra le code compilé et évitera les conflits de compilation avec Eclipse :

```
mkdir compile
```

Pour lancer l'application en ligne de commande il suffit dans un premier temps de compiler le code :

```
scalac -d compile -cp bin:lib/json-simple-1.1.1.jar -sourcepath src src/*.scala
```

Puis de l'éxécuter via la commande scala :

```
scala -cp compile:lib/json-simple-1.1.1.jar Main
```

### Script BASH

Il est aussi possible d'exécuter le code via le script __run.sh__ de la façon suivante :

```
sh run.sh
```
En ajoutant le paramètre 'build' on compile et exécute le code :

```
sh run.sh build
```

En ajoutant le paramètre 'show' on exécute le code et génère l'album :

```
sh run.sh show
```

En ajoutant les paramètres 'build' & 'show' on compile, exécute le code et génère l'album :

```
sh run.sh build show
```

### Contributors

* [jbuisine] (https://github.com/jbuisine)

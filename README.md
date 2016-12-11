# M1.I2L.TD.RO.2016.11.09

### Synopsis

Le but du projet est de permettre la création automatique d'un album photo. Pour cela, plusieurs critères ont été évalué dans le but d'être optimisé. Il faut donc au travers d'algorithmes de recherches locales notamment ceux vus en cours, obtenir une solution dans le but d'obtenir un rendu de proche d'une solution optimale.

Le projet sera développé en Java pour la lecture de fichiers & en Scala par la suite, dans le but de profiter du paradigme fonctionnel composé par ce second langage.

### Description

-------------------
* Description : Projet de création d'album photo automatique par optimisation
* Auteur      : [S. Verel] (http://www-lisic.univ-littoral.fr/~verel/)
* Date        : 8/11/2015


-------------------
Liste des fichiers :

- buildAlbum.py               : code python permettant de créer les pages web avec l'album à partir d'un fichier de solution
- html                        : dossier pour recevoir les pages web avec l'album
- html/img/*.jpg              : les 55 photos au format jpg de l'album photo
- html/styleAlbum.css         : feuille de style associée aux pages web de l'album
- data/info-photo.json        : information sur les 55 photos au format json
- data/info-album.json        : information sur les 9 pages de l'album
- data/chronologic-order.sol  : fichier contenant une solution de disposition des photos de l'album (par ordre chronologique)
- src : dossier du code scala donnant un exemple de lecture des données et de fonction d'évaluation


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

### Contributors

* [jbuisine] (https://github.com/jbuisine)

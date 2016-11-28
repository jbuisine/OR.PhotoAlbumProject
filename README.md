# M1.I2L.TD.RO.2016.11.09

### Synopsis

The aim of this project is a to create a photo album dynamically with the most possible coherences. For this we will use algorithms of local and evolutionary searches seen in formation.

### Description

-------------------
Description : Projet de création d'album photo automatique par optimisation
Auteur      : [S. Verel] (http://www-lisic.univ-littoral.fr/~verel/)
Date        : 8/11/2015


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

### Contributors

* [jbuisine] (https://github.com/jbuisine)

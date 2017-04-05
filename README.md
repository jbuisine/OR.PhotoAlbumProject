# RO : Photo album project

## Synopsis

The aim of the project is to generate photo album automatically based on criteria. Optimization is implemented with some algorithms shown during courses.

Algorithms used for mono-objective :
- Hill Climber First Improvement
- Iterated Local Search
- Evolutionary Algorithm

Algorithms used for multi-objective :
- Pareto Local Search
- Multi Objective Evolutionary Algorithm with Decomposition (MOEA/D)

The project is mainly developed with Scala language to get benefit of functional paradigm.

A node.js web application is developed to let the application more attractive and useful.

## Description

-------------------
* Author      : [S. Verel](http://www-lisic.univ-littoral.fr/~verel/)
* Date        : 8/11/2015

-------------------

### Files list description :

**Scala code**
- src/*                                 : Scala files with optimisation algorithms.    

**Web application**
- www/*                                 : Web node.js application with bower modules.
- www/views/templates/*                 : Templates with photos and pages generated.

**Utilities**
- utilities/lib/*                       : Libraries added to classpath when compiling scala code. 
- buildAlbum.py                         : Python script which generates ejs pages of a Template.
- buildGraph.R                          : Script which can be used to generate 2D & 3D plot.

**Resources**               
- resources/data                        : JSON files which described a template disposition.
- resources/solutions                   : Solutions generated for a specific Template and disposition.
- resources/docs		                : Documentation of the project.

-------------------


## Prerequisites

First of all, to compile et run scala, you need the java JDK 1.8 and [download] (https://www.scala-lang.org/download/install.html) scala library.

You need to install [node.js] (https://nodejs.org/en/) and later install bower package :

```
npm install -g bower
```

**If you have Linux or MacOS, you can do it with your favorite package manager on your terminal**


To use Clarifai API, you need to install it (With pyton 2.7) and export your tokens :

```
pip install clarifai==2.0.20
export CLARIFAI_APP_ID=<an_application_id_from_your_account>
export CLARIFAI_APP_SECRET=<an_application_secret_from_your_account>
```

If you do not already have it :

```
pip install imagehash
```

You also need the GraphicsMagick package (Here with yum) :
```
yum install GraphicsMagick
```

## Installation & run

Build and run application :

```
./run.sh build
```

Just run application (Only if already build): 

```
./run.sh
```

If you want to install application into a server, add a cron task (sudo crontab -e): 

```
@reboot cd /home/{user}/{directory}/{to}/{project} && ./run.sh build &
```

## Contributors

* [geoffreyp](https://github.com/geoffreyp)
* [jbuisine](https://github.com/jbuisine)
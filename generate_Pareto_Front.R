# Author : JB
# date 2016/01/17

###################################################################
###################### Pareto local search ########################
###################################################################
###################################################################
# Ce fichier sera amené à être amélioré pour prendre en compte :  #
# - le type de diagramme souhaité                                 #
# - le fichier de données en paramètres                           #
###################################################################

#Chemin absolu du repository git
setwd("/home/jbuisine/git/M1.I2L.TD.RO.2016-11-09/scores")

# lit les données du pareto local search entre la moyenne des couleurs et des tags non communs
df.rs <- read.table("PLS_greyAVGAndColorsAndCommonTags.xls", sep = ",")

names(df.rs) <- c("greyAVG", "colors", "CommonTags")

# vérification du fichier: affiche les premières lignes
head(df.rs)

# nuage de points obtenus en fonction des deux scores des fonctions (solution)
#plot(colors ~ uncommonTags, data = df.rs, col="blue")

## Load scatterplot3d
library(scatterplot3d)

## Simple scatter plot
with(data = df.rs,
     scatterplot3d(x = colors,
                   y = greyAVG,
                   z = CommonTags,
     )
)
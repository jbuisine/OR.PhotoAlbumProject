# Author : JB
# date 2016/01/17

#######################
# Pareto local search #
#######################

#Chemin absolu du repository git
setwd("/home/jbuisine/git/M1.I2L.TD.RO.2016-11-09/scores")

# lit les données du pareto local search entre la moyenne des couleurs et des tags non communs
df.rs <- read.table("PLS_colorsAndUncommonTags.xls", sep = ",")

names(df.rs) <- c("colors", "uncommonTags")

# vérification du fichier: affiche les premières lignes
head(df.rs)

# nuage de points obtenus en fonction des deux scores des fonctions (solution)
plot(colors ~ uncommonTags, data = df.rs, col="blue")
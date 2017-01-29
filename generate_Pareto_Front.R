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


main <- function(){
   setwd("/home/jbuisine/git/M1.I2L.TD.RO.2016-11-09/scores")
   
   twoDimPlot("PLS_greyAVGAndColors.xls", ",", c("greyAVG", "colors"))
   #threeDimPlot("PLS_greyAVGAndColorsAndCommonTags.xls", ",", c("greyAVG", "colors", "commonTags"))
   
}
twoDimPlot <- function(filename, separator,  namesCoords) {
   
   #Read values and initialize head
   df.rs <- read.table(filename, sep = separator)
   names(df.rs) <- c(namesCoords[1], namesCoords[2])
   head(df.rs)
   print(df.rs[,1])
   
   #Create plot 
   plot(df.rs[,1] ~ df.rs[,2], data = df.rs, col="blue")
}

threeDimPlot <- function(filename, separator, namesCoords) {
   df.rs <- read.table(filename, sep = separator)
   names(df.rs) <- c(namesCoords[1], namesCoords[2], namesCoords[3])
   head(df.rs)
   
   ## Load scatterplot3d
   library(scatterplot3d)
   
   ## Simple scatter plot
   with(data = df.rs, scatterplot3d(x = df.rs[,1], y = df.rs[,2], z = df.rs[,3]))
}

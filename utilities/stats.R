# R script used for statistics of project
# Author : JB
# date 2016/06/24

####################################################################
###################### Multiobjective stats ########################
####################################################################

main <- function(){
  
  #options(max.print=1000000)
  
  library("matrixStats")
  
  setwd("/home/jbuisine/Documents/M1.I2L/AnnualProject/RO.PhotoAlbumProject/resources/solutions/TestTemplate/album-6-2per3/")
  
  filename = "TPLS_greyAVG_commonTags_10000_5_5.sol.tracking"

  df <- read.csv(header = TRUE, file = filename, sep = ",")
  head(df)
  
  plot(df$D ~ df$I, data = df, col="blue")
  
  plot(df$ND ~ df$I, data = df, col="blue")
  
  
  df_bis <- df[,c("D","ND","HVL", "HV", "HVDiff")]
  
  df_bis <- data.matrix(df_bis)
  colSds(df_bis)
  colMeans(df_bis)
  colMedians(df_bis)
  colVars(df_bis)
}


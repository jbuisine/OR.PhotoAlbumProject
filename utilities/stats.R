# R script used for statistics of project
# Author : JB
# date 2016/06/24

####################################################################
###################### Multiobjective stats ########################
####################################################################

gettingStats <- function(df, range, fn){
  df_bis <- data.matrix(df[0:range,])
  new_df <- fn(df_bis)
  new_df["I"] <- range
  return(new_df)
}


generateOutpuStats <- function(fn, data){
  df1 <- data.frame(matrix(nrow=0, ncol=6))
  for(i in 1:200) { 
    
    d <- gettingStats(data, 100*i, fn)
    df1 <- rbind(df1, d)
  }
  
  names(df1)<-c("D","ND", "HVL", "HV", "HVDiff","I")
  return(df1)
}

readFile <- function(filename){
  return(read.csv(header = TRUE, file = filename, sep = ","))
}

main <- function(){
  
  library("matrixStats")
  
  needed <- c("D","ND","HVL", "HV", "HVDiff")
  
  setwd("/home/jbuisine/Documents/M1.I2L/AnnualProject/RO.PhotoAlbumProject/resources/solutions/StatsTemplate/album-4-2per2/")
  
  filenameStats = "RandomWalk_greyAVG_commonTags_20000.sol.tracking"
  filename = "RandomWalk_greyAVG_commonTags_20000.sol"

  dfRandom <- readFile("RandomWalk_greyAVG_commonTags_20000.sol")
  dfMOEAD_T <- readFile("MOEAD_greyAVG_commonTags_20000_T.sol")
  
  dfStatsRandom <- readFile("RandomWalk_greyAVG_commonTags_20000.sol.tracking")
  dfStatsMOEAD_T <- readFile("MOEAD_greyAVG_commonTags_20000_T.sol.tracking")
  dfStatsMOEAD_W <- readFile("MOEAD_greyAVG_commonTags_20000_W.sol.tracking")
  
  plot(dfMOEAD_T$greyAVG ~ dfMOEAD_T$commonTags,xlab = "Common Tags", ylab = "Grey AVG", data = dfStats, col="blue")
  
  #plot(dfStats$D ~ dfStats$I, data = dfStats, col="blue")
  
  #plot(dfStats$ND ~ dfStats$I, data = dfStats, col="blue")
  
  
  # Diferent function
  # colSds(df_bis)
  # colMeans(df_bis)
  # colMedians(df_bis)
  # colVars(df_bis)

  dfRandom_s <- generateOutpuStats(colMeans, dfStatsRandom[, needed])
  dfMOEAD_T_s <- generateOutpuStats(colMeans, dfStatsMOEAD_T[, needed])
  dfMOEAD_W_s <- generateOutpuStats(colMeans, dfStatsMOEAD_W[, needed])
  
  plot(HV ~ I ,
       xlab = "Iteration", 
       ylab = "HV", 
       data = dfRandom_s, 
       ylim = c(50,150),
       type = "l",
       col="black",
       lty=3)
  
  head(dfRandom_s)
  #lines(dfRandom_s$ND ~ dfRandom_s$I, col = "steelblue")
  lines(dfMOEAD_T_s$HV ~ dfMOEAD_T_s$I, col = "blue", lty=2)
  lines(dfMOEAD_W_s$HV ~ dfMOEAD_W_s$I, col = "red", lty=1)
}


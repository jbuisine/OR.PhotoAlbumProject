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
  

  dfRandom <- readFile("RandomWalk_greyAVG_commonTags_20000.sol")
  dfMOEAD_T <- readFile("MOEAD_greyAVG_commonTags_20000_T.sol")
  dfMOEAD_W <- readFile("MOEAD_greyAVG_commonTags_20000_W.sol")
  dfPLS <- readFile("PLS_greyAVG_commonTags_20000.sol")
  dfTPLS_T <- readFile("TPLS_greyAVG_commonTags_20000_10_10_T.sol")
  dfTPLS_W <- readFile("TPLS_greyAVG_commonTags_20000_10_10_W.sol")
  
  dfStatsRandom <- readFile("RandomWalk_greyAVG_commonTags_20000.sol.tracking")
  dfStatsMOEAD_T <- readFile("MOEAD_greyAVG_commonTags_20000_T.sol.tracking")
  dfStatsMOEAD_W <- readFile("MOEAD_greyAVG_commonTags_20000_W.sol.tracking")
  dfStatsPLS <- readFile("PLS_greyAVG_commonTags_20000.sol.tracking")
  dfStatsTPLS_T <- readFile("TPLS_greyAVG_commonTags_20000_10_10_T.sol.tracking")
  dfStatsTPLS_W <- readFile("TPLS_greyAVG_commonTags_20000_10_10_W.sol.tracking")
  
  # Remove duplicate line iterations
  dfStatsPLS <- dfStatsPLS[!duplicated(dfStatsPLS[,1]),]
  dfStatsTPLS_T <- dfStatsTPLS_T[!duplicated(dfStatsTPLS_T[,1]),]
  dfStatsTPLS_W <- dfStatsTPLS_W[!duplicated(dfStatsTPLS_W[,1]),]
  
  plot(dfTPLS_W$greyAVG ~ dfTPLS_W$commonTags, xlab = "Common Tags", ylab = "", 
       data = dfTPLS_W, 
       col="black", pch=1, 
       ylim=c(2, 10), xlim=c(12,18),
       main="TPLS W")
  
  # Diferent function
  # colSds(df_bis)
  # colMeans(df_bis)
  # colMedians(df_bis)
  # colVars(df_bis)

  dfRandom_s <- generateOutpuStats(colSds, dfStatsRandom[, needed])
  dfMOEAD_T_s <- generateOutpuStats(colSds, dfStatsMOEAD_T[, needed])
  dfMOEAD_W_s <- generateOutpuStats(colSds, dfStatsMOEAD_W[, needed])
  dfPLS_s <- generateOutpuStats(colSds, dfStatsPLS[, needed])
  dfTPLS_T_s <- generateOutpuStats(colSds, dfStatsTPLS_T[, needed])
  dfTPLS_W_s <- generateOutpuStats(colSds, dfStatsTPLS_W[, needed])
  
  plot(c(0,20000),
       c(10,30),
       type="n",
       xlab = "Iteration", 
       ylab = "",
       main="Mean")
       
  lines(dfRandom_s$ND ~ dfRandom_s$I, col="black", lwd=2, lty=1)
  lines(dfPLS_s$ND ~ dfPLS_s$I, col="red", lwd=2, lty=2)
  lines(dfMOEAD_T_s$ND ~ dfMOEAD_T_s$I, col="blue", lwd=2, lty=3)
  lines(dfMOEAD_W_s$ND ~ dfMOEAD_W_s$I, col="#4d9900", lwd=2, lty=4)
  lines(dfTPLS_T_s$ND ~ dfTPLS_T_s$I, col="orange", lwd=2, lty=5)
  lines(dfTPLS_W_s$ND ~ dfTPLS_W_s$I, col="purple", lwd=2, lty=6)
  
  legend(14500,
         29,
         c("Random walk", "PLS", "MOEAD T", "MOEAD W", "TPLS T","TPLS W"),
         lty=c(1,2,3,4,5,6),
         lwd=c(2,2,2,2,2,2),
         col=c("black","red", "blue","#4d9900","orange", "purple")
      )
  
  #lines(dfRandom_s$ND ~ dfRandom_s$I, col = "steelblue")
  #lines(dfMOEAD_T_s$HV ~ dfMOEAD_T_s$I, lty = 2, col="blue")
  #lines(dfMOEAD_W_s$HV ~ dfMOEAD_W_s$I, lty = 3, col="green")
  #lines(dfPLS_s$HV ~ dfPLS_s$I, lty = 4, col="red")
  #lines(dfTPLS_T_s$HV ~ dfTPLS_T_s$I, lty = 5, col="orange")
}


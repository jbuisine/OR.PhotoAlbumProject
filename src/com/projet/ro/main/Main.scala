package com.projet.ro.main

import com.projet.ro.utilities._
import scala.util.control.Breaks
import scala.util.Random

object Main{
  
  val df = new java.text.DecimalFormat("0.##")
  val pathPhoto = "data/info-photo.json"
  val pathAlbum = "data/info-album.json"
  val solFile = "fichier.sol"
  val nbPhotos = 55

  
  def main(args: Array[String]): Unit = {
    
    ComputeAlbum.init(pathPhoto, pathAlbum)
    
    var arr = ComputeAlbum.generateRandomSolution(nbPhotos)
    println(arr.size)
    
    var newArr = hillClimberFirstImrovment(nbPhotos, 10000, arr)
    println(ComputeAlbum.eval(newArr))
  }
  
  
  def hillClimberFirstImrovment(numberElements: Int, iteration: Int, arr: Array[Int]): Array[Int] = {
    var solution = arr
    
    if(solution == null){
      solution = ComputeAlbum.generateRandomSolution(nbPhotos)
    }
    
    var bestResult = ComputeAlbum.eval(solution)
   
    val random = Random;
    val inner = new Breaks;
    
    var next = true
    var i = 0
    
    do{
      var result = 0.0
      var firstRandomValue = 0
      var secondRandomValue = 0
      var temporyValue = 0
      
      var j = 0
      
      inner.breakable {
        for(j <- 0 to numberElements){
          firstRandomValue = random.nextInt(solution.length)
          secondRandomValue = random.nextInt(solution.length)
          
          temporyValue = solution(firstRandomValue)
          solution(firstRandomValue) = solution(secondRandomValue)
          solution(secondRandomValue) = temporyValue
          
          result = ComputeAlbum.eval(solution)
          
          if(result < bestResult)
            inner.break
          
          solution(secondRandomValue) = solution(firstRandomValue)
          solution(firstRandomValue) = temporyValue
        }
      }
      
      if(result < bestResult)
        bestResult = result
      else 
        next = false
       
      i+=1
      
    } while(next && i < iteration)
      
    return solution
  }
  
  
  
}

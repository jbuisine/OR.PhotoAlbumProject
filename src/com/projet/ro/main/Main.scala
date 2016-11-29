package com.projet.ro.main

import com.projet.ro.utilities._

object Main{
  
  val df = new java.text.DecimalFormat("0.##");
  val pathPhoto = "data/info-photo.json";
  val pathAlbum = "data/info-album.json";
  val solFile = "fichier.sol";
  val nbPhotos = 55;

  
  def main(args: Array[String]): Unit = {
    
    
    ComputeAlbum.init(pathPhoto, pathAlbum);
    
    var arr = ComputeAlbum.generateRandomSolution(nbPhotos);
    println(arr.size);
    
    arr.foreach { x => println(x) }
    println(ComputeAlbum.eval(arr));
  }
  
  
  def hillClimberFirstImrovment(arr: Array[Int]): Array[Int] = {
    var array = arr;
    if(array == null){
      array = ComputeAlbum.generateRandomSolution(nbPhotos);
    }
   
    return arr;
  }
  
  def getValue(): String = {
    return "My value"
  }
}

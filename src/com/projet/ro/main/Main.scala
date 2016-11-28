package com.projet.ro.main

import com.projet.ro.utilities._

object Main{
  
  def photoDist = Array[Array[Double]]()
  def albumInvDist =  Array[Array[Double]]()
  
  val df = new java.text.DecimalFormat("0.##");

  
  def main(args: Array[String]): Unit = {
    val file = new FileClass("fichier.sol")
    //photoDist = Array.ofDim[Double](2,3)
    file.writeLine(getValue, false)
    println("Hello World")
  }
  
  def getValue(): String = {
    return "My value"
  }
}
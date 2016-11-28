package com.projet.ro

import com.projet.ro.utilities._
import com.projet.ro.utilities.FileClass

object Main{
  
  def photoDist = Array[Array[Double]]()
  def albumInvDist =  Array[Array[Double]]()
  
  val df = new java.text.DecimalFormat("0.##");

  
  def main(args: Array[String]): Unit = {
    val file = new FileClass("example.txt")
    //photoDist = Array.ofDim[Double](2,3)
    file.writeLine(getValue, false)
    println("Hello World")
  }
  
  def getValue(): String = {
    return "My value"
  }
}
package com.projet.ro.main

import com.projet.ro.utilities._
import scala.util.control.Breaks
import scala.util.Random
import scala.collection.mutable.MutableList

object Main {

  val df = new java.text.DecimalFormat("0.##")
  val pathPhoto = "data/info-photo.json"
  val pathAlbum = "data/info-album.json"
  val solFile = "fichier.sol"
  val nbPhotos = 55

  def main(args: Array[String]): Unit = {

    //Initialize problem modelisation
    ComputeAlbum.init(pathPhoto, pathAlbum)

    var HC = hillClimberFirstImrovment(nbPhotos, 10000, null)
    println("HC -> ", ComputeAlbum.eval(HC))
    
    var ILS = IteratedLocalSearch(nbPhotos, 10000, 1000, 10)
    println("ILS -> ", ComputeAlbum.eval(ILS))
  }

  /**
   * HillClimber First improvment method used to get best local solution
   * @param numberElements
   * @param iteration
   * @param arr
   * @return best solution
   */
  def hillClimberFirstImrovment(numberElements: Int, iteration: Int, arr: Array[Int]): Array[Int] = {
    var solution = arr

    if (solution == null) {
      solution = ComputeAlbum.generateRandomSolution(nbPhotos)
    }

    var bestResult = ComputeAlbum.eval(solution)

    val random = Random;
    val inner = new Breaks;

    var next = true
    var i = 0

    do {
      var result = 0.0
      var firstRandomValue = 0
      var secondRandomValue = 0
      var temporyValue = 0

      var j = 0

      inner.breakable {
        for (j <- 0 to numberElements) {
          firstRandomValue = random.nextInt(solution.length)
          secondRandomValue = random.nextInt(solution.length)

          temporyValue = solution(firstRandomValue)
          solution(firstRandomValue) = solution(secondRandomValue)
          solution(secondRandomValue) = temporyValue

          result = ComputeAlbum.eval(solution)

          if (result < bestResult)
            inner.break

          solution(secondRandomValue) = solution(firstRandomValue)
          solution(firstRandomValue) = temporyValue
        }
      }

      if (result < bestResult)
        bestResult = result
      else
        next = false

      i += 1

    } while (next && i < iteration)

    return solution
  }
  
  /**
	 * Method which used the iteratedLocalSearch solution to find a solution
	 *
	 * @param numberElements
	 * @param iteration
	 * @param perturbation
	 * @return the best solution
	 */
  def IteratedLocalSearch(numberElements: Int, iteration: Int, iterationHillClimber: Int, perturbation: Int): Array[Int] = {
    var random = Random
    var solution = ComputeAlbum.generateRandomSolution(numberElements)
    
    hillClimberFirstImrovment(numberElements, iterationHillClimber, solution)
    
    var bestResult = ComputeAlbum.eval(solution)
    var i = 0
    
    do{
      ComputeAlbum.pertubationIterated(solution, perturbation, random)
      
      var currentSolution = hillClimberFirstImrovment(numberElements, iterationHillClimber, solution)
      
      var currentResult = ComputeAlbum.eval(currentSolution)
      
      if(currentResult < bestResult)
        solution = currentSolution
       
      i+=1
      
    } while(i < iteration)
    
    return solution
  }
  
  def GeneticEvolutionnayAlgorithm(mu: Int, lambda: Int, numberElements: Int, iteration: Int, hillClimberIteration: Int, numberOfHc: Int, numberOfPermutations: Int): Array[Int] = {
  
    var parentsSolutions = MutableList[Array[Int]]()
    
    for (i <- 1 to mu) {
      parentsSolutions += ComputeAlbum.generateRandomSolution(numberElements)
    }
      
    return null;
  }

}

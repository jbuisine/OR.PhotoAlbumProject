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

    var HC = HillClimberFirstImprovment(nbPhotos, 10000, null)
    println("HC -> ", ComputeAlbum.eval(HC))
    
    var ILS = IteratedLocalSearch(nbPhotos, 10000, 1000, 10)
    println("ILS -> ", ComputeAlbum.eval(ILS))
    
    GeneticEvolutionnaryAlgorithm(100, 50, nbPhotos, 3000, 1000, 100, 20);
  }

  /**
   * HillClimber First improvment method used to get best local solution
   * @param numberElements
   * @param iteratioe
   * @param arr
   * @return best solution
   */
  def HillClimberFirstImprovment(numberElements: Int, iteration: Int, arr: Array[Int]): Array[Int] = {
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
    
    HillClimberFirstImprovment(numberElements, iterationHillClimber, solution)
    
    var bestResult = ComputeAlbum.eval(solution)
    var i = 0
    
    do{
      ComputeAlbum.pertubationIterated(solution, perturbation, random)
      
      var currentSolution = HillClimberFirstImprovment(numberElements, iterationHillClimber, solution)
      
      var currentResult = ComputeAlbum.eval(currentSolution)
      
      if(currentResult < bestResult)
        solution = currentSolution
       
      i+=1
      
    } while(i < iteration)
    
    return solution
  }
  
  /**
	 * Method which used the genetic evolutionary algorithm
	 *
	 * @param mu
	 * @param lambda
	 * @param numberElements
	 * @param iteration
	 * @param hillClimberIteration
	 * @param numberOfPermutations
	 * @return best solution object found
	 */
  def GeneticEvolutionnaryAlgorithm(mu: Int, lambda: Int, numberElements: Int, iteration: Int, hillClimberIteration: Int, numberOfHc: Int, numberOfPermutations: Int): Array[Int] = {
  
    var rand = Random
    
    // Generate all parents solutions to start the algorithm
    var parentsSolutions = MutableList[Array[Int]]()
    
    for (i <- 1 to mu) {
      parentsSolutions += ComputeAlbum.generateRandomSolution(numberElements)
    }
      
    // Loop which defined the stop search (Iteration number)
    for(i <- 0 to iteration){
      var genitorsSolutions = MutableList[Array[Int]]()
      
      for(j <- 0 to lambda){
        var firstSelectedIndex = rand.nextInt(parentsSolutions.length);
				var secondSelectedIndex = rand.nextInt(parentsSolutions.length);
				
				if(ComputeAlbum.eval(parentsSolutions(firstSelectedIndex)) >= ComputeAlbum.eval(parentsSolutions(secondSelectedIndex))){
				  genitorsSolutions += parentsSolutions(firstSelectedIndex)
				}
				else {
				  genitorsSolutions += parentsSolutions(secondSelectedIndex)
				}
      }
      
      // Do variations on Genitors like mutation & HC
			// Mutation needs make probability
			for (j <- 0 to genitorsSolutions.length) {

				// Do permutation
				ComputeAlbum.pertubationIterated(genitorsSolutions(j), numberOfPermutations, rand);

				// Make hill climber on the current solution to improve the
				// genitor solution
				for (k <- 0 to numberOfHc) {
					var currentSolution = HillClimberFirstImprovment(genitorsSolutions(j).length, 1000,
							genitorsSolutions(j).clone())

					if (ComputeAlbum.eval(currentSolution) < ComputeAlbum.eval(genitorsSolutions(j))) {
						genitorsSolutions(j) = currentSolution
					}
				}
			}
			
			// Get the best between old parents & Genitors to make Survivors

			// First of all we need to add all children
			for(j <- 0 to lambda) {
				parentsSolutions += genitorsSolutions(j);
			}

			// Used to order list of solution by result
			parentsSolutions = parentsSolutions.sortWith((x, y) => ComputeAlbum.eval(x) < ComputeAlbum.eval(y))

			// Remove all elements without good result for the next step
			for (j <- 0 to parentsSolutions.length) {
				parentsSolutions = parentsSolutions.filterNot { elem  => elem == parentsSolutions(j) };
			}
			System.out.println("Genetic evolutionary algorithm : " + df.format(i * 100.0 / iteration) + "%");
 
    }
    //parentsSolutions.foreach { x => println(ComputeAlbum.eval(x)) }
    return parentsSolutions(0);
  }

}

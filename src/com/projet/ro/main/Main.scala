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

  /**
   * Main method which throws all algorithms
 	 * @param args
	 */
  def main(args: Array[String]): Unit = {

    val solutionFile = "fichier.sol"
    
    //Initialize problem modelisation
    Modelisation.init(pathPhoto, pathAlbum, "phashdist")

    //Objective function choice    
    var f: (Array[Int]) => Double = Modelisation.tagsEval;
    
    var HC = HillClimberFirstImprovement(nbPhotos, 1000, null, f)
    println("HC -> ", f(HC))

    var ILS = IteratedLocalSearch(nbPhotos, 1000, 100, 10, f)
    println("ILS -> ", f(ILS))
   
    var EA = GeneticEvolutionnaryAlgorithm(100, 20, nbPhotos, 300, 100, 100, 20, f);
    println("EA -> ", f(EA))
    
    Modelisation.writeSolution(solutionFile, EA)
  }

  /**
   * HillClimber First improvement method used to get best local solution
   * @param numberElements
   * @param iteratioe
   * @param arr
   * @return best solution
   */
  def HillClimberFirstImprovement (numberElements: Int, iteration: Int, arr: Array[Int], eval: (Array[Int]) => Double): Array[Int] = {
    var solution = arr

    if (solution == null) {
      solution = Modelisation.generateRandomSolution(nbPhotos)
    }

    var bestResult = eval(solution)

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

          result = eval(solution)

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
  def IteratedLocalSearch(numberElements: Int, iteration: Int, iterationHillClimber: Int, perturbation: Int, eval: (Array[Int]) => Double): Array[Int] = {
    var random = Random
    var solution = Modelisation.generateRandomSolution(numberElements)

    HillClimberFirstImprovement(numberElements, iterationHillClimber, solution, eval)

    var bestResult = eval(solution)
    var i = 0

    do{
      Modelisation.pertubationIterated(solution, perturbation, random)

      var currentSolution = HillClimberFirstImprovement(numberElements, iterationHillClimber, solution, eval)

      var currentResult = eval(currentSolution)

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
  def GeneticEvolutionnaryAlgorithm(mu: Int, lambda: Int, numberElements: Int, iteration: Int, hillClimberIteration: Int, numberOfHc: Int, numberOfPermutations: Int, eval: (Array[Int]) => Double): Array[Int] = {

    var rand = Random

    // Generate all parents solutions to start the algorithm
    var parentsSolutions = MutableList[Array[Int]]()

    for (i <- 0 to mu - 1) {
      parentsSolutions += Modelisation.generateRandomSolution(numberElements)
    }
    //println(parentsSolutions.length)

    // Loop which defined the stop search (Iteration number)
    for(i <- 0 to iteration - 1){
      var genitorsSolutions = MutableList[Array[Int]]()

      for(j <- 0 to lambda - 1){
        var firstSelectedIndex = rand.nextInt(parentsSolutions.length-1);
				var secondSelectedIndex = rand.nextInt(parentsSolutions.length-1);

				if(eval(parentsSolutions(firstSelectedIndex)) >= eval(parentsSolutions(secondSelectedIndex))){
				  genitorsSolutions += parentsSolutions(firstSelectedIndex)
				}
				else {
				  genitorsSolutions += parentsSolutions(secondSelectedIndex)
				}
      }

      // Do variations on Genitors like mutation & HC
			// Mutation needs make probability
			for (j <- 0 to genitorsSolutions.length - 1) {

				// Do permutation
				Modelisation.pertubationIterated(genitorsSolutions(j), numberOfPermutations, rand);

				// Make hill climber on the current solution to improve the
				// genitor solution
				for (k <- 0 to numberOfHc - 1) {
					var currentSolution = HillClimberFirstImprovement(genitorsSolutions(j).length, 1000,
							genitorsSolutions(j).clone(), eval)

					if (eval(currentSolution) < eval(genitorsSolutions(j))) {
						genitorsSolutions(j) = currentSolution
					}
				}
			}

			// Get the best between old parents & Genitors to make Survivors

			// First of all we need to add all children
			for(j <- 0 to lambda - 1) {
				parentsSolutions += genitorsSolutions(j);
			}

			// Used to order list of solution by result
			parentsSolutions = parentsSolutions.sortWith((x, y) => eval(x) < eval(y))

			// Remove all elements without good result for the next step
			parentsSolutions = parentsSolutions.take(mu)

			println("Genetic evolutionary algorithm : " + df.format(i * 100.0 / iteration) + "%");

    }
    
    return parentsSolutions(0);
  }

}

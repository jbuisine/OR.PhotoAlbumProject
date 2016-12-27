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
    Modelisation.init(pathPhoto, pathAlbum, "ahashdist")

    //Objective function choice    
    var f: (Array[Int]) => Double = Modelisation.hashEval;

    var HC = HillClimberFirstImprovement(nbPhotos, 10000, null, f)
    println("HC -> ", f(HC))
    

    var ILS = IteratedLocalSearch(nbPhotos, 1000, 10000, 5, f)
    println("ILS -> ", f(ILS))
   
    var EA = GeneticEvolutionnaryAlgorithm(50, 20, nbPhotos, 100, 10000, 100, 5, f);
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

  def HillClimberFirstImprovement (numberElements: Int, nbEval: Int, arr: Array[Int], eval: (Array[Int]) => Double): Array[Int] = {
    var solution = arr
    var index = 0;
    var i = 0
    
    val random = Random
    val inner = new Breaks

    if (solution == null) {
      solution = Modelisation.generateRandomSolution(nbPhotos)
    }

    var bestResult = eval(solution)

    while (i < nbEval){
      var result = 0.0;


      inner.breakable {
        for (j <- 0 to numberElements) {
        
          val firstRandomValue = random.nextInt(solution.length)
          val secondRandomValue = random.nextInt(solution.length)

          val temporyValue = solution(firstRandomValue)
          solution(firstRandomValue) = solution(secondRandomValue)
          solution(secondRandomValue) = temporyValue

          result = eval(solution)
          i+=1

          if (result < bestResult){
            
            inner.break
          }

          solution(secondRandomValue) = solution(firstRandomValue)
          solution(firstRandomValue) = temporyValue
        }
      }
      
      if (result < bestResult){
        bestResult = result
      }
    } 
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
    var solution = HillClimberFirstImprovement(numberElements, iterationHillClimber, Modelisation.generateRandomSolution(numberElements), eval)

    var bestResult = eval(solution)
    var bestSolution = solution.clone();
    var i = 0

    do{
      Modelisation.pertubationIterated(solution, perturbation, random)
      
      val currentSolution = HillClimberFirstImprovement(numberElements, iterationHillClimber, solution.clone(), eval)

      val currentResult = eval(currentSolution)

      if(currentResult < bestResult){
        bestResult = currentResult
        bestSolution = currentSolution.clone()
        solution = bestSolution.clone();
      }
      
      i+=1
      println("ILS -> "+ i*100.0/iteration + "%")
    } while(i < iteration)

    return bestSolution
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

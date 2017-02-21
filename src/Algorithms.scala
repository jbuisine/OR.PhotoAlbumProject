
import scala.util.control.Breaks
import scala.collection.mutable.{ListBuffer, MutableList}
import scala.util.Random

/**
  * Created by jbuisine on 12/01/17.
  *
  * Class which contains all algorithms
  */
object Algorithms {

  /**
    * Attribute used to saved number of evaluation
    */
  var nbEvaluation: Int = 0

  /**
    * HillClimber First improvement method used to get best local solution
    * @param numberElements
    * @param nbEval
    * @param arr
    * @return best solution
    */
  def HillClimberFirstImprovement(numberElements: Int, nbEval: Int, arr: Array[Int], eval: (Array[Int]) => Double): Array[Int] = {
    var solution = arr
    var index = 0
    var i = 0

    val random = Random
    val inner = new Breaks

    if (solution == null) {
      solution = UtilityClass.generateRandomSolution(Main.nbPhotos)
    }

    var bestResult = eval(solution)

    while (i < nbEval) {
      var result = 0.0

      inner.breakable {
        for (j <- 0 to numberElements) {

          val firstRandomValue = random.nextInt(solution.length)
          val secondRandomValue = random.nextInt(solution.length)

          val temporyValue = solution(firstRandomValue)
          solution(firstRandomValue) = solution(secondRandomValue)
          solution(secondRandomValue) = temporyValue

          result = eval(solution)
          i += 1
          nbEvaluation+=1

          if (result < bestResult) {

            inner.break
          }

          solution(secondRandomValue) = solution(firstRandomValue)
          solution(firstRandomValue) = temporyValue
        }
      }

      if (result < bestResult) {
        bestResult = result
      }
    }
    solution
  }

  /**
    * Method which used the iterated local search algorithm to find a better solution
    *
    * @param iteration
    * @param nbEvaluationHillClimber
    * @param perturbation
    * @return the best solution
    */
  def IteratedLocalSearch(numberElements: Int, iteration: Int, nbEvaluationHillClimber: Int, perturbation: Int, eval: (Array[Int]) => Double): Array[Int] = {
    var random = Random

    var solution = HillClimberFirstImprovement(numberElements, nbEvaluationHillClimber, UtilityClass.generateRandomSolution(Main.nbPhotos), eval)

    var bestResult = eval(solution)
    var bestSolution = solution.clone()
    var i = 0
    var percentEvolution = ""

    do {
      UtilityClass.perturbationIterated(solution, perturbation, random)

      val currentSolution = HillClimberFirstImprovement(numberElements, nbEvaluationHillClimber, solution.clone(), eval)

      val currentResult = eval(currentSolution)

      if (currentResult < bestResult) {
        bestResult = currentResult
        bestSolution = currentSolution.clone()
        solution = bestSolution.clone()
      }

      i += 1

      val lengthText = percentEvolution.length()
      percentEvolution = "ILS -> " + Main.df.format(i * 100.0 / iteration) + "%"
      UtilityClass.showEvolution(lengthText, percentEvolution)
    } while (i < iteration)

    return bestSolution
  }

  /**
    * Method which implements the genetic evolutionary algorithm
    *
    * @param mu
    * @param lambda
    * @param iteration
    * @param nbEvaluationHillClimber
    * @param numberOfPermutations
    * @return best solution object found
    */
  def GeneticEvolutionnaryAlgorithm(mu: Int, lambda: Int, iteration: Int, nbEvaluationHillClimber: Int, numberOfHc: Int, numberOfPermutations: Int, eval: (Array[Int]) => Double): Array[Int] = {

    var rand = Random
    var percentEvolution = ""

    // Generate all parents solutions to start the algorithm
    var parentsSolutions = MutableList[Array[Int]]()

    for (i <- 0 to mu - 1) {
      parentsSolutions += UtilityClass.generateRandomSolution(Main.nbPhotos)
    }
    //println(parentsSolutions.length)

    // Loop which defined the stop search (Iteration number)
    for (i <- 0 to iteration - 1) {
      var genitorsSolutions = MutableList[Array[Int]]()

      for (j <- 0 to lambda - 1) {
        var firstSelectedIndex = rand.nextInt(parentsSolutions.length - 1)
        var secondSelectedIndex = rand.nextInt(parentsSolutions.length - 1)

        if (eval(parentsSolutions(firstSelectedIndex)) >= eval(parentsSolutions(secondSelectedIndex))) {
          genitorsSolutions += parentsSolutions(firstSelectedIndex)
        } else {
          genitorsSolutions += parentsSolutions(secondSelectedIndex)
        }
      }

      // Do variations on Genitors like mutation & HC
      // Mutation needs make probability
      for (j <- 0 to genitorsSolutions.length - 1) {

        // Do permutation
        UtilityClass.perturbationIterated(genitorsSolutions(j), numberOfPermutations, rand)

        // Make hill climber on the current solution to improve the genitor solution
        for (k <- 0 to numberOfHc - 1) {
          var currentSolution = HillClimberFirstImprovement(genitorsSolutions(j).length, nbEvaluationHillClimber,
            genitorsSolutions(j).clone(), eval)

          if (eval(currentSolution) < eval(genitorsSolutions(j))) {
            genitorsSolutions(j) = currentSolution
          }
        }
      }

      // Get the best between old parents & Children to get Survivors

      // First of all we need to add all children
      for (j <- 0 to lambda - 1) {
        parentsSolutions += genitorsSolutions(j)
      }

      // Used to order list of solution by result
      parentsSolutions = parentsSolutions.sortWith((x, y) => eval(x) < eval(y))

      // Remove all elements without good result for the next step
      parentsSolutions = parentsSolutions.take(mu)

      val lengthText = percentEvolution.length()
      percentEvolution = "EA -> " + Main.df.format(i * 100.0 / iteration) + "%"
      UtilityClass.showEvolution(lengthText, percentEvolution)

    }

    return parentsSolutions(0)
  }

  /**
    * Method which used method implement pareto local search and find non determinist solution
    * @param nbEval
    * @param arr
    * @return best solution
    */
  def ParetoLocalSearch(nbEval: Int, arr: ListBuffer[Array[Int]], evals : Array[(Array[Int]) => Double]): ListBuffer[Array[Int]] = {

    var rand = new Random
    var percentEvolution = ""

    var solutions = arr
    var solutionsPassed = new ListBuffer[Array[Int]]
    var index = 0
    var i = 0

    val random = Random
    val inner = new Breaks

    if (solutions == null) {
      solutions = new ListBuffer[Array[Int]]()
      solutions += UtilityClass.generateRandomSolution(Main.nbPhotos)
    }

    while (i < nbEval) {
      //Select a non visited solution of solutions
      var current_sol = new Array[Int](Main.nbPhotos)
      do{

        val randIndex = rand.nextInt(solutions.length)
        current_sol = solutions(randIndex)

      }while(solutionsPassed.contains(current_sol))

      //Flypping each bit of the current solution
      (0 until Main.nbPhotos).foreach( index => {
        val randomValue = random.nextInt(Main.nbPhotos)

        val temporyValue = current_sol(index)
        current_sol(index) = current_sol(randomValue)
        current_sol(randomValue) = temporyValue

        //Add this new solutions with all old solutions
        solutions += current_sol.clone()

        current_sol(randomValue) = current_sol(index)
        current_sol(index) = temporyValue
      })

      i += 1
      nbEvaluation+=1

      //Flag solution as visited
      solutionsPassed += current_sol

      //Take only non dominated solutions
      solutions = UtilityClass.getNonDominatedSolutions(solutions, evals)

      val lengthText = percentEvolution.length()
      percentEvolution = "PLS -> " + Main.df.format(i * 100.0 / nbEval) + "% "
      UtilityClass.showEvolution(lengthText, percentEvolution)
    }
    solutions
  }

  /**
    *
    * MOEAD Algorithm implementation
    *
    * @param nbEval
    * @param N
    * @param T
    * @param evals
    * @param choice
    * @return
    */
  def MOEAD_Algorithm(nbEval: Int, N: Int, T: Int, evals : Array[(Array[Int]) => Double], choice: Int): ListBuffer[Array[Int]] = {

    /**
      * All utilities local variables
      */
    var random = new Random
    var percentEvolution = ""

    /**
      * 1. Initialize of the context
      */

    //Step 1.0 : Initialization of each scalar sub problems
    var vectors = MOEADInit.generateVectors(N)

    //Step 1.1 : Initialization of EP (set to empty)
    var nonDominatedSolutions = new ListBuffer[Array[Int]]()

    //Step 1.2 : Compute distances between T closest weight vector to each weight vector
    var B = MOEADInit.getNeightborsVectors(vectors, T)

    //Step 1.3 : Initialize population (solution for each sub problems)
    var population = MOEADInit.generateRandomPopulation(N)
    var values = MOEADInit.computeFunctionValues(population, evals)

    //Step 1.4 : Initialize reference point
    var z = MOEADInit.getRefPoint(values, evals.length)

    var evaluation = 0

    /**
      * 2. Update
      */
    do{


      (0 until N).foreach( i => {

        /**
          *  2.1 Reproduction : Select randomly two solutions to create new solution y
          *
          *  For the moment we just permute randomly values of solution selected to create new solution
          */

        //2.1.1 : Getting random index of closest vectors and retrieve solution associated
        val firstIndex = B(i)(random.nextInt(B(i).length))
        val secondIndex = B(i)(random.nextInt(B(i).length))

        var firstSol = population(firstIndex).clone()
        //var secondSol = population(secondIndex).clone()

        //2.1.1. Create new solution with the selected solutions

        UtilityClass.perturbationIterated(firstSol, 10, random)
        //UtilityClass.pertubationIterated(secondSol, 10, random)

        var newSol = new Array[Int](Main.nbPhotos)

        //Review this mutation method later
        /*(0 until Main.nbPhotos).foreach( index => {
          if(random.nextInt(1) == 0)
            newSol(firstSol.indexOf(index)) = index
          else
            newSol(secondSol.indexOf(index)) = index
        })*/

        newSol = firstSol

        /**
          * 2.2 Improvement : Not developed at this time (Perhaps later)
          */


        /**
          * 2.3 Update z : reference point
          */
        //Setting new reference point if exists
        (0 until evals.length).foreach( index => {
          z(index) = math.min(z(index), evals(index)(newSol))
        })

        /**
          * 2.4 Update of Neighboring Solutions
          */

        //For each solution into the population check if new solution is better
        (0 until B(i).length).foreach( index => {
          val neighborIndex = B(i)(index)

          //Getting Tchebivech function result for the new solution and current solution
          val gNeighbor = MOEADInit.computeCombinedValues(population(neighborIndex), z, vectors(neighborIndex), evals, choice)
          val gY = MOEADInit.computeCombinedValues(newSol, z, vectors(neighborIndex), evals, choice)

          //If better update population and values
          if (gY < gNeighbor) {
            population(neighborIndex) = newSol
            (0 until evals.length).foreach( current => {
              values(neighborIndex)(current) = evals(current)(population(neighborIndex))
            })
          }
        })

        //Increment number of evaluation
        evaluation += 1

        /**
          * 2.5 Update of EP : Update non dominated solutions
          */

        //Add new solution to EP
        nonDominatedSolutions += newSol

        val oldLength = nonDominatedSolutions.length

        //Get new EP without dominated solutions (if they exist)
        nonDominatedSolutions = UtilityClass.getNonDominatedSolutions(nonDominatedSolutions, evals)

      })

      val lengthText = percentEvolution.length()
      percentEvolution = "MOEA/D -> " + Main.df.format(evaluation * 100.0 / nbEval) + "% "
      UtilityClass.showEvolution(lengthText, percentEvolution)

    }while(evaluation < nbEval)

    nonDominatedSolutions
  }
}

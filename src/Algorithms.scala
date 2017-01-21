
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
  def IteratedLocalSearch(iteration: Int, nbEvaluationHillClimber: Int, perturbation: Int, eval: (Array[Int]) => Double): Array[Int] = {
    var random = Random
    var solution = HillClimberFirstImprovement(Main.nbPhotos, nbEvaluationHillClimber, UtilityClass.generateRandomSolution(Main.nbPhotos), eval)

    var bestResult = eval(solution)
    var bestSolution = solution.clone()
    var i = 0
    var percentEvolution = ""

    do {
      UtilityClass.pertubationIterated(solution, perturbation, random)

      val currentSolution = HillClimberFirstImprovement(Main.nbPhotos, nbEvaluationHillClimber, solution.clone(), eval)

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
        UtilityClass.pertubationIterated(genitorsSolutions(j), numberOfPermutations, rand)

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

  def MOEAD_Algorithm(nbEval: Int, nbDirection: Int, evals : Array[(Array[Int]) => Double]): ListBuffer[Array[Int]] = {

    var random = new Random
    var percentEvolution = ""

    //Fixed objective size to 2 functions
    var directions = new Array[Array[Double]](nbDirection)
    var solutions = new ListBuffer[Array[Int]]

    //Init vectors direction
    (0 until nbDirection).foreach( index => {
      directions(index) = new Array[Double](2)
      directions(index)(0) = Math.cos(index * Math.PI / (2*(nbDirection-1)))
      directions(index)(1) = Math.sin(index * Math.PI / (2*(nbDirection-1)))

      //Generate random sol for each direction
      solutions += UtilityClass.generateRandomSolution(Main.nbPhotos)
    })

    var i = 0

    do{

      (0 until directions.length).foreach( direction_index => {

        //Flypping each bit of the current solution
        (0 until Main.nbPhotos).foreach( index => {
          val randomValue = random.nextInt(Main.nbPhotos)

          val result_sol:Double = directions(direction_index)(0)*evals(0)(solutions(direction_index)) + directions(direction_index)(1)*evals(1)(solutions(direction_index))

          val temporyValue = solutions(direction_index)(index)
          solutions(direction_index)(index) = solutions(direction_index)(randomValue)
          solutions(direction_index)(randomValue) = temporyValue

          val current_sol:Double = directions(direction_index)(0)*evals(0)(solutions(direction_index)) + directions(direction_index)(1)*evals(1)(solutions(direction_index))

          if(current_sol >= result_sol){
            solutions(direction_index)(randomValue) = solutions(direction_index)(index)
            solutions(direction_index)(index) = temporyValue
          }
          i+=1
        })
      })

      val lengthText = percentEvolution.length()
      percentEvolution = "MOEA/D -> " + Main.df.format(i * 100.0 / nbEval) + "% "
      UtilityClass.showEvolution(lengthText, percentEvolution)

    }while(i < nbEval)

    solutions
  }
}

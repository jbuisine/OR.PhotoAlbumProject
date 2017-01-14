
import scala.util.control.Breaks
import scala.collection.mutable.MutableList
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
  def HillClimberFirstImprovement(numberElements: Int, nbEval: Int, arr: Array[Int], evals : Array[(Array[Int]) => Double]): Array[Int] = {

    var solution = arr
    var index = 0
    var i = 0
    var bestResults = new Array[Double](evals.length)

    val random = Random
    val inner = new Breaks

    if (solution == null) {
      solution = UtilityClass.generateRandomSolution(Main.nbPhotos)
    }

    //For each function we take the best result
    (0 until evals.length).foreach( index => bestResults(index) = evals(index)(solution))

    while (i < nbEval) {
      var results = new Array[Double](evals.length)
      var checkSolution = false

      inner.breakable {
        for (j <- 0 to numberElements) {

          val firstRandomValue = random.nextInt(solution.length)
          val secondRandomValue = random.nextInt(solution.length)

          val temporyValue = solution(firstRandomValue)
          solution(firstRandomValue) = solution(secondRandomValue)
          solution(secondRandomValue) = temporyValue

          (0 until evals.length).foreach(index => results(index) = evals(index)(solution))

          i += 1
          nbEvaluation+=1

          checkSolution = false

          (0 until evals.length).foreach(index => {
            if(results(index) < bestResults(index))
              checkSolution = true
            else
              checkSolution = false
          })

          if(checkSolution){
            inner.break
          }

          solution(secondRandomValue) = solution(firstRandomValue)
          solution(firstRandomValue) = temporyValue
        }
      }

      (0 until evals.length).foreach(index => {
        if (checkSolution)
          bestResults(index) = results(index)
      })
    }
    solution
  }

  /**
    * Method which used the iterated local search algorithm to find a better solution
    *
    * @param numberElements
    * @param iteration
    * @param nbEvaluationHillClimber
    * @param perturbation
    * @return the best solution
    */
  def IteratedLocalSearch(numberElements: Int, iteration: Int, nbEvaluationHillClimber: Int, perturbation: Int, evals : Array[(Array[Int]) => Double]): Array[Int] = {

    var random = Random
    var solution = HillClimberFirstImprovement(numberElements, nbEvaluationHillClimber, UtilityClass.generateRandomSolution(numberElements), evals)

    var bestResults = new Array[Double](evals.length)
    //For each function we take the best result
    (0 until evals.length).foreach( index => bestResults(index) = evals(index)(solution))

    var i = 0
    var percentEvolution = ""

    do {
      var checkSolution = false
      UtilityClass.pertubationIterated(solution, perturbation, random)

      val currentSolution = HillClimberFirstImprovement(numberElements, nbEvaluationHillClimber, solution, evals)

      var results = new Array[Double](evals.length)
      //For each functions we saved the current result
      (0 until evals.length).foreach(index => results(index) = evals(index)(currentSolution))

      (0 until evals.length).foreach(index => {
        if(results(index) < bestResults(index))
          checkSolution = true
        else
          checkSolution = false
      })

      if (checkSolution) {
        (0 until evals.length).foreach(index => {
          bestResults(index) = results(index)
        })
        solution = currentSolution
      }

      i += 1

      val lengthText = percentEvolution.length()
      percentEvolution = "ILS -> " + Main.df.format(i * 100.0 / iteration) + "%"
      UtilityClass.showEvolution(lengthText, percentEvolution)
    } while (i < iteration)

    solution
  }

  /**
    * Method which implements the genetic evolutionary algorithm
    *
    * @param mu
    * @param lambda
    * @param numberElements
    * @param iteration
    * @param nbEvaluationHillClimber
    * @param numberOfPermutations
    * @return best solution object found
    */
  def GeneticEvolutionnaryAlgorithm(mu: Int, lambda: Int, numberElements: Int, iteration: Int, nbEvaluationHillClimber: Int, numberOfHc: Int, numberOfPermutations: Int, evals: Array[(Array[Int]) => Double]): Array[Int] = {

    var rand = Random
    var percentEvolution = ""

    // Generate all parents solutions to start the algorithm
    var parentsSolutions = MutableList[Array[Int]]()

    for (i <- 0 to mu - 1) {
      parentsSolutions += UtilityClass.generateRandomSolution(numberElements)
    }
    //println(parentsSolutions.length)

    // Loop which defined the stop search (Iteration number)
    for (i <- 0 to iteration - 1) {
      var genitorsSolutions = MutableList[Array[Int]]()

      for (j <- 0 to lambda - 1) {
        var firstSelectedIndex = rand.nextInt(parentsSolutions.length - 1);
        var secondSelectedIndex = rand.nextInt(parentsSolutions.length - 1);

        var checkFisrtWinner = false
        var checkSecondWinner = false
        (0 until evals.length).foreach(index => {
          if(evals(index)(parentsSolutions(firstSelectedIndex)) <= evals(index)(parentsSolutions(secondSelectedIndex))) {
            checkFisrtWinner = true
            checkSecondWinner = false
          }else {
            checkFisrtWinner = false
            checkSecondWinner = true
          }
        })

        if (checkFisrtWinner) {
          genitorsSolutions += parentsSolutions(firstSelectedIndex)
        } else if(checkSecondWinner){
          genitorsSolutions += parentsSolutions(secondSelectedIndex)
        }
      }

      // Do variations on Genitors like mutation & HC
      // Mutation needs make probability
      for (j <- 0 to genitorsSolutions.length - 1) {

        // Do permutation
        UtilityClass.pertubationIterated(genitorsSolutions(j), numberOfPermutations, rand);

        // Make hill climber on the current solution to improve the genitor solution
        for (k <- 0 to numberOfHc - 1) {
          var currentSolution = HillClimberFirstImprovement(genitorsSolutions(j).length, nbEvaluationHillClimber,
            genitorsSolutions(j).clone(), evals)

          var results = new Array[Double](evals.length)
          var checkSolution = false
          //For each functions we saved the current result
          (0 until evals.length).foreach(index => results(index) = evals(index)(currentSolution))

          (0 until evals.length).foreach(index => {
            if(results(index) < evals(index)(genitorsSolutions(j)))
              checkSolution = true
            else
              checkSolution = false
          })

          if (checkSolution) {
            genitorsSolutions(j) = currentSolution
          }
        }
      }

      // Get the best between old parents & Children to get Survivors

      // First of all we need to add all children
      for (j <- 0 to lambda - 1) {
        parentsSolutions += genitorsSolutions(j);
      }

      // Used to order list of solution by result
      parentsSolutions = parentsSolutions.sortWith((x, y) => evals(0)(x) < evals(0)(y))

      // Remove all elements without good result for the next step
      parentsSolutions = parentsSolutions.take(mu)

      val lengthText = percentEvolution.length()
      percentEvolution = "EA -> " + Main.df.format(i * 100.0 / iteration) + "%"
      UtilityClass.showEvolution(lengthText, percentEvolution)
    }

    parentsSolutions(0);
  }
}

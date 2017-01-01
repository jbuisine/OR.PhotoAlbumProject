
import scala.util.control.Breaks
import scala.util.Random
import scala.collection.mutable.MutableList
import scala.util.control.Breaks._
import scala.util.{ Try, Success, Failure }

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

    //Sanner utility object
    val scanner = new java.util.Scanner(System.in)
    val breaker = new Breaks

    //File where solution is writed
    val solutionFile = "fichier.sol"
    var solution = Array[Int](nbPhotos)

    val numberFunction = 4;
    val numberAlgo = 3;

    //Objective function
    var f: (Array[Int]) => Double = null

    val hashTypes = Array("ahashdist", "phashdist", "dhashdist")

    // Choices variables
    var functionChoice = "";
    var hashChoice = "";
    var algorithmChoice = "";

    var checkFuncChoice = false;
    do {
      println("Which type of objective function do you want to use ?" +
        "\n1. Hash function objective (You need later to select between aHash, pHash & dHash attributes)" +
        "\n2. Tags function objective"+
        "\n3. Colors function objective"+
        "\n4. Grey AVG function objective");
      functionChoice = scanner.nextLine();
      Try(functionChoice.toInt) match {
        case Success(num) => {
          if (num <= 0 || num > numberFunction)
            println("Number written does not exist into the list")
          else
            checkFuncChoice = true;
        }
        case Failure(thrown) => {
          println("Error, please select another function choice")
        }
      }
    } while (!checkFuncChoice)

    if (functionChoice.toInt == 1) {
      var attributeChoice = false;
      do {

        println("Which type of attributes do you want ?" +
          "\n1. Average hash" +
          "\n2. Perspective hash" +
          "\n3. Difference hash");
        hashChoice = scanner.nextLine();
        Try(hashChoice.toInt) match {
          case Success(num) => {
            if (num <= 0 || num > hashTypes.length)
              println("Number written does not exist into the list")
            else
              attributeChoice = true;
          }
          case Failure(f) => {
            println("Error, please select another attribute choice")
          }
        }
      } while (!attributeChoice)
    }

    //Initialize problem modelisation
    functionChoice.toInt match {
      case 1 =>
        Modelisation.initHash(pathPhoto, pathAlbum, hashTypes(hashChoice.toInt - 1))
        f = Modelisation.hashEval
      case 2 =>
        Modelisation.initTags(pathPhoto)
        f = Modelisation.tagsEval
      case 3 =>
        Modelisation.initColors(pathPhoto)
        f = Modelisation.colorsEval
      case 4 =>
        Modelisation.initGreyAvg(pathPhoto)
        f = Modelisation.greyAVGEval
    }

    var checkAlgoChoice = false;
    do {

      println("Which type of algorithm do you want to executes ?" +
        "\n1. Hill Climber First Improvment" +
        "\n2. Iterated Local Search" +
        "\n3. Evolutionary Algorithm");
      algorithmChoice = scanner.nextLine();
      Try(algorithmChoice.toInt) match {
        case Success(num) => {
          if (num <= 0 || num > numberAlgo)
            println("Number written does not exist into the list")
          else
            checkAlgoChoice = true;
        }
        case Failure(f) => {
          println("Error, please select another algorithm choice")
        }
      }
    } while (!checkAlgoChoice)

    algorithmChoice.toInt match {
      case 1 =>
        solution = HillClimberFirstImprovement(nbPhotos, 10000, null, f)
        println("HC -> " + f(solution))
      case 2 =>
        solution = IteratedLocalSearch(nbPhotos, 1000, 10000, 5, f)
        println("ILS -> " + f(solution))
      case 3 =>
        solution = GeneticEvolutionnaryAlgorithm(50, 20, nbPhotos, 100, 10000, 100, 5, f);
        println("EA -> " + f(solution))
    }
    Modelisation.writeSolution(solutionFile, solution)
  }

  /**
   * HillClimber First improvement method used to get best local solution
   * @param numberElements
   * @param iteratioe
   * @param arr
   * @return best solution
   */
  def HillClimberFirstImprovement(numberElements: Int, nbEval: Int, arr: Array[Int], eval: (Array[Int]) => Double): Array[Int] = {
    var solution = arr
    var index = 0;
    var i = 0

    val random = Random
    val inner = new Breaks

    if (solution == null) {
      solution = Modelisation.generateRandomSolution(nbPhotos)
    }

    var bestResult = eval(solution)

    while (i < nbEval) {
      var result = 0.0;

      inner.breakable {
        for (j <- 0 to numberElements) {

          val firstRandomValue = random.nextInt(solution.length)
          val secondRandomValue = random.nextInt(solution.length)

          val temporyValue = solution(firstRandomValue)
          solution(firstRandomValue) = solution(secondRandomValue)
          solution(secondRandomValue) = temporyValue

          result = eval(solution)
          i += 1

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
    return solution
  }

  /**
   * Method which used the iterated local search algorithm to find a better solution
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

    do {
      Modelisation.pertubationIterated(solution, perturbation, random)

      val currentSolution = HillClimberFirstImprovement(numberElements, iterationHillClimber, solution.clone(), eval)

      val currentResult = eval(currentSolution)

      if (currentResult < bestResult) {
        bestResult = currentResult
        bestSolution = currentSolution.clone()
        solution = bestSolution.clone();
      }

      i += 1
      println("ILS -> " + i * 100.0 / iteration + "%")
    } while (i < iteration)

    return bestSolution
  }

  /**
   * Method which implements the genetic evolutionary algorithm
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
    for (i <- 0 to iteration - 1) {
      var genitorsSolutions = MutableList[Array[Int]]()

      for (j <- 0 to lambda - 1) {
        var firstSelectedIndex = rand.nextInt(parentsSolutions.length - 1);
        var secondSelectedIndex = rand.nextInt(parentsSolutions.length - 1);

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
      for (j <- 0 to lambda - 1) {
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


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
  val scanner = new java.util.Scanner(System.in)

  // Choices variables
  var functionChoice: Int = _;
  var hashChoice: Int = _;
  var algorithmChoice: Int = _;

  /**
   * Main method which throws all algorithms
   * @param args
   */
  def main(args: Array[String]): Unit = {

    //Sanner utility object
    val breaker = new Breaks

    //File where solution is writed
    val solutionFile = "fichier.sol"
    var solution = Array[Int](nbPhotos)

    val numberFunction = 4;
    val numberAlgo = 3;

    //Objective function
    var f: (Array[Int]) => Double = null

    val hashTypes = Array("ahashdist", "phashdist", "dhashdist")

    val functionQuestion =
      "Which type of objective function do you want to use ?" +
        "\n1. Hash function objective (You need later to select between aHash, pHash & dHash attributes)" +
        "\n2. Tags function objective" +
        "\n3. Colors function objective" +
        "\n4. Grey AVG function objective " +
        "\n\n";
    functionChoice = getScannerValue(functionQuestion, "function", 0, numberFunction)

    if (functionChoice == 1) {

      val hashQuestion = "Which type of attributes do you want ?" +
        "\n1. Average hash" +
        "\n2. Perspective hash" +
        "\n3. Difference hash" +
        "\n\n";
      hashChoice = getScannerValue(hashQuestion, "hash attribute", 0, hashTypes.length)
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

    val algorithmQuestion = "Which type of algorithm do you want to executes ?" +
      "\n1. Hill Climber First Improvment" +
      "\n2. Iterated Local Search" +
      "\n3. Evolutionary Algorithm" +
      "\n\n";
    algorithmChoice = getScannerValue(algorithmQuestion, "algorithm", 0, numberAlgo)

    algorithmChoice.toInt match {
      case 1 => {

        val iterationQuestion = "Please select number of evaluation you want for you HC (between 1 and 100000)"
        val numberEvaluation = getScannerValue(iterationQuestion, "number of iteration", 1, 100000)

        println("\n------------------------------------------------------------------------------------------")
        println("HC algorithm starts search one of the best solution... It will take few seconds or more...")
        println("------------------------------------------------------------------------------------------\n")
        solution = HillClimberFirstImprovement(nbPhotos, numberEvaluation, null, f)

        println("\nHC better score found -> " + f(solution))
      }
      case 2 => {
        val ilsQuestion = "This algorithm need some paramaters : " +
          "\n1. Number of iteration for ILS (between 1 and 100000)" +
          "\n2. Number of evaluation for all HC (between 1 and 100000)" +
          "\n3. Number of maximum elements you want to permute for each solution (between 1 and " + nbPhotos + ")" +
          "\n\n"
        println(ilsQuestion)

        val iterationQuestion = "1. So, please select number of iteration for ILS"
        val numberIteration = getScannerValue(iterationQuestion, "number of iteration", 1, 100000)

        val evaluationQuestion = "2. Select number of evaluation for all HC"
        val numberEvaluation = getScannerValue(evaluationQuestion, "number of evaluation", 1, 100000)

        val permutationQuestion = "3. Select number of maximum elements permuted for each solution"
        val numberPermutation = getScannerValue(permutationQuestion, "number of permutation", 1, nbPhotos)

        println("\n------------------------------------------------------------------------------------------")
        println("ILS algorithm starts search one of the best solution... It will take few minutes")
        println("------------------------------------------------------------------------------------------\n")
        solution = IteratedLocalSearch(nbPhotos, numberIteration, numberEvaluation, numberPermutation + 1, f)
        println("\nILS better score found-> " + f(solution))
      }
      case 3 => {

        val eaQuestion = "This algorithm need some paramaters : " +
          "\n1. Number of mu (parents) elements (between 1 and 1000)" +
          "\n2. Number of lambda (children) elements (between 1 and 1000)" +
          "\n3. Number of iteration for EA algorithm (between 1 and 100000) " +
          "\n4. Number of evaluation for each HC (between 1 and 100000)" +
          "\n5. Number of HC you want to do for each genitors (same number of lambda) solutions (between 1 and 100000)" +
          "\n6. Number of maximum elements you want to permute for each solution (between 1 and " + nbPhotos + ")" +
          "\n\n"
        println(eaQuestion)

        val muQuestion = "1. So, please select number of mu elements"
        val mu = getScannerValue(muQuestion, "number of mu", 1, 1000)

        val lambdaQuestion = "2. Select number of lambda elements"
        val lambda = getScannerValue(lambdaQuestion, "number of lambda", 1, 1000)

        val iterationQuestion = "3. Select number of iteration you want for EA"
        val numberIteration = getScannerValue(iterationQuestion, "number of iteration", 1, 100000)

        val evaluationQuestion = "4. Select number of evaluation for all HC"
        val numberEvaluation = getScannerValue(evaluationQuestion, "number of evaluation", 1, 100000)

        val hcQuestion = "5. Select number of evaluation for all HC"
        val hcNumber = getScannerValue(hcQuestion, "number of HC", 1, 100000)

        val permutationQuestion = "6. Select number of maximum elements permuted for each solution"
        val numberPermutation = getScannerValue(permutationQuestion, "number of elements to permute", 1, nbPhotos)

        println("\n------------------------------------------------------------------------------------------")
        println("EA algorithm starts search one of the best solution... It will take few minutes or more...")
        println("------------------------------------------------------------------------------------------\n")

        solution = GeneticEvolutionnaryAlgorithm(mu, lambda, nbPhotos, numberIteration, numberEvaluation, hcNumber, numberPermutation, f);
        println("\nEA better score found -> " + f(solution))
      }
    }
    Modelisation.writeSolution(solutionFile, solution)
  }

  /**
   * HillClimber First improvement method used to get best local solution
   * @param numberElements
   * @param nbEval
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
   * @param nbEvaluationHillClimber
   * @param perturbation
   * @return the best solution
   */
  def IteratedLocalSearch(numberElements: Int, iteration: Int, nbEvaluationHillClimber: Int, perturbation: Int, eval: (Array[Int]) => Double): Array[Int] = {
    var random = Random
    var solution = HillClimberFirstImprovement(numberElements, nbEvaluationHillClimber, Modelisation.generateRandomSolution(numberElements), eval)

    var bestResult = eval(solution)
    var bestSolution = solution.clone();
    var i = 0
    var percentEvolution = ""

    do {
      Modelisation.pertubationIterated(solution, perturbation, random)

      val currentSolution = HillClimberFirstImprovement(numberElements, nbEvaluationHillClimber, solution.clone(), eval)

      val currentResult = eval(currentSolution)

      if (currentResult < bestResult) {
        bestResult = currentResult
        bestSolution = currentSolution.clone()
        solution = bestSolution.clone();
      }

      i += 1

      val lengthText = percentEvolution.length()
      percentEvolution = "ILS -> " + df.format(i * 100.0 / iteration) + "%"
      showEvolution(lengthText, percentEvolution)
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
   * @param nbEvaluationHillClimber
   * @param numberOfPermutations
   * @return best solution object found
   */
  def GeneticEvolutionnaryAlgorithm(mu: Int, lambda: Int, numberElements: Int, iteration: Int, nbEvaluationHillClimber: Int, numberOfHc: Int, numberOfPermutations: Int, eval: (Array[Int]) => Double): Array[Int] = {

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
        parentsSolutions += genitorsSolutions(j);
      }

      // Used to order list of solution by result
      parentsSolutions = parentsSolutions.sortWith((x, y) => eval(x) < eval(y))

      // Remove all elements without good result for the next step
      parentsSolutions = parentsSolutions.take(mu)

      println("EA : " + df.format(i * 100.0 / iteration) + "%");

    }

    return parentsSolutions(0);
  }

  /**
   * Function which gets integer value until is not correct
   * @param q
   * @param failure type
   * @param min : criteria of minimum value asked (included)
   * @param max : criteria of maximum value asked (excluded)
   * @return
   */
  def getScannerValue(q: String, failure: String, min: Int, max: Int): Int = {
    var output = "";
    var choice = false;
    do {

      println(q);
      output = scanner.nextLine();
      Try(output.toInt) match {
        case Success(num) => {
          if (num <= min || num > max)
            println("Number written is not excepted")
          else
            choice = true;
        }
        case Failure(f) => {
          println("Error, please select another " + failure)
        }
      }
    } while (!choice)
    output.toInt
  }

  /**
   * Function which show evolution percentage of algorithm
   * @param previousText
   * @param text
   */
  def showEvolution(previousText: Int, text: String): Unit = {
    var lengthContent = previousText
    if (lengthContent < text.length()) {
      lengthContent += text.length() - previousText

    }
    for (i <- 0 until lengthContent) {
      print("\b")
    }

    print(text)
    
  }
}

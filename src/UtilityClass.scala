import scala.util.{ Try, Success, Failure }
import java.util.Random

/**
 * @author j.buisine
 *
 */
object UtilityClass {
  
   val scanner = new java.util.Scanner(System.in)
  
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
            println("Number written is not excepted.")
          else
            choice = true;
        }
        case Failure(f) => {
          println("Error, please select another " + failure + ".")
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
  
  /**
   * Function which generates random solution of photos order
   */
  def generateRandomSolution(number: Int): Array[Int] = {
    val random = new Random()
    val randomArray = Array.ofDim[Int](number)
    for (i <- 0 until number) {
      randomArray(i) = i
    }
    for (i <- 0 until number) {
      val randomValue = random.nextInt(number)
      val temporyValue = randomArray(i)
      randomArray(i) = randomArray(randomValue)
      randomArray(randomValue) = temporyValue
    }
    randomArray
  }

  /**
   * Function which permutes photos of a solution
   *
   * @param solution
   * @param number
   * @param r
   */
  def pertubationIterated(solution: Array[Int], number: Int, r: scala.util.Random) {
    val nbMutations = r.nextInt(number) + 1
    for (i <- 0 until nbMutations) {
      var oldValue = 0
      val firstBoxElement = r.nextInt(solution.length)
      val secondBoxElement = r.nextInt(solution.length)
      oldValue = solution(firstBoxElement)
      solution(firstBoxElement) = solution(secondBoxElement)
      solution(secondBoxElement) = oldValue
    }
  }

  /**
   * Function which writes best solution into the solution file
   *
   * @param filename
   * @param bestSolution
   */
  def writeSolution(filename: String,
    bestSolution: Array[Int]) {
    val file = new FileClass(filename)
    var line = "";
    for (i <- 0 until bestSolution.length) {
      line += bestSolution(i) + " "
    }
    file.writeLine(line, false)
    println(s"Solution saved into $filename")
  }

  /**
   * Function which writes number evaluation and result
   *
   * @param filename
   * @param nbEval
   * @param result
   * @param solution
   * @param bestSolution
   */
  def writeEvaluation(filename: String, nbEval: Int, result: Double, solution: Array[Int]) {

    val file = new FileClass("scores/" + filename)
    var line = nbEval + "," + result + ","

    for (i <- 0 until solution.length) {
      line += solution(i) + " "
    }

    file.writeLine(line, true)
    println(s"Evaluation saved into $filename")
  }
}
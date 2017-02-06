import java.util.Random

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.{Failure, Success, Try}

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
    val file = new FileClass("../resources/solutions/"+filename)
    var line = "";
    for (i <- 0 until bestSolution.length) {
      line += bestSolution(i) + " "
    }
    file.writeLine(line, false)
    println(s"Solution saved into solutions/$filename")
  }

  /**
   * Function which writes number evaluation and result
   *
   * @param filename
   * @param nbEval
   * @param result
   * @param solution
   */
  def writeEvaluation(filename: String, nbEval: Int, result: Double, solution: Array[Int]) {

    val file = new FileClass("../resources/scores/" + filename)
    var line = nbEval + "," + result + ","

    for (i <- 0 until solution.length) {
      line += solution(i) + " "
    }

    file.writeLine(line, true)
    println(s"Evaluation saved into scores/$filename")
  }

  /**
    * Function which writes number evaluation and result
    *
    * @param filename
    * @param solution
    */
  def writePLSScores(filename: String, evals : Array[(Array[Int]) => Double], solution: Array[Int]) {

    val file = new FileClass("../resources/scores/" + filename)

    var line = ""
    (0 until evals.length).foreach( index => {
      line += evals(index)(solution) + ","
    })

    for (i <- 0 until solution.length) {
      line += solution(i) + " "
    }

    file.writeLine(line, true)
  }

  /**
    * Function used to check if solutions can be removed because it is a non dominated solution
    * @param arr
    * @param evals
    * @return non dominated solutions
    */
  def getNonDominatedSolutions(arr: ListBuffer[Array[Int]], evals : Array[(Array[Int]) => Double]) : ListBuffer[Array[Int]] = {
    var solutions = arr
    var elements = new ListBuffer[Int]

    (0 until arr.length).foreach( sol_index => {

      (0 until arr.length).foreach( current_sol_index => {
        var numberDominatedFunction = 0
        (0 until evals.length).foreach( index => {
          if(evals(index)(arr(sol_index)) > evals(index)(arr(current_sol_index)))
            numberDominatedFunction += 1
        })

        if(numberDominatedFunction >= evals.length && !elements.contains(sol_index))
            elements += sol_index
      })
    })

    var elem = 0
    if(elements.length != arr.length){
      elements.foreach( x => {
        solutions.remove(x - elem)
        elem += 1
      })
    }

    solutions
  }
}
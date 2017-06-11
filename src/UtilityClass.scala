import java.util.Random

import scala.collection.mutable.ListBuffer
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
  def perturbationIterated(solution: Array[Int], number: Int, r: scala.util.Random) {
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
  def writeSolution(filename: String, bestSolution: Array[Int]) {
    val file = new FileClass("../resources/solutions/"+filename)
    var line = "";
    for (i <- 0 until bestSolution.length) {
      line += bestSolution(i) + " "
    }
    file.writeLine(line, true)
  }

  /**
    * Function which writes best solution into the solution file
    *
    * @param filename
    * @param solution
    * @param evals
    */
  def writeSolutionAndScores(filename: String, solution: Array[Int],  evals : Array[(Array[Int]) => Double]) {
    val file = new FileClass("../resources/solutions/"+filename)
    var line = ""

    for (i <- 0 until solution.length) {
      line += solution(i) + " "
    }

    (0 until evals.length).foreach( i => {
      line += ","
      line += evals(i)(solution)
    })

    file.writeLine(line, true)
  }

  /**
    * Function which sets header of tracks file
    *
    * @param filename
    */
  def writeHeaderTracking(filename: String, nbEvalsFunction: Int): Unit ={
    val file = new FileClass("../resources/solutions/"+filename+".tracking")
    var line = "I,D,ND,HVL,HV,HVDiff"
    (0 until nbEvalsFunction).foreach(i => {
      line += ",x" + i + "_avg,x" + i + "_median"
    })
    if(!file.fileExist())
      file.writeLine(line, false)
  }

  /**
    * Function which writes number evaluation and result
    *
    * @param filename
    * @param iteration
    * @param D
    * @param ND
    * @param HVL
    * @param HV
    * @param HVDiff
    * @param avgValues
    * @param medianeValues
    */
  def writeTrackingLine(filename: String, iteration: Int, D: Double, ND: Double, HVL: Double, HV: Double, HVDiff: Double, avgValues: Array[Double], medianeValues: Array[Double]) {

    val file = new FileClass("../resources/solutions/"+filename+".tracking")
    var line = f"$iteration, $D, $ND, $HVL, $HV, $HVDiff"

    (0 until avgValues.length).foreach(i => {
      line += "," + avgValues(i) + "," + medianeValues(i)
    })
    file.writeLine(line, true)
  }

  /**
    * Function which sets header of solutions file
    *
    * @param filename
    * @param line
    */
  def writeHeader(filename: String, line: String): Unit ={
    val file = new FileClass("../resources/solutions/"+filename)
    println(line)
    file.writeLine(line, false)
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
    println("Evaluation saved into scores " + filename)
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

  /**
    * Method used for normalized all values of criteria matrice
    *
    * @param arr matrice values from criteria
    * @return arr values normalized
    */
  def normalizeData(arr: Array[Array[Double]]) : Array[Array[Double]] = {

    var arrOut = arr;
    var maxValue = (for(x <- arr) yield x.max).max
    var minValue = (for(x <- arr) yield x.min).min

    (0 until arr.length).foreach(x => {
      (0 until arr(x).length).foreach(y =>{
        arr(x)(y) = (arr(x)(y) - minValue) / (maxValue - minValue)
      })
    })

    arr
  }


  /**
    *
    * Method used for make track about algorithm performance
    *
    * @param filename
    * @param iteration
    * @param currentSolution
    * @param arr solutions of the space search
    * @param evals
    */
  def algorithmEvaluationTrack(filename: String, iteration: Int, currentSolution: Array[Int], arr: ListBuffer[Array[Int]], evals : Array[(Array[Int]) => Double]) {

    //Erase the current solution which is already present
    var nbDominated = 0
    var nbNonDominated = 0

    var solutionsCoords = Array.ofDim[Double](arr.length, evals.length)
    var currentSolScore = Array.ofDim[Double](evals.length)

    var hyperVolumeLocal = 1.0
    var hyperVolumeCurrentSol = 1.0
    var hyperVolumeDiff = 0.0
    var averageValues = Array.ofDim[Double](evals.length)
    var medianeValues = Array.ofDim[Double](evals.length)

    //Use to compute only once the solutions scores
    (0 until evals.length).foreach(func_index => {
      currentSolScore(func_index) = evals(func_index)(currentSolution)
      hyperVolumeCurrentSol *= currentSolScore(func_index)
    })

    (0 until arr.length).foreach(sol_index => {

      var nbFuncDominated = 0

      (0 until evals.length).foreach(func_index => {

        //Add score to compute hypervolume
        solutionsCoords(sol_index)(func_index) = evals(func_index)(arr(sol_index))

        if(solutionsCoords(sol_index)(func_index) > currentSolScore(func_index))
          nbFuncDominated += 1
      })

      if(nbFuncDominated == evals.length)
        nbDominated += 1
      else
        nbNonDominated += 1
    })

    //Order solutions scores by first criteria (x axys) to compute hypervolume more easily
    solutionsCoords = solutionsCoords.sortBy(_(1)).reverse

    //Compute average values and mediane values
    (0 until evals.length).foreach(i => {

      val column = solutionsCoords.map(_(i))

      averageValues(i) = column.sum / solutionsCoords.length
      medianeValues(i) = column(solutionsCoords.length/2)
    })

    //Variable used to keep in memory the total volume of previous solutions
    var previousFirstCoord = 0.0

    (0 until solutionsCoords.length).foreach(i => {
       var volumeCurrentSol = 1.0
       var volumePreviousSols = 1.0

      (0 until solutionsCoords(i).length).foreach(axys_index => {
        volumeCurrentSol *= solutionsCoords(i)(axys_index)

        if(axys_index != 0){
          volumePreviousSols *= solutionsCoords(i)(axys_index)
        }
        else{
          volumePreviousSols *= previousFirstCoord
          previousFirstCoord = solutionsCoords(i)(axys_index)
        }
      })

      hyperVolumeLocal += volumeCurrentSol - volumePreviousSols
    })

    //Gettting hypervolume difference
    hyperVolumeDiff = hyperVolumeLocal - hyperVolumeCurrentSol

    val nbDominatedPercent = nbDominated * 100.0 / solutionsCoords.length;
    val nbNonDominatedPercent = nbNonDominated * 100.0 / solutionsCoords.length;
    writeTrackingLine(filename, iteration, nbDominatedPercent, nbNonDominatedPercent, hyperVolumeLocal, hyperVolumeCurrentSol, hyperVolumeDiff, averageValues, medianeValues)
  }
}
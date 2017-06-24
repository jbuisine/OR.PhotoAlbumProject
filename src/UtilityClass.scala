import java.util
import java.util.Random

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks
import scala.util.{Failure, Success, Try}

/**
 * @author j.buisine
 *
 */
object UtilityClass {
  
   val scanner = new java.util.Scanner(System.in)
  
   /**
   * Function which gets integer value until is not correct
     *
   * @param q : question which will be displayed to user
   * @param failure type
   * @param min : criteria of minimum value asked (included)
   * @param max : criteria of maximum value asked (excluded)
   * @return
   */
  def getScannerValue(q: String, failure: String, min: Int, max: Int): Int = {
    var output = ""
    var choice = false
    do {

      println(q)
      output = scanner.nextLine()
      Try(output.toInt) match {
        case Success(num) =>
          if (num <= min || num > max)
            println("Number written is not excepted.")
          else
            choice = true
        case Failure(_) =>
          println("Error, please select another " + failure + ".")
      }
    } while (!choice)
    output.toInt
  }

  /**
   * Function which show evolution percentage of algorithm on console
    *
   * @param previousText : previous input text value
   * @param text : text displayed
   */
  def showEvolution(previousText: Int, text: String): Unit = {
    var lengthContent = previousText
    if (lengthContent < text.length()) {
      lengthContent += text.length() - previousText

    }
    for (_ <- 0 until lengthContent) {
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
      val tmpValue = randomArray(i)
      randomArray(i) = randomArray(randomValue)
      randomArray(randomValue) = tmpValue
    }
    randomArray
  }

  /**
   * Function which permutes photos of a solution
   *
   * @param solution : current solution
   * @param number : number of mutations (user choice)
   * @param r : random object
   */
  def perturbationIterated(solution: Array[Int], number: Int, r: scala.util.Random) {
    val nbMutations = r.nextInt(number) + 1
    for (_ <- 0 until nbMutations) {
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
   * @param filename : file name based on user choice
   * @param bestSolution : best solution found at the end
   */
  def writeSolution(filename: String, bestSolution: Array[Int]) {
    val file = new FileClass("../resources/solutions/"+filename)
    var line = ""
    for (i <- bestSolution.indices) {
      line += bestSolution(i) + " "
    }
    file.writeLine(line, b = true)
  }

  /**
    * Function which writes best solution into the solution file
    *
    * @param filename : file name where is stored information data
    * @param solution : current solution treated
    * @param evals : Array of criteria function to optimize
    */
  def writeSolutionAndScores(filename: String, solution: Array[Int],  evals : Array[(Array[Int]) => Double]) {
    val file = new FileClass("../resources/solutions/"+filename)
    var line = ""

    for (i <- solution.indices) {
      line += solution(i) + " "
    }

    evals.indices.foreach(i => {
      line += ","
      line += evals(i)(solution)
    })

    file.writeLine(line, b = true)
  }

  /**
    * Function which sets header of tracking file
    *
    * @param filename : file name of tricking data file when header will be added
    */
  def writeHeaderTracking(filename: String, nbEvalsFunction: Int): Unit ={
    val file = new FileClass("../resources/solutions/"+filename+".tracking")
    var line = "I,D,ND,HVL,HV,HVDiff"
    (0 until nbEvalsFunction).foreach(i => {
      line += ",x" + i + "_avg,x" + i + "_median"
    })
    if(!file.fileExist())
      file.writeLine(line, b = false)
  }

  /**
    * Function which writes number evaluation and result
    *
    * @param filename : file name where the data will be stored
    * @param iteration : the current iteration indication
    * @param D : number of dominated solutions (siblings solutions)
    * @param ND : number of non dominated solutions (siblings solutions)
    * @param HVL : hyper volume local (all siblings solutions)
    * @param HV : hyper volume of the current solution
    * @param HVDiff : Hyper volume difference
    * @param avgValues : all average values for a specific criteria
    * @param medianValues : all median values for a specific criteria
    */
  def writeTrackingLine(filename: String, iteration: Int, D: Double, ND: Double, HVL: Double, HV: Double, HVDiff: Double, avgValues: Array[Double], medianValues: Array[Double]) {

    val file = new FileClass("../resources/solutions/"+filename+".tracking")
    var line = f"$iteration, $D, $ND, $HVL, $HV, $HVDiff"

    avgValues.indices.foreach(i => {
      line += "," + avgValues(i) + "," + medianValues(i)
    })
    file.writeLine(line, b= true)
  }

  /**
    * Function which sets header of solutions file
    *
    * @param filename : name of file
    * @param line : line to add into file
    */
  def writeHeader(filename: String, line: String): Unit ={
    val file = new FileClass("../resources/solutions/"+filename)
    println(line)
    file.writeLine(line, b = false)
  }


  /**
   * Function which writes number evaluation and result
   *
   * @param filename : filename to store data
   * @param nbEval : number of evaluation
   * @param result : result retrieved
   * @param solution : current solution
   */
  def writeEvaluation(filename: String, nbEval: Int, result: Double, solution: Array[Int]) {

    val file = new FileClass("../resources/scores/" + filename)
    var line = nbEval + "," + result + ","

    for (i <- solution.indices) {
      line += solution(i) + " "
    }

    file.writeLine(line, b = true)
    println("Evaluation saved into scores " + filename)
  }

  /**
    * Function which writes number evaluation and result
    *
    * @param filename : filename to store current solution scores
    * @param evals : evaluation functions
    * @param solution : current solution
    */
  def writePLSScores(filename: String, evals : Array[(Array[Int]) => Double], solution: Array[Int]) {

    val file = new FileClass("../resources/scores/" + filename)

    var line = ""
    evals.indices.foreach(index => {
      line += evals(index)(solution) + ","
    })

    for (i <- solution.indices) {
      line += solution(i) + " "
    }

    file.writeLine(line, b = true)
  }

  /**
    * Function used to check if solutions can be removed because it is a non dominated solution
    * @param arr : current solution
    * @param evals : evalution functions
    * @return non dominated solutions
    */
  def getNonDominatedSolutions(arr: ListBuffer[Array[Int]], evals : Array[(Array[Int]) => Double]) : ListBuffer[Array[Int]] = {
    val solutions = arr
    var elements = new ListBuffer[Int]

    arr.indices.foreach(sol_index => {

      arr.indices.foreach(current_sol_index => {
        var numberDominatedFunction = 0
        evals.indices.foreach(index => {
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
    * @param arr : matrice values from criteria
    * @return arr : values normalized
    */
  def normalizeData(arr: Array[Array[Double]]) : Array[Array[Double]] = {

    val maxValue = (for(x <- arr) yield x.max).max
    val minValue = (for (x <- arr) yield x.min).min

    arr.indices.foreach(x => {
      arr(x).indices.foreach(y =>{
        arr(x)(y) = (arr(x)(y) - minValue) / (maxValue - minValue)
      })
    })

    arr
  }


  /**
    *
    * Method used for make track about algorithm performance
    *
    * @param filename : filename to store tracking line
    * @param iteration : number of iteration retrieved
    * @param currentSolution : the current solution
    * @param evals : evaluation functions
    */
  def algorithmEvaluationTrack(filename: String, iteration: Int, currentSolution: Array[Int], numberElement: Int, evals : Array[(Array[Int]) => Double]) {

    //Erase the current solution which is already present
    var nbDominated = 0
    var nbNonDominated = 0

    val neighbors = getNeighbors(arr = currentSolution)
    var solutionsCoords = Array.ofDim[Double](neighbors.length, evals.length)
    val currentSolScore = Array.ofDim[Double](evals.length)

    var hyperVolumeLocal = 1.0
    var hyperVolumeCurrentSol = 1.0
    var hyperVolumeDiff = 0.0
    val averageValues = Array.ofDim[Double](evals.length)
    val medianValues = Array.ofDim[Double](evals.length)

    //Use to compute only once the solutions scores
    evals.indices.foreach(func_index => {
      currentSolScore(func_index) = evals(func_index)(currentSolution)
      hyperVolumeCurrentSol *= currentSolScore(func_index)
    })

    neighbors.indices.foreach(sol_index => {

      var nbFuncDominated = 0

      evals.indices.foreach(func_index => {

        //Add score to compute hyper volume
        solutionsCoords(sol_index)(func_index) = evals(func_index)(neighbors(sol_index))

        if(solutionsCoords(sol_index)(func_index) > currentSolScore(func_index))
          nbFuncDominated += 1
      })

      if(nbFuncDominated == evals.length)
        nbDominated += 1
      else
        nbNonDominated += 1
    })

    //Order solutions scores by first criteria (x axys) to compute hyper volume more easily
    solutionsCoords = solutionsCoords.sortBy(_(1)).reverse

    //Compute average values and median values
    evals.indices.foreach(i => {

      val column = solutionsCoords.map(_(i))

      averageValues(i) = column.sum / solutionsCoords.length

      medianValues(i) = column(neighbors.length/2)
    })

    //Variable used to keep in memory the total volume of previous solutions
    var previousFirstCoord = 0.0

    solutionsCoords.indices.foreach(i => {
       var volumeCurrentSol = 1.0
       var volumePreviousSols = 1.0

      solutionsCoords(i).indices.foreach(axys_index => {
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

    //Getting hyper volume difference
    hyperVolumeDiff = hyperVolumeLocal - hyperVolumeCurrentSol

    writeTrackingLine(filename, iteration, nbDominated, nbNonDominated, hyperVolumeLocal, hyperVolumeCurrentSol, hyperVolumeDiff, averageValues, medianValues)
  }

  /**
    * Method which is used for retrieve all neighbors of solution
    * @param arr : current solution
    * @return
    */
  def getNeighbors(arr: Array[Int]): ListBuffer[Array[Int]] = {

    var neighbors = new ListBuffer[Array[Int]]()

    // Adding current solution to escape same values array to be added
    neighbors += arr

    println("Array size  ", arr.length)

    arr.indices.foreach( i => {
      arr.indices.foreach(j => {
        var newest = arr.clone()

        val oldValue = newest(i)
        newest(i) = newest(j)
        newest(j) = oldValue

        if (!checkExists(neighbors, newest)) neighbors += newest
      })
    })

    println("Before number of neighbors ", neighbors.length)

    // Deleting current solution
    neighbors -= arr
    neighbors
  }

  /**
    * Method created to check if array is already added into neighbors list
    * @param solutions : all solutions
    * @param arr : current solution
    * @return
    */
  def checkExists(solutions: ListBuffer[Array[Int]], arr: Array[Int]): Boolean = {
    var check: Boolean = false
    val inner = new Breaks

    inner.breakable {
      for (a <- solutions) {
        if (util.Arrays.equals(a, arr)) {
          check = true
          inner.break
        }
      }
    }
    check
  }

  /**
    * Method which return the factorial of number
    *
    * @param n : number
    * @return
    */
  def factorial(n: BigInt): BigInt = {
    if(n == 0)
       1
    else
      n * factorial(n-1)
  }
}
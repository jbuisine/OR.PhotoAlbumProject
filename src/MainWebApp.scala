import scala.util.control.Breaks
import org.json.simple.{JSONArray, JSONObject}
import org.json.simple.parser.JSONParser

import scala.collection.mutable.ListBuffer

/**
 * @author j.buisine
 *
 */
object MainWebApp {

  val df = new java.text.DecimalFormat("0.##")
  val pathPhoto = "./../www/views/templates/{template}/info-photo.json"
  var pathAlbum = "./../resources/data/"
  val scanner = new java.util.Scanner(System.in)

  // Choices variables

  var functionChoice: Int = _
  var algorithmChoice: Int = _

  var generationTypeChoice: Int = _
  var evaluationFile = ""
  var criteriaChoices: Array[String] = _
  var solutionFile = ""

  val numberFunction = 9

  //Objective function
  var functionsList: Array[(Array[Int]) => Double] = _

  var hashChoice: Int = 0
  var filePath = ""

  val breaker = new Breaks

  /**
   * Main method which throws all algorithms
   * @param args : args passed when program is executed
   */
  def main(args: Array[String]): Unit = {

    criteriaChoices = Array("ahashdist", "phashdist", "dhashdist", "colors", "greyAVG", "commonTags", "uncommonTags", "nbUncommonTags")
    functionsList = Array(Modelisation.ahashEval, Modelisation.phashEval, Modelisation.dhashEval, Modelisation.colorsEval, Modelisation.greyAVGEval, Modelisation.commonTagEval, Modelisation.uncommonTagEval, Modelisation.nbUncommonTagEval)

    val parser = new JSONParser()
    val data =  parser.parse(args(0).toString).asInstanceOf[JSONObject]
    val templateName = data.get("templateName").toString
    val directory = data.get("albumType").toString.split('.')(0)
    val templateSize = data.get("templateSize").toString.toInt
    val criteriaIndexes = data.get("criteria").asInstanceOf[JSONArray].toArray.map( x => x.toString.toInt)
    val algorithmChoice = data.get("algorithm").toString.toInt
    val algorithmIteration = data.get("iterationAlgorithm").toString.toInt
    val iterationHC = data.get("iterationHC").toString.toInt
    val numberPermutation = data.get("numberPermutation").toString.toInt

    solutionFile = data.get("solutionFile").toString

    //Create path of album type
    pathAlbum += templateName + "/"
    pathAlbum += data.get("albumType").toString

    val pathPhotoTemplate = pathPhoto.replace("{template}", templateName)


    filePath = templateName + "/" + directory + "/" + solutionFile

    //Initialization of context
    Modelisation.init(pathPhotoTemplate, pathAlbum)

    var solutions = new ListBuffer[Array[Int]]

    //Get function choices
    val functions: Array[(Array[Int]) => Double] = functionsList.zipWithIndex.filter{ case (_, index) => criteriaIndexes.contains(index) }.map(_._1)

    functions.length match {
      // One criteria
      case 1 =>
        algorithmChoice match {

          //HC
          case 0 =>
            solutions += Algorithms.HillClimberFirstImprovement(templateSize, algorithmIteration, null, functions(0))


          //ILS
          case 1 =>
            solutions += Algorithms.IteratedLocalSearch(templateSize, algorithmIteration, iterationHC, numberPermutation, functions(0))

          //EA
          case 2 =>
            //Values for evolution strategy algorithm
            val HCGenitor = data.get("HCGenitor").toString.toInt
            val muElement = data.get("muElement").toString.toInt
            val lambdaElement = data.get("lambdaElement").toString.toInt
            solutions += Algorithms.GeneticEvolutionnaryAlgorithm(templateSize, muElement, lambdaElement, algorithmIteration, iterationHC, HCGenitor, numberPermutation, functions(0))

          // Random walk
          case 6 =>
            solutions = Algorithms.RandomWalkAlgorithm(filePath, templateSize, algorithmIteration, functions)


      }

      // Two criteria
      case 2 =>
        algorithmChoice match {

          //PLS
          case 3 =>
            solutions = Algorithms.ParetoLocalSearch(filePath, templateSize, algorithmIteration, null, functions)

          //MOEA/D
          case 4 =>
            //MOEA/D values
            val numberVectors = data.get("numberVectors").toString.toInt
            val closestVectors = data.get("closestVectors").toString.toInt
            val computedChoice = data.get("computedChoice").toString.toInt
            solutions = Algorithms.MOEAD_Algorithm(filePath, templateSize, algorithmIteration, numberVectors, closestVectors, functions, computedChoice)

          case 5 =>
            //MOEA/D values
            val numberVectors = data.get("numberVectors").toString.toInt
            val closestVectors = data.get("closestVectors").toString.toInt
            val computedChoice = data.get("computedChoice").toString.toInt

            solutions = Algorithms.TPLS_Algorithm(filePath, templateSize, algorithmIteration, numberVectors, closestVectors, functions, computedChoice)

          // Random walk
          case 6 =>
            solutions = Algorithms.RandomWalkAlgorithm(filePath, templateSize, algorithmIteration, functions)
        }
      //Three criteria
      case 3 =>
        algorithmChoice match {

          //PLS
          case 3 =>
            solutions = Algorithms.ParetoLocalSearch(filePath, templateSize, algorithmIteration, null, functions)

          // Random walk
          case 6 =>
            solutions = Algorithms.RandomWalkAlgorithm(filePath, templateSize, algorithmIteration, functions)
        }
    }

    //Writing solutions file
    var headerLine = ""

    criteriaIndexes.foreach( criteria => headerLine += criteriaChoices(criteria) + ",")
    headerLine = headerLine.substring(0, headerLine.length()-1)

    UtilityClass.writeHeader(filePath, headerLine)
    solutions.foreach( sol => UtilityClass.writeSolutionAndScores(filePath, sol, functions))

  }
}

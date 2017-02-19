import scala.util.control.Breaks
import org.json.simple.{JSONArray, JSONObject}
import org.json.simple.parser.{JSONParser}

import scala.collection.mutable.ListBuffer

/**
 * @author j.buisine
 *
 */
object MainWebApp {

  val df = new java.text.DecimalFormat("0.##")
  val pathPhoto = "./../resources/data/info-photo.json"
  var pathAlbum = "./../resources/data/albums-type/"
  val scanner = new java.util.Scanner(System.in)

  // Choices variables

  var functionChoice: Int = _
  var algorithmChoice: Int = _

  var generationTypeChoice: Int = _
  var evaluationFile = ""
  var criteriaChoices: Array[String] = _
  var solutionFile = ""

  val numberFunction = 9
  val numberMonoObjectiveAlgo = 3
  val numberTwoObjectiveAlgo = 2
  val numberMultiObjectiveAlgo = 1

  //Objective function
  var functionsList: Array[(Array[Int]) => Double] = null

  val hashTypes = Array("ahashdist", "phashdist", "dhashdist")
  var hashChoice: Int = 0

  //Sanner utility object
  val breaker = new Breaks

  /**
   * Main method which throws all algorithms
   * @param args
   */
  def main(args: Array[String]): Unit = {

    functionsList = Array(Modelisation.hashEval, Modelisation.hashEval, Modelisation.hashEval, Modelisation.colorsEval, Modelisation.greyAVGEval, Modelisation.commonTagEval, Modelisation.uncommonTagEval, Modelisation.nbUncommonTagEval)

    val parser = new JSONParser()
    val data =  parser.parse(args(0).toString).asInstanceOf[JSONObject]

    solutionFile = data.get("solutionFile").toString
    pathAlbum += data.get("albumType").toString
    val directory = data.get("albumType").toString.split('.')(0)
    val templateSize = data.get("templateSize").toString.toInt
    val templateName = data.get("templateName").toString
    var criteriaIndexes:Array[Int] = data.get("criteria").asInstanceOf[JSONArray].toArray.map( x => x.toString.toInt)
    val algorithmChoice = data.get("algorithm").toString.toInt
    val algorithmIteration = data.get("iterationAlgorithm").toString.toInt
    val iterationHC = data.get("iterationHC").toString.toInt
    val numberPermutation = data.get("numberPermutation").toString.toInt

    val hashValues = criteriaIndexes.filter(_ < 3)

    if(hashValues.length > 0)
      hashValues.foreach( value => { Modelisation.init(pathPhoto, pathAlbum, hashTypes(value)) })
    else
      Modelisation.init(pathPhoto, pathAlbum, "")

    var solutions = new ListBuffer[Array[Int]]

    //Get function choices
    var functions: Array[(Array[Int]) => Double] = functionsList.zipWithIndex.filter{ case (f, index) => criteriaIndexes.contains(index) }.map(_._1)

    println(functions.length)
    functions.length match {
      // One criteria
      case 1 => {

        algorithmChoice match {

          //HC
          case 0 => {
            solutions += Algorithms.HillClimberFirstImprovement(templateSize, algorithmIteration, null, functions(0))
          }

          //ILS
          case 1 => {
            solutions += Algorithms.IteratedLocalSearch(templateSize, algorithmIteration, iterationHC, numberPermutation, functions(0))
          }

          //EA
          case 2 => {
            //Values for evolution strategy algorithm
            val HCGenitor = data.get("HCGenitor").toString.toInt
            val muElement = data.get("muElement").toString.toInt
            val lambdaElement = data.get("lambdaElement").toString.toInt
            solutions += Algorithms.GeneticEvolutionnaryAlgorithm(muElement, lambdaElement, algorithmIteration, iterationHC, HCGenitor, numberPermutation, functions(0))
          }
        }
      }

      // Two criteria
      case 2 => {

        algorithmChoice match {

          //PLS
          case 3 => {
            solutions = Algorithms.ParetoLocalSearch(algorithmIteration, null, functions)
          }

          //MOEA/D
          case 4 => {
            //MOEA/D values
            val numberVectors = data.get("numberVectors").toString.toInt
            val closestVectors = data.get("closestVectors").toString.toInt
            val computedChoice = data.get("computedChoice").toString.toInt
            solutions = Algorithms.MOEAD_Algorithm(algorithmIteration, numberVectors, closestVectors, functions, computedChoice)
          }
        }
      }

      //Three criteria
      case 3 => {
        solutions = Algorithms.ParetoLocalSearch(algorithmIteration, null, functions)
      }
    }

    solutions.foreach( sol => UtilityClass.writeSolution(templateName + "/" + directory + "/" + solutionFile, sol))

  }
}

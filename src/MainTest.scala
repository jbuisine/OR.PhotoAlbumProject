

import java.nio.file.Paths

import scala.util.control.Breaks

/**
 * @author j.buisine
 *
 */
object MainTest {

  val df = new java.text.DecimalFormat("0.##")
  val pathPhoto = "./resources/data/info-photo.json"
  val pathAlbum = "./resources/data/albums-type/album-6-2per3.json"
  val nbPhotos = 55
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
  var f: Array[(Array[Int]) => Double] = null

  val hashTypes = Array("ahashdist", "phashdist", "dhashdist")
  var hashChoice: Int = 0

  //Sanner utility object
  val breaker = new Breaks

  /**
   * Main method which throws all algorithms
   * @param args
   */
  def main(args: Array[String]): Unit = {

    //File solution value
    solutionFile = args(0)

    val criteriaChoice = args(1).toArray
    val choiceAlgo = args(2).toInt

    println(criteriaChoice)
    /*
    if(criteriaChoice >= 0 && criteriaChoice <= 2)
      hashChoice = criteriaChoice+1

    */
    println(hashChoice)
    f = Array(Modelisation.hashEval, Modelisation.hashEval, Modelisation.hashEval, Modelisation.colorsEval, Modelisation.greyAVGEval, Modelisation.commonTagEval, Modelisation.uncommonTagEval, Modelisation.nbUncommonTagEval)

    val hashValue = if (hashChoice > 0) hashTypes(hashChoice - 1) else ""

    Modelisation.init(pathPhoto, pathAlbum, hashValue)

    var solution = new Array[Int](Main.nbPhotos)
    choiceAlgo.toInt match {
      case 0 => {
        solution = Algorithms.IteratedLocalSearch(1000, 15000, 20, f(0))
        println(f(0)(solution))
      }
    }

    if(solutionFile.length() > 0)
      UtilityClass.writeSolution(solutionFile, solution)
  }
}

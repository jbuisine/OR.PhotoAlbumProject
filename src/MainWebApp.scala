

import java.io.FileReader
import java.nio.file.Paths

import scala.util.control.Breaks
import org.json.simple.{JSONArray, JSONObject}
import org.json.simple.parser.{JSONParser, ParseException}

/**
 * @author j.buisine
 *
 */
object MainWebApp {

  val df = new java.text.DecimalFormat("0.##")
  val pathPhoto = "./../resources/data/info-photo.json"
  val pathAlbum = "./../resources/data/templates-type/album-6-2per3.json"
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

    f = Array(Modelisation.hashEval, Modelisation.hashEval, Modelisation.hashEval, Modelisation.colorsEval, Modelisation.greyAVGEval, Modelisation.commonTagEval, Modelisation.uncommonTagEval, Modelisation.nbUncommonTagEval)

    val parser = new JSONParser()
    val data =  parser.parse(args(0).toString).asInstanceOf[JSONObject]

    solutionFile = data.get("solutionFile").toString
    val directory = data.get("templateType").toString.split('.')(0)
    var criteriasIndexes = data.get("criterias").asInstanceOf[Array[Int]]

    println(criteriasIndexes)
    val hashValues = criteriasIndexes.filter(_ < 3)

    if(hashValues.length > 0)
      hashValues.foreach( value => { Modelisation.init(pathPhoto, pathAlbum, hashTypes(value)) })
    else
      Modelisation.init(pathPhoto, pathAlbum, "")

    var solution = new Array[Int](Main.nbPhotos)

    criteriasIndexes.length match {
      case 1 => {

      }

      case 2 => {

      }

      case 3 => {

      }
    }

    solution = Algorithms.IteratedLocalSearch(1000, 15000, 20, f(3))

    if(solutionFile.length() > 0)
      UtilityClass.writeSolution(directory + "/" + solutionFile, solution)
  }
}

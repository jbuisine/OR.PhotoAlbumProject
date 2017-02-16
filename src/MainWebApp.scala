

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
  val pathAlbum = "./../resources/data/albums-type/album-6-2per3.json"
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

    println(args(0))
    val data =  parser.parse(args(0).toString).asInstanceOf[JSONObject]

    solutionFile = data.get("solutionFile").toString
    val directory = data.get("albumType").toString.split('.')(0)


    val hashValue = if (hashChoice > 0) hashTypes(hashChoice - 1) else ""

    Modelisation.init(pathPhoto, pathAlbum, hashValue)

    var solution = new Array[Int](Main.nbPhotos)

    solution = Algorithms.IteratedLocalSearch(1000, 15000, 20, f(3))


    if(solutionFile.length() > 0)
      UtilityClass.writeSolution(directory + "/" + solutionFile, solution)
  }
}

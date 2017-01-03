
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.text.DecimalFormat
import java.util.Random
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import scala.collection.JavaConversions._

/**
 * @author j.buisine
 *
 */
object Modelisation {

  // Different hash distances between photos
  private var photoDist: Array[Array[Double]] = _
  private var albumInvDist: Array[Array[Double]] = _

  // Tags values distances calculates between photos
  private var photoDistancesCommonsTags: Array[Array[Double]] = _
  private var photoDistancesUncommonsTags: Array[Array[Double]] = _
  private var photoDistancesUncommonsNbTags: Array[Array[Double]] = _

  // Colors values distances calculates between photos
  private var photoColor1: Array[Array[Int]] = _
  private var photoColor2: Array[Array[Int]] = _
  private var photoDistancesColors: Array[Array[Double]] = _

  // Grey AVG values distances calculates between photos
  private var photoGreyAVG: Array[Int] = _
  private var photoDistancesGreyAVG: Array[Array[Double]] = _

  private var df: DecimalFormat = new java.text.DecimalFormat("0.##")
  
  //Public variable used for know how album is generated
  // - Value true :  Based on page cohesion
  // - Value false : Or just based on logical sequence of photo 
  var formatAlbum = true;

  /**
   * Function used for init hash objective functions
   * @param pathPhoto
   * @param pathAlbum
   * @param attribute
   */
  def initHash(pathPhoto: String, pathAlbum: String, attribute: String) {
    computeDistances(pathPhoto, pathAlbum, attribute)
    computePhotoTags(pathPhoto)
  }

  /**
   * Function used for init tags arrays
   * @param pathPhoto
   */
  def initTags(pathPhoto: String, albumFileName: String) {
    computeAlbumDistances(albumFileName)
    computePhotoTags(pathPhoto)
  }

  /**
   * Function used for init colors arrays
   * @param pathPhoto
   */
  def initColors(pathPhoto: String) {
    computePhotoColors(pathPhoto)
  }

  /**
   * Function used for init grey avg colors array
   * @param pathPhoto
   */
  def initGreyAvg(pathPhoto: String) {
    computePhotoGreyAVG(pathPhoto)
  }

  /**
   * Compute the matrice of distance between solutions and of inverse distance
   * between positions
   */
  private def computeDistances(photoFileName: String, albumFileName: String, attribute: String) {
    computePhotoDistances(photoFileName, attribute)
    computeAlbumDistances(albumFileName)
  }

  /**
   * Function which load and compute distances
   * @param fileName
   */
  private def computeAlbumDistances(fileName: String) {
    try {
      val reader = new FileReader(fileName)
      val parser = new JSONParser()
      val obj = parser.parse(reader)
      val album = obj.asInstanceOf[JSONObject]
      val nPage = album.get("page")
      val pageSize = album.get("pagesize").asInstanceOf[JSONArray]
      val size = pageSize.get(0).toString().toInt
      var nbPhoto = 0
      for (i <- 0 until pageSize.size) nbPhoto += pageSize.get(i).toString().toInt
      albumInvDist = Array.ofDim[Double](nbPhoto, nbPhoto)
      for (i <- 0 until nbPhoto; j <- 0 until nbPhoto)
        albumInvDist(i)(j) = inverseDistance(size, i, j)
    } catch {
      case pe: ParseException => {
        println("position: " + pe.getPosition)
        println(pe)
      }
      case ex: FileNotFoundException => ex.printStackTrace()
      case ex: IOException => ex.printStackTrace()
    }
  }

  /**
   * Method used for inverse distance of photo
   * @param size
   * @param i
   * @param j
   * @return
   */
  private def inverseDistance(size: Int, i: Int, j: Int): Double = {
    val pagei = i / size
    val pagej = j / size
    if (pagei != pagej) 0 else {
      val posi = i % size
      val posj = j % size
      val xi = posi % 2
      val yi = posi / 2
      val xj = posj % 2
      val yj = posj / 2
      1.toDouble / (Math.abs(xi - xj) + Math.abs(yi - yj)).toDouble
    }
  }

  /**
   * Method which loads the distance between photos
   * @param fileName
   * @param attribute
   */
  private def computePhotoDistances(fileName: String, attribute: String) {
    try {
      val reader = new FileReader(fileName)
      val parser = new JSONParser()
      val obj = parser.parse(reader)
      val array = obj.asInstanceOf[JSONArray]
      photoDist = Array.ofDim[Double](array.size, array.size)
      for (i <- 0 until array.size) {
        val image = array.get(i).asInstanceOf[JSONObject]
        val d = image.get(attribute).asInstanceOf[JSONArray]
        for (j <- 0 until d.size) {
          photoDist(i)(j) = d.get(j).toString().toDouble
        }
      }
    } catch {
      case pe: ParseException => {
        println("position: " + pe.getPosition)
        println(pe)
      }
      case ex: FileNotFoundException => ex.printStackTrace()
      case ex: IOException => ex.printStackTrace()
    }
  }

  /**
   * Method which loads tags values
   * @param fileName
   */
  private def computePhotoTags(fileName: String) {
    try {
      val reader = new FileReader(fileName)
      val parser = new JSONParser()
      val obj = parser.parse(reader)
      val array = obj.asInstanceOf[JSONArray]
      val nbTags: Int = array.get(0).asInstanceOf[JSONObject].get("tags").asInstanceOf[JSONObject].get("classes").asInstanceOf[JSONArray].size
      var photoTagsValue = Array.ofDim[Double](array.size(), nbTags)
      var photoTagsName = Array.ofDim[String](array.size(), nbTags)
      photoDistancesCommonsTags = Array.ofDim[Double](array.size(), array.size())
      photoDistancesUncommonsTags = Array.ofDim[Double](array.size(), array.size())
      photoDistancesUncommonsNbTags = Array.ofDim[Double](array.size(), array.size())

      //Get information of tags
      for (i <- 0 until array.size) {
        val tags = array.get(i).asInstanceOf[JSONObject].get("tags").asInstanceOf[JSONObject].get("classes").asInstanceOf[JSONArray]
        val probs = array.get(i).asInstanceOf[JSONObject].get("tags").asInstanceOf[JSONObject].get("probs").asInstanceOf[JSONArray]
        for (j <- 0 until nbTags) {

          photoTagsName(i)(j) = tags.get(j).toString()
          photoTagsValue(i)(j) = probs.get(j).toString().toDouble
        }
      }

      for (i <- 0 until array.size) {

        //Get sum of all sames or differents tags
        //Make two differents array 
        // - One to minimize commons value
        // - The other will be used fo minimize difference between each photos
        for (j <- 0 until array.size) {

          var uncommonSum = 0.0
          var commonSum = 0.0
          var nbCommonTag = 0
          for (k <- 0 until nbTags; l <- 0 until nbTags) {
            
            if (photoTagsName(i)(l) != photoTagsName(j)(k))
              uncommonSum += math.abs(photoTagsValue(i)(l) - photoTagsValue(j)(k))
            else {
              commonSum += math.abs(photoTagsValue(i)(l) - photoTagsValue(j)(k))
              nbCommonTag += 1
            }
          }
          photoDistancesUncommonsTags(i)(j) = uncommonSum
          photoDistancesUncommonsNbTags(i)(j) = nbTags - nbCommonTag
          
          if (nbCommonTag > 0)
            photoDistancesCommonsTags(i)(j) = commonSum / nbCommonTag
        }
      }
    } catch {
      case pe: ParseException => {
        println("position: " + pe.getPosition)
        println(pe)
      }
      case ex: FileNotFoundException => ex.printStackTrace()
      case ex: IOException => ex.printStackTrace()
    }
  }

  /**
   * Method which loads colors values of photos
   * @param fileName
   */
  private def computePhotoColors(fileName: String) {
    try {
      val reader = new FileReader(fileName)
      val parser = new JSONParser()
      val obj = parser.parse(reader)
      val array = obj.asInstanceOf[JSONArray]
      photoColor1 = Array.ofDim[Int](array.size, 3)
      photoColor2 = Array.ofDim[Int](array.size, 3)
      for (i <- 0 until array.size) {
        val image = array.get(i).asInstanceOf[JSONObject]
        photoColor1(i)(0) = image.get("color1").asInstanceOf[JSONObject].get("r").toString().toInt
        photoColor1(i)(1) = image.get("color1").asInstanceOf[JSONObject].get("b").toString().toInt
        photoColor1(i)(2) = image.get("color1").asInstanceOf[JSONObject].get("g").toString().toInt

        photoColor2(i)(0) = image.get("color2").asInstanceOf[JSONObject].get("r").toString().toInt
        photoColor2(i)(1) = image.get("color2").asInstanceOf[JSONObject].get("b").toString().toInt
        photoColor2(i)(2) = image.get("color2").asInstanceOf[JSONObject].get("g").toString().toInt
      }
    } catch {
      case pe: ParseException => {
        println("position: " + pe.getPosition)
        println(pe)
      }
      case ex: FileNotFoundException => ex.printStackTrace()
      case ex: IOException => ex.printStackTrace()
    }
  }

  /**
   * Method which loads Grey AVG values
   * @param fileName
   */
  private def computePhotoGreyAVG(fileName: String) {
    try {
      val reader = new FileReader(fileName)
      val parser = new JSONParser()
      val obj = parser.parse(reader)
      val array = obj.asInstanceOf[JSONArray]
      photoGreyAVG = Array.ofDim[Int](array.size)
      for (i <- 0 until array.size) {
        val image = array.get(i).asInstanceOf[JSONObject]
        photoGreyAVG(i) = image.get("greyavg").toString().toInt
      }
    } catch {
      case pe: ParseException => {
        println("position: " + pe.getPosition)
        println(pe)
      }
      case ex: FileNotFoundException => ex.printStackTrace()
      case ex: IOException => ex.printStackTrace()
    }
  }

  
  def eval (arr: Array[Array[Double]], solution: Array[Int]): Double = {
    var sum: Double = 0
    if(Modelisation.formatAlbum){
      for (i <- 0 until albumInvDist.length; j <- i + 1 until albumInvDist.length) {
        sum += arr(solution(i))(solution(j)) * albumInvDist(i)(j)
      }
    }else {
      for (i <- 0 until albumInvDist.length; j <- i + 1 until albumInvDist.length) {
        sum += arr(solution(i))(solution(j))
      }
    }
    sum
  }
  
  /**
   *
   * Un exemple de fonction objectif (à minimiser): distance entre les photos
   * pondérées par l'inverse des distances spatiales sur l'album Modélisaiton
   * comme un problème d'assignement quadratique (QAP)
   *
   * Dans cette fonction objectif, pas de prise en compte d'un effet de page
   * (harmonie/cohérence de la page) par le choix de distance, pas
   * d'intéraction entre les photos sur des différentes pages
   *
   * Cette fonction se basera sur les différents tags fournis dans le fichier JSON soit :
   * - ahashdist : average hash
   * - phashdist : perspective hash
   * - dhashdist : difference hash
   *
   * Ce paramètre sera fournit lors de l'initialisation de la classe.
   *
   * @param solution
   * @return
   */
  def hashEval(solution: Array[Int]): Double = {
    eval(photoDist, solution)
  }

  /**
   * New objective function
   *
   * For each photos into a solution 
   *
   * After that returns the score of the solution
   *
   * @param solution
   * @return score
   */
  def commonsTagEval(solution: Array[Int]): Double = {
    eval(photoDistancesCommonsTags, solution)
  }

  /**
   * @param solution
   * @return
   */
  def uncommonsTagEval(solution: Array[Int]): Double = {
    eval(photoDistancesUncommonsTags, solution)
  }
  
  /**
   * @param solution
   * @return
   */
  def nbUnommonsTagEval(solution: Array[Int]): Double = {
    eval(photoDistancesUncommonsNbTags, solution)
  }

  /**
   * New objective function
   *
   * For each photos of solution :
   * Evaluates the difference between color 1 & 2 between photo n and n+1 and add distance to the sum
   *
   * Finally function gave the solution score
   *
   * @param solution
   * @return score
   */
  def colorsEval(solution: Array[Int]): Double = {

    var sum = 0.0;
    for (i <- 0 until photoColor1.length - 1) {
      val d1 = math.sqrt((photoColor1(solution(i))(0) - photoColor1(solution(i + 1))(0)) * (photoColor1(solution(i))(0) - photoColor1(solution(i + 1))(0))
        + (photoColor1(solution(i))(1) - photoColor1(solution(i + 1))(1)) * (photoColor1(solution(i))(1) - photoColor1(solution(i + 1))(1))
        + (photoColor1(solution(i))(2) - photoColor1(solution(i + 1))(2)) * (photoColor1(solution(i))(2) - photoColor1(solution(i + 1))(2)))

      val d2 = math.sqrt((photoColor2(solution(i))(0) - photoColor2(solution(i + 1))(0)) * (photoColor2(solution(i))(0) - photoColor2(solution(i + 1))(0))
        + (photoColor2(solution(i))(1) - photoColor2(solution(i + 1))(1)) * (photoColor2(solution(i))(1) - photoColor2(solution(i + 1))(1))
        + (photoColor2(solution(i))(2) - photoColor2(solution(i + 1))(2)) * (photoColor2(solution(i))(2) - photoColor2(solution(i + 1))(2)))

      sum += d1 + d2;
    }

    return sum;
  }

  /**
   * New objective function
   *
   * For each photos of solution :
   * Evaluates the difference between grey AVG between photo n and n+1
   *
   * Finally function gave the solution score
   *
   * @param solution
   * @return score
   */
  def greyAVGEval(solution: Array[Int]): Double = {

    var sum = 0
    for (i <- 0 until photoGreyAVG.length - 1) {
      sum += math.abs(photoGreyAVG(solution(i)) - photoGreyAVG(solution(i + 1)))
    }
    return sum
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

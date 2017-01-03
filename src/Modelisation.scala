
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
  private var photoDistancesColors: Array[Array[Double]] = _

  // Grey AVG values distances calculates between photos
  private var photoDistancesGreyAVG: Array[Array[Double]] = _

  private var df: DecimalFormat = new java.text.DecimalFormat("0.##")


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
  def initColors(pathPhoto: String, albumFileName: String) {
    computeAlbumDistances(albumFileName)
    computePhotoColors(pathPhoto)
  }

  /**
   * Function used for init grey avg colors array
   * @param pathPhoto
   */
  def initGreyAvg(pathPhoto: String, albumFileName: String) {
    computeAlbumDistances(albumFileName)
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

            if (photoTagsName(i)(l) != photoTagsName(j)(k)){
              uncommonSum += math.abs(photoTagsValue(i)(l) - photoTagsValue(j)(k))
              
              //Penality result cause of different tag
              commonSum += 0.2
            }
            else {
              //Increase result cause of same tag
              uncommonSum -= 0.2
              commonSum -= math.abs(photoTagsValue(i)(l) - photoTagsValue(j)(k))
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
      var photoColor1 = Array.ofDim[Int](array.size, 3)
      var photoColor2 = Array.ofDim[Int](array.size, 3)
      photoDistancesColors = Array.ofDim[Double](array.size, array.size)
      for (i <- 0 until array.size) {
        val image = array.get(i).asInstanceOf[JSONObject]
        photoColor1(i)(0) = image.get("color1").asInstanceOf[JSONObject].get("r").toString().toInt
        photoColor1(i)(1) = image.get("color1").asInstanceOf[JSONObject].get("b").toString().toInt
        photoColor1(i)(2) = image.get("color1").asInstanceOf[JSONObject].get("g").toString().toInt

        photoColor2(i)(0) = image.get("color2").asInstanceOf[JSONObject].get("r").toString().toInt
        photoColor2(i)(1) = image.get("color2").asInstanceOf[JSONObject].get("b").toString().toInt
        photoColor2(i)(2) = image.get("color2").asInstanceOf[JSONObject].get("g").toString().toInt
      }

      for (i <- 0 until array.size) {
        for (j <- 0 until array.size) {
            val d1 = math.sqrt((photoColor1(i)(0) - photoColor1(j)(0)) * (photoColor1(i)(0) - photoColor1(j)(0))
              + (photoColor1(i)(1) - photoColor1(j)(1)) * (photoColor1(i)(1) - photoColor1(j)(1))
              + (photoColor1(i)(2) - photoColor1(j)(2)) * (photoColor1(i)(2) - photoColor1(j)(2)))
  
            val d2 = math.sqrt((photoColor2(i)(0) - photoColor2(j)(0)) * (photoColor2(i)(0) - photoColor2(j)(0))
              + (photoColor2(i)(1) - photoColor2(j)(1)) * (photoColor2(i)(1) - photoColor2(j)(1))
              + (photoColor2(i)(2) - photoColor2(j)(2)) * (photoColor2(i)(2) - photoColor2(j)(2))) 
             photoDistancesColors(i)(j) = d1+d2;
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
   * Method which loads Grey AVG values
   * @param fileName
   */
  private def computePhotoGreyAVG(fileName: String) {
    try {
      val reader = new FileReader(fileName)
      val parser = new JSONParser()
      val obj = parser.parse(reader)
      val array = obj.asInstanceOf[JSONArray]
      var photoGreyAVG = Array.ofDim[Int](array.size)
      photoDistancesGreyAVG = Array.ofDim[Double](array.size, array.size)
      
      for (i <- 0 until array.size) {
        val image = array.get(i).asInstanceOf[JSONObject]
        photoGreyAVG(i) = image.get("greyavg").toString().toInt
      }
      
      for(i <- 0 until array.size)
        for(j <- 0 until array.size)
          photoDistancesGreyAVG(i)(j) = math.abs(photoGreyAVG(i) - photoGreyAVG(j))
      
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
   * Common eval function used for load each eval function and implements album page cohesion or not
   * @param arr
   * @param solution
   * @return
   */
  def eval(arr: Array[Array[Double]], solution: Array[Int]): Double = {
    var sum: Double = 0
    
    for (i <- 0 until albumInvDist.length; j <- i + 1 until albumInvDist.length)
        sum += arr(solution(i))(solution(j)) * albumInvDist(i)(j)
 
    sum
  }

  /**
   *
   * Un exemple de fonction objectif (à minimiser): distance entre les photos
   * pondérées par l'inverse des distances spatiales sur l'album Modélisation
   * comme un problème d'assignement quadratique (QAP)
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
   * New objective function based on QAP
   *
   * Objective function which uses distance defined by common tags value
	 * weighted by the inverse of the spatial distances on the album 
   *
   * @param solution
   * @return score
   */
  def commonsTagEval(solution: Array[Int]): Double = {
    eval(photoDistancesCommonsTags, solution)
  }

  /**
   * New objective function based on QAP
   *
   * Objective function which uses distance defined by uncommon tags value 
	 * weighted by the inverse of the spatial distances on the album 
	 * 
   * @param solution
   * @return
   */
  def uncommonsTagEval(solution: Array[Int]): Double = {
    eval(photoDistancesUncommonsTags, solution)
  }

  /**
   * New objective function based on QAP
   *
   * Objective function which uses distance defined by number of uncommon tags 
	 * weighted by the inverse of the spatial distances on the album 
	 * 
   * @param solution
   * @return
   */
  def nbUnommonsTagEval(solution: Array[Int]): Double = {
    eval(photoDistancesUncommonsNbTags, solution)
  }

  /**
   * New objective function based on QAP
   *
   * Objective function which uses distance defined by distance between colors (color 1 & color2)
	 * weighted by the inverse of the spatial distances on the album 
   *
   * @param solution
   * @return score
   */
  def colorsEval(solution: Array[Int]): Double = {
    eval(photoDistancesColors, solution)
  }

  /**
	 * New objective function based on QAP
   *
   * Objective function which uses distance defined by grey AVG value
	 * weighted by the inverse of the spatial distances on the album 
   *
   * @param solution
   * @return score
   */
  def greyAVGEval(solution: Array[Int]): Double = {
    eval(photoDistancesGreyAVG, solution)
  }
}

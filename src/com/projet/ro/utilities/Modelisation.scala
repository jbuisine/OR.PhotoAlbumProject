package com.projet.ro.utilities

import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.text.DecimalFormat
import java.util.Random
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
//remove if not needed
import scala.collection.JavaConversions._
import com.projet.ro.classes.FileClass

object Modelisation {

  // Distance between photos
  private var photoDist: Array[Array[Double]] = _
  
  // Tags of all photos
  private var photoTags: Array[Map[String,Double]] = _

  private var albumInvDist: Array[Array[Double]] = _

  private var df: DecimalFormat = new java.text.DecimalFormat("0.##")

  def init(pathPhoto: String, pathAlbum: String, attribute: String) {
    computeDistances(pathPhoto, pathAlbum, attribute)
    computePhotoTags(pathPhoto)
  }

  /**
 	 *
 	 * Example of json file parsing
 	 *
 	 * see: https://code.google.com/p/json-simple/ for more example to decode
 	 * json under java
 	 *
 	 */
  private def readPhotoExample(fileName: String) {
    try {
      val reader = new FileReader(fileName)
      val parser = new JSONParser()
      val obj = parser.parse(reader)
      val array = obj.asInstanceOf[JSONArray]
      println("The first element:\n" + array.get(0))
      val obj2 = array.get(0).asInstanceOf[JSONObject]
      println("the id of the first element is: " + obj2.get("id"))
      val arraytag = obj2.get("tags").asInstanceOf[JSONObject].get("classes").asInstanceOf[JSONArray]
      println("Tag list of the first element:")
      for (i <- 0 until arraytag.size) System.out.print(" " + arraytag.get(i))
      println()
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
 	 * Compute the matrice of distance between solutions and of inverse distance
 	 * between positions
 	 */
  private def computeDistances(photoFileName: String, albumFileName: String, attribute: String) {
    computePhotoDistances(photoFileName, attribute)
    computeAlbumDistances(albumFileName)
  }

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

  private def computePhotoTags(fileName: String) {
    try {
      val reader = new FileReader(fileName)
      val parser = new JSONParser()
      val obj = parser.parse(reader)
      val array = obj.asInstanceOf[JSONArray]
      val nbTags: Int = array.get(0).asInstanceOf[JSONObject].get("tags").asInstanceOf[JSONObject].get("classes").asInstanceOf[JSONArray].size
      photoTags = Array.ofDim[Map[String,Double]](array.size)
      for (i <- 0 until array.size) {
        photoTags(i) = collection.immutable.Map[String, Double]()
        val tags = array.get(i).asInstanceOf[JSONObject].get("tags").asInstanceOf[JSONObject].get("classes").asInstanceOf[JSONArray]
        val probs = array.get(i).asInstanceOf[JSONObject].get("tags").asInstanceOf[JSONObject].get("probs").asInstanceOf[JSONArray]
        for (j <- 0 until nbTags) {
          photoTags(i) += (tags.get(j).toString() -> probs.get(j).toString().toDouble)
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
   * - dhashdist : différence hash
   * 
   * Ce paramètre sera fournit lors de l'initialisation de la classe.
   * 
   * @param solution
   * @return
   */
  def hashEval(solution: Array[Int]): Double = {
    var sum : Double = 0
    for (i <- 0 until albumInvDist.length; j <- i + 1 until albumInvDist.length) {
      sum += photoDist(solution(i))(solution(j)) * albumInvDist(i)(j)
    }
    sum
  }
  
  /**
   * Fourth objective function
   * 
   * Fonction qui va calculer les différence entre les différents tags commun 
   * 
   * @param solution
   * @return
   */
  def tagsEval(solution: Array[Int]): Double = {
    var sum : Double = 0
    for (i <- 0 until photoTags.length -1) {
      var count: Int = 0
      var currentSum: Double = 0
      
      //Get sum of all sames tags
      photoTags(solution(i)).foreach( x => photoTags(solution(i+1)).foreach(y => {
        if(x._1 == y._1){
          count += 1;
          currentSum += Math.abs(x._2 - y._2)
        }
      }));
      
      //Priviligie les éléments avec plus de tags commun en divisant par count avant d'ajouter la somme
      if(count != 0)
        sum += currentSum/count; 
    }
    sum
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
  
  def mergeMap[A, B](ms: List[Map[A, B]])(f: (B, B) => B): Map[A, B] =
    (Map[A, B]() /: (for (m <- ms; kv <- m) yield kv)) { (a, kv) =>
      a + (if (a.contains(kv._1)) kv._1 -> f(a(kv._1), kv._2) else kv)
  }
}

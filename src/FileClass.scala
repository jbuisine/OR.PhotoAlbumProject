



import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import scala.collection.JavaConversions._

class FileClass(nom: String) {

  private var fichier: File = new File(nom)

  var fichierReader: BufferedReader = _

  this.open()

  try {
    fichierReader = new BufferedReader(new FileReader(fichier))
  } catch {
    case e: FileNotFoundException => println("File does not exist... Creation of the file...")
    fichier.getParentFile().mkdirs();
    //val writer = new FileWriter(fichier);
  }

  private def open() {
    fichier.setExecutable(true)
    fichier.setReadable(true)
    fichier.setWritable(true)
  }

  def nextLine(): String = {
    var value = ""
    try {
      value = fichierReader.readLine()
    } catch {
      case e: IOException => value = null
    }
    value
  }

  def close() {
    fichier.setExecutable(false)
    fichier.setReadable(false)
    fichier.setWritable(false)
  }

  def writeLine(pVal: String, b: Boolean) {
    try {
      val fichierWriter = new BufferedWriter(new FileWriter(fichier, b))
      fichierWriter.write(pVal)
      fichierWriter.newLine()
      fichierWriter.close()
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def findLine(pVal: String): Boolean = {
    var check = false
    try {
      val fichierReader = new BufferedReader(new FileReader(fichier))
      while ((fichierReader.readLine()) != null && fichierReader.ready()) {
        if (pVal == fichierReader.readLine().toString) check = true
      }
      fichierReader.close()
    } catch {
      case e: IOException => e.printStackTrace()
    }
    check
  }
  
  def fileExist(): Boolean = fichier.exists()
}

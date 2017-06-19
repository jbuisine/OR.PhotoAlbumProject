import java.io._

/**
 * @author j.buisine
 *
 */
class FileClass(nom: String) {

  private val file: File = new File(nom)

  var fileReader: BufferedReader = _

  this.open()

  try {
    fileReader = new BufferedReader(new FileReader(file))
  } catch {
    case _: FileNotFoundException => println("File does not exist... Creation of the file...")
    file.getParentFile.mkdirs();
    //val writer = new FileWriter(file);
  }

  private def open() {
    file.setExecutable(true)
    file.setReadable(true)
    file.setWritable(true)
  }

  def nextLine(): String = {
    var value = ""
    try {
      value = fileReader.readLine()
    } catch {
      case _: IOException => value = null
    }
    value
  }

  def close() {
    file.setExecutable(false)
    file.setReadable(false)
    file.setWritable(false)
  }

  def writeLine(pVal: String, b: Boolean) {
    try {
      val fileWriter = new BufferedWriter(new FileWriter(file, b))
      fileWriter.write(pVal)
      fileWriter.newLine()
      fileWriter.close()
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def findLine(pVal: String): Boolean = {
    var check = false
    try {
      val fileReader = new BufferedReader(new FileReader(file))
      while (fileReader.readLine() != null && fileReader.ready()) {
        if (pVal == fileReader.readLine()) check = true
      }
      fileReader.close()
    } catch {
      case e: IOException => e.printStackTrace()
    }
    check
  }
  
  def fileExist(): Boolean = file.exists()
}

import scala.io.{Source, Codec}
import java.io.{PrintWriter, File}
import java.nio.charset.CodingErrorAction

class Entry(
  val user : String, 
  val date : String, 
  val time : String, 
  val action : String) {

  var year = 2000
  var month = 1
  var day = 1
  var hour = 1
  var minute = 1

  // This condition is necessary to prevent ArrayIndexOutOfBoundsExceptions from being fatal if data is corrupted
  if(date.length == 10 && time.length == 5) {
    year = date.substring(6, 10).toInt
    month = date.substring(3, 5).toInt
    day = date.substring(0, 2).toInt
    hour = time.substring(0, 2).toInt
    minute = time.substring(3, 5).toInt
  }
}

object Entry {
  def fromString(data : String) = {
    val array = data.split("\t")
    new Entry(array(0).replace(" ", ""), array(1), array(2), array(3))
  }
}

class Hist(path : String)
{
  val decoder = Codec.UTF8.decoder.onMalformedInput(CodingErrorAction.IGNORE)
  val data = Source.fromFile(path)(decoder).getLines.toList
  val entries = data.tail.map(line => Entry.fromString(line))
  def entries_filtered = entries.filter(_.action == "Code eingegeben")

  def output(path : String) {
    try {
      val file = new File(path)
      if(file.exists) 
        file.delete()
      val writer = new PrintWriter(file)
      val entries = this.entries_filtered.sortBy(e => 
        (e.year, e.month, e.day, e.hour, e.minute))
      for(entry <- entries) {
        writer.write(entry.user)
        writer.write(",")
        writer.write(entry.date)
        writer.write(",")
        writer.write(entry.time)
        writer.write("\n")
      }
      writer.close()
    } finally {}
  }
}

object HSTParse extends App {
  println("HSTParse version 1.2 by A. Seitz")

  val dir = new File("./")
  println("Converting all .HST files in directory " + dir.getAbsolutePath())
  val files = dir.listFiles().filter(isHist(_))
  for (file <- files) {
    val i = file.getName.lastIndexOf('.')
    val fname_noext = file.getName.substring(0, i)
    val fname_target = fname_noext + ".csv"
    new Hist(file.getName).output(fname_target)
    println(">> " + file.getName + " converted successfully.")
  }

  def isHist(file : File) = {
    val i = file.getName.lastIndexOf('.')
    i >= 0 && file.getName.substring(i + 1).equalsIgnoreCase("HST")
  }

}

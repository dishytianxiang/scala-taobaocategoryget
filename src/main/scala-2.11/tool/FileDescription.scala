package tool

import java.io.{File, FileWriter, PrintWriter}
import java.nio.charset.CharsetDecoder
import java.nio.charset.StandardCharsets
import java.util

//import util.control.Breaks._
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

/**
  * Created by kingdee on 2017/6/12.
  */
class FileDescription(val fd: String) {
  var fileDir: String = ""
  //listfile.name
  def FileDescription(fd: String) {
    this.fileDir = fd
  }
}
object FileDescription {
  val file = "dishy"
  def listFiles(fileDir:String): Future[Iterator[File]] = Future{
    println("start list File")
    val f = new File(fileDir)
    subList(new File(fileDir))
  }
  def subList(file: File): Iterator[File] = {
    val dirs = file.listFiles().filter(_.isDirectory)
    val files = file.listFiles().filter(_.isFile)
    //files.toIterator ++
    files.toIterator ++ dirs.toIterator.flatMap(subList _)
  }
  def readCsv(file: String,outPutFile: String): Future[String] = {
    val result = Promise[String]
    val content = Source.fromFile(file)("UTF-8").getLines()
    val contentFormat = ListBuffer[String]()
    //if (content.length <= 2) {
      //println(file)
   // }
    for (line <- content) {
      val data = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)")
      if (data.length < 3) {
        println(data.length)
        println(line)
      }else{
        var str = data.reduce(_+"\u0001"+_)
        str = str + "\n"
        contentFormat.append(str)
        if(contentFormat.size > 1500) {
          ToFile(contentFormat,outPutFile)
        }
      }
    }

    result.success("success")
    result.future
  }
  def ToFile(fs: ListBuffer[String],file: String): Unit = this.synchronized {
    val writer = new FileWriter(new File(file),true)
    fs foreach(writer.write(_))
    writer.close()
    fs.clear()
  }
  def sum(n:Int):Int = {
    var r = 0;
    for (i <- 1 to 10)
      r = r*i
    return r
  }
}

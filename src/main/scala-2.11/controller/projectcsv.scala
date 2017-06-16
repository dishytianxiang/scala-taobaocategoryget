package controller

import java.sql.{DriverManager, ResultSet}

import com.sun.javaws.jnl.XMLFormat
import model.ConfigModel
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements
import tool.{FileDescription, TaoBaoCategory}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.Try
import scala.xml.XML
//import scala.io.Source
import org.json4s._
import org.json4s.native.JsonMethods._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import scala.util.{Failure, Success}

/**
  * Created by kingdee on 2017/6/12.
  */
object projectcsv {
  def main(args: Array[String]): Unit = {
    categoryGet()

  }
  //淘宝数据格式化
  def Task(): Unit = {
    System.setProperty("scala.concurrent.context.maxThreads", "4")
    val future = FileDescription.listFiles(ConfigModel.filePath)
    println(future.isCompleted)
    val result = Await.result(future, Duration.Inf)
    val files = result.map(f => f.getAbsolutePath)
    val sqeue = ListBuffer[Future[String]]()
    for (fp <- files) {
      sqeue.append(FileDescription.readCsv(fp,ConfigModel.taoBaoOutPutFile))
    }
    val f4 = Future.sequence(sqeue)
    val result4 = Await.result(f4, Duration.Inf)
    println(result4)
  }
  //淘宝类别获取
  def categoryGet(): Unit = {
    var listId = mutable.Map[String, String]()
    Try(Jsoup.connect("https://www.taobao.com/").get()) match {
      case Failure(e) =>
        println(e.getMessage)
      case Success(doc) =>
        listId = parseDoc(doc)
    }
    System.setProperty("scala.concurrent.context.maxThreads", "4")
    val listBuffer = new ListBuffer[Future[(String,String)]]()
    //listId.foreach(x => listBuffer.append(TaoBaoCategory.ReadCategory(x)))
    for ((k,v) <- listId) {
      listBuffer.append(TaoBaoCategory.ReadCategory(k,v))
    }
    val f = Future.sequence(listBuffer)
    val result = Await.result(f,Duration.Inf)
    for ((name,jsondata) <- result) {
      val  jsonObj = parse(jsondata)
      val jsonParse = parse(jsondata).values.asInstanceOf[Map[String,Any]]
      for ((k1,v1) <- jsonParse) {
        val v2 = v1.asInstanceOf[Map[String,Any]].get("value").get.asInstanceOf[Map[String,Any]].get("list").get.asInstanceOf[List[Map[String,Any]]]
        v2.foreach {
          case m: Map[String,Any] => {
            val nameitem = if (!m.get("name").isEmpty) m.get("name").get else Some("")
            val link = if (!m.get("link").isEmpty) m.get("link").get else Some("")
           // println(s"${name} : ${k1}: ${nameitem}: ${link}")
            val str = s"insert into tmitemcatalog(categoryid,firstname,secondname,secondnameurl,status) value('${k1}','${name}','${nameitem}','${link}','1')"
            println(str)
            insertData(str)
          }
        }
      }
    }
  }
  def parseDoc(doc:Document): mutable.Map[String, String] = {
    val map = scala.collection.mutable.Map[String,String]()
   (doc.select("a[data-cid=\"1\"]").toArray().foreach(x => {val d = XML.loadString(x.toString);map += (d.text -> d.attribute("data-dataid").get.toString())}))//.map(_.get.toString().toInt)
    map
  }
  def display(input: String) = println(input)
  val divide = new PartialFunction[Int,Int] {
    def isDefinedAt(x: Int): Boolean = (x != 0)
    def apply(x: Int): Int = 100/x
  }
  val divide2 : PartialFunction[Int,Int] ={
    case d: Int if d != 0 => 100/d
  }
  def insertData(str: String):Unit = {
    classOf[com.mysql.jdbc.Driver]
    val conn = DriverManager.getConnection(ConfigModel.mysqlMessage)
    try {
      val statement = conn.createStatement()
      //val rs = statement.executeQuery("")
      val resutl = statement.executeUpdate(str)

    }finally {
      conn.close()
    }
  }
  implicit def typeConvert(s: Int) : String = s.toString
}

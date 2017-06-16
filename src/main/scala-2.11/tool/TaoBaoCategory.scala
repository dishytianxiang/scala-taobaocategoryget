package tool

import model.ConfigModel

import scala.concurrent.{Future, Promise}
import scala.io.Source

/**
  * Created by kingdee on 2017/6/14.
  */
class TaoBaoCategory() {

}
object TaoBaoCategory {
  def ReadCategory(name: String,id:String,url: String = ConfigModel.taoBaoCagetoryUrlGet): Future[(String,String)] = {
    val p = Promise[(String,String)]
    val fileContent = Source.fromURL(url + id,"utf-8").mkString
    p.success((name,fileContent))
    p.future
  }
  implicit def convertIntToString(arg: Int) = arg.toString
  implicit def convertBoolToString(arg: Boolean) = if(arg) "true" else "false"
  implicit def convertLongToString(arg: Long) = arg.toString
  implicit def convertFloatToString(arg: Float) = arg.toString
}

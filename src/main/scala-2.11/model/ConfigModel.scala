package model

/**
  * Created by kingdee on 2017/6/12.
  */
object ConfigModel {
  private val env = false
  private val taoBaoDirPath = "F:\\jingdongscrapyrecords\\taobao\\taobao"
  private val taoBaoDirPahtPro = ""
  private val taoBaoDirPahtProOutPutFile = "F:\\jingdongscrapyrecords\\taobao\\taobao\\taobaodata.txt"
  private val taoBaoCagetoryUrl = "https://tce.alicdn.com/api/data.htm?ids="
  private val conn_str = "jdbc:mysql://xxxxx:3306/dishy?user=xxx&password=xxxx"
  def filePath: String = {
    if(env) taoBaoDirPahtPro else taoBaoDirPath
  }
  def taoBaoOutPutFile: String = {
    if(env) taoBaoDirPahtProOutPutFile else taoBaoDirPahtProOutPutFile
  }
  def taoBaoCagetoryUrlGet:String = {
    if(env) taoBaoCagetoryUrl else taoBaoCagetoryUrl
  }
  def mysqlMessage: String = {
    if(env) conn_str else conn_str
  }
}

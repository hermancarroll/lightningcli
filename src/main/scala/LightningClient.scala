import java.io.{BufferedReader, InputStreamReader, PrintWriter}

import org.scalasbt.ipcsocket.UnixDomainSocket
import spray.json.DefaultJsonProtocol

import spray.json._
import DefaultJsonProtocol._

case class GetInfo(command: String = "getinfo", params: Array[String] = Array.empty, id: Int)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val getinfoFormat: RootJsonFormat[GetInfo] = jsonFormat3(GetInfo)
}


object LightningClient extends App {


  import MyJsonProtocol._


  val path = "/var/lib/docker/volumes/generated_clightning_bitcoin_datadir/_data/lightning-rpc"
  val client = new UnixDomainSocket(path)


  val out = new PrintWriter(client.getOutputStream, true)
  val in = new BufferedReader(new InputStreamReader(client.getInputStream))


  var id = -1

  def getInfo(method : String = "getinfo") = {

    id += 1
    val info = GetInfo(id = id)
    println(info.toJson)
    info.toJson.prettyPrint
    out.println(info.toJson)
    val line = in.readLine
    println(line)


  }

  getInfo("help")
  getInfo()




}

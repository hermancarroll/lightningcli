import java.io.{BufferedReader, InputStreamReader, PrintWriter}

import org.scalasbt.ipcsocket.UnixDomainSocket
import spray.json.DefaultJsonProtocol

import spray.json._
import DefaultJsonProtocol._

case class GetInfo(method: String = "getinfo", params: Array[String] = Array.empty[String], id: Int)

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

  def send(method : String, params: Array[String] = Array.empty[String]) = {

    id += 1
    val info = GetInfo(method = method,id = id, params = params)
    println(info.toJson.prettyPrint)
    out.println(info.toJson)
    val line = in.readLine
    println(line)


  }
  // fixme: not parsing
//  send("listpeers")
//  send("listinvoices")

//  send("getinfo")
//  send("help")
  send("pay", Array("lntb1u1pwmtmzypp5d4dmmw0p37ft9rtf4lq582v0p7fsulrcqt968wxpxxne6ynt8j6sdqqcqzpgxqyz5vqk88lferk6jgzc9pq6qr3s9w8lpww7vs5a0n27336vnrhzgnw2e2zg829shpa9srgtga4t68c6ahetaqmkefut72fq5vsdlat74ekfzgpwmrxj9"))




}

import java.io.{BufferedReader, InputStreamReader, PrintWriter}

import org.scalasbt.ipcsocket.UnixDomainSocket

object LightningClient extends App {

  val path = "/var/lib/docker/volumes/generated_clightning_bitcoin_datadir/_data/lightning-rpc"
  val client = new UnixDomainSocket(path)

  val out = new PrintWriter(client.getOutputStream, true)
  val in = new BufferedReader(new InputStreamReader(client.getInputStream))


  var id = 0
  def requestParam(command: String, params: String) = {

    id += 1
  s"""
    |{
    |    method: $command,
    |    params: $params,
    |    id: $id
    |  }
  """.stripMargin

  }


  def send(command: String, params: String) = {

    val r = requestParam(command, params)
    out.println(r)
    val line = in.readLine
    println(line)


  }

  def getInfo = {

    send("getinfo", "{}")
  }

  getInfo

}

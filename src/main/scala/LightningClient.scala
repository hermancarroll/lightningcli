import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.util.concurrent.atomic.AtomicInteger

import org.scalasbt.ipcsocket.UnixDomainSocket
import spray.json.DefaultJsonProtocol
import spray.json._
import DefaultJsonProtocol._

case class Request(method: String, params: Array[String] = Array.empty[String], id: Int)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val getinfoFormat: RootJsonFormat[Request] = jsonFormat3(Request)
}

trait Response[T] {

  def id: Int
  def jsonrpc: Double = 2.0
  def response: T
}

case class ResultBis(
                      id: Double,
                      payment_hash: String,
                      destination: String,
                      msatoshi: Double,
                      amount_msat: String,
                      msatoshi_sent: Double,
                      amount_sent_msat: String,
                      created_at: Double,
                      status: String,
                      payment_preimage: String,
                      bolt11: String
                    )

case class Payments(
                     id: Double,
                     payment_hash: String,
                     destination: String,
                     msatoshi: Double,
                     amount_msat: String,
                     msatoshi_sent: Double,
                     amount_sent_msat: String,
                     created_at: Double,
                     status: String,
                     payment_preimage: String,
                     bolt11: String
                   )

case class ListPayments(id: Int, response: Payments) extends Response[Payments]

case class PayResponse(id: Int, response: ResultBis) extends Response[ResultBis]


object LightningClient extends App {


  import MyJsonProtocol._


  val path = "/var/lib/docker/volumes/generated_clightning_bitcoin_datadir/_data/lightning-rpc"

  val atom = new AtomicInteger()
  val client = new UnixDomainSocket(path)
  val in = new BufferedReader(new InputStreamReader(client.getInputStream))
  val out = new PrintWriter(client.getOutputStream, true)

  def send(method : String, params: Array[String] = Array.empty[String]) = {

    val id = atom.getAndIncrement()
    val info = Request(method = method, id = id, params = params)
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
  //send("pay", Array("lntb100n1pwmv0wdpp5jse9v4pvwywwt7xucvzmnrzr8dlwm7t0tx9c38juncvus02yapxqdqqcqzpgxqyz5vqcn36xtue6yzcmf9nr70h6n0uhfsg6jwc320w66r9hh6zsymqyeh9jwkdqge5u2slpqjdw8j8kggrn3re68pw7ysylxvrt3f9tdf9c0gp4vrfxr"))

  send("listpayments", Array("lntb100n1pwmv0wdpp5jse9v4pvwywwt7xucvzmnrzr8dlwm7t0tx9c38juncvus02yapxqdqqcqzpgxqyz5vqcn36xtue6yzcmf9nr70h6n0uhfsg6jwc320w66r9hh6zsymqyeh9jwkdqge5u2slpqjdw8j8kggrn3re68pw7ysylxvrt3f9tdf9c0gp4vrfxr"))


}

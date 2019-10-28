package com.mathbot.lightning

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.util.concurrent.atomic.AtomicInteger

import org.scalasbt.ipcsocket.UnixDomainSocket
import spray.json.{DefaultJsonProtocol, _}

case class Request(method: String, id: Int, params: Array[String] = Array.empty[String])

trait MyJsonProtocol extends DefaultJsonProtocol {
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


class LightningClient(path: String = "/var/lib/docker/volumes/generated_clightning_bitcoin_datadir/_data/lightning-rpc") extends MyJsonProtocol {



  private val atom = new AtomicInteger()

  private val client = new UnixDomainSocket(path)
  private val in = new BufferedReader(new InputStreamReader(client.getInputStream))
  private val out = new PrintWriter(client.getOutputStream, true)

  def call(method : String, params: String*) = {

    val id = atom.getAndIncrement()
    val info = Request(method = method, id = id, params = params.toArray)
    println(info.toJson.prettyPrint)
    out.println(info.toJson)
    val line = in.readLine
    println(line)
    line

  }

  def close = {
    client.close()
  }




  // fixme: not parsing
//  call("listpeers")
//  call("listinvoices")

//  call("getinfo")
//  call("help")
  //call("pay", Seq("lntb100n1pwmv0wdpp5jse9v4pvwywwt7xucvzmnrzr8dlwm7t0tx9c38juncvus02yapxqdqqcqzpgxqyz5vqcn36xtue6yzcmf9nr70h6n0uhfsg6jwc320w66r9hh6zsymqyeh9jwkdqge5u2slpqjdw8j8kggrn3re68pw7ysylxvrt3f9tdf9c0gp4vrfxr"))



}


object LightningClientApp extends App {

  val client = new LightningClient()

//  client.call("pay", "lntb100n1pwmvnwspp5qe5jtqym2nfexgvzawcsjhejyjqkgcvq305nywz8m5edsle7gghqdqqcqzpgxqyz5vqz7taykkfas4gszlqj6sh3tmht03le4q3uc988xdfnpkyejkrnrwztlm8lluccfn7kr4330dylxkzn58d2s5f07zq9cpnhwvq07nla2cq6de2z0")
  client.call("listpayments", "lntb100n1pwmv0wdpp5jse9v4pvwywwt7xucvzmnrzr8dlwm7t0tx9c38juncvus02yapxqdqqcqzpgxqyz5vqcn36xtue6yzcmf9nr70h6n0uhfsg6jwc320w66r9hh6zsymqyeh9jwkdqge5u2slpqjdw8j8kggrn3re68pw7ysylxvrt3f9tdf9c0gp4vrfxr")
//  client.call("listpayments",  "lntb100n1pwmvnwspp5qe5jtqym2nfexgvzawcsjhejyjqkgcvq305nywz8m5edsle7gghqdqqcqzpgxqyz5vqz7taykkfas4gszlqj6sh3tmht03le4q3uc988xdfnpkyejkrnrwztlm8lluccfn7kr4330dylxkzn58d2s5f07zq9cpnhwvq07nla2cq6de2z0")

  client.close
  sys.exit(0)
}

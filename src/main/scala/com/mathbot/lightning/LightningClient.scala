package com.mathbot.lightning

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.util.concurrent.atomic.AtomicInteger

import org.scalasbt.ipcsocket.UnixDomainSocket
import spray.json._

import scala.collection.immutable.List



class LightningClient(path: String = "/var/lib/docker/volumes/generated_clightning_bitcoin_datadir/_data/lightning-rpc") extends MyJsonProtocol {

  private val atom = new AtomicInteger()

  private val client = new UnixDomainSocket(path)
  private val in = new BufferedReader(new InputStreamReader(client.getInputStream))
  private val out = new PrintWriter(client.getOutputStream, true)

  def call(method : String, params: String*): String = {

    val id = atom.getAndIncrement()
    val info = Request(method = method, id = id, params = params.toArray)
    println(info.toJson.prettyPrint)
    out.println(info.toJson)
    val line = in.readLine
    println(line)
    line

  }

  def call(method: String, params: JsObject) = {

    val id = atom.getAndIncrement()
    val info = JsObject(
      "method" -> JsString(method),
      "id" -> JsNumber(id),
      "params" -> params
    )
    println(info.toJson.prettyPrint)
    out.println(info.toJson)
    val line = in.readLine
    println(line)
    line
  }

  /**
    * Sets up automatic cleaning of expired invoices. {cycle_seconds} sets
    * the cleaning frequency in seconds (defaults to 3600) and {expired_by}
    * sets the minimum time an invoice should have been expired for to be
    * cleaned in seconds (defaults to 86400).
    * @param cycleSeconds
    * @param expiredBy
    */
  def autocleaninvoice(cycleSeconds: Option[Int], expiredBy: Option[Int]) = {
    call("autocleaninvoice", JsObject(
      "cycle_seconds" -> cycleSeconds.map(JsNumber(_)).getOrElse(JsNull),
      "expired_by" -> expiredBy.map(JsNumber(_)).getOrElse(JsNull)
    ))
  }

  def pay(bolt11: String): String =
    call("pay", bolt11)


  def listPayments(bol11: String) : String  = listPayments(Some(bol11))

  def listPayments(bolt11: Option[String]): String =
    call("listpayments", bolt11 getOrElse Array.empty)

  def listTransactions = call("listtransactions")

  /**
    * Show invoice {label} (or all, if no {label))
    * @param invoice
    * @return
    */
  def listInvoices(invoice: Option[String]) = call("listinvoices", invoice getOrElse Array.empty)


  /**
    * Show all nodes in our local network view, filter on node {id}
    * if provided
    *
    * @param node
    * @return
    */
  def listNodes(node: Option[String]) = call("listnodes", node getOrElse Array.empty)

  def help: String = call("help")

  def getinfo: String = call("getinfo")

  def decodePay(bolt11: String) = call("decodepay", bolt11)

  def payStatus(bolt11: Option[String]) = call("paystatus", bolt11 getOrElse Array.empty)

  def setChannelFee(id: Option[String], base: Option[Int], ppm: Option[Int]) = {
    call("setchannelfee")
  }

  def close(): Unit =
    client.close()


}


object LightningClientApp extends App {

  val client = new LightningClient()

  client.autocleaninvoice(None, None)

  client.listPayments("lntb100n1pwmv0wdpp5jse9v4pvwywwt7xucvzmnrzr8dlwm7t0tx9c38juncvus02yapxqdqqcqzpgxqyz5vqcn36xtue6yzcmf9nr70h6n0uhfsg6jwc320w66r9hh6zsymqyeh9jwkdqge5u2slpqjdw8j8kggrn3re68pw7ysylxvrt3f9tdf9c0gp4vrfxr")
  client.close()
//  client.call("pay", "lntb100n1pwmvnwspp5qe5jtqym2nfexgvzawcsjhejyjqkgcvq305nywz8m5edsle7gghqdqqcqzpgxqyz5vqz7taykkfas4gszlqj6sh3tmht03le4q3uc988xdfnpkyejkrnrwztlm8lluccfn7kr4330dylxkzn58d2s5f07zq9cpnhwvq07nla2cq6de2z0")
//  client.call("listpayments",  "lntb100n1pwmvnwspp5qe5jtqym2nfexgvzawcsjhejyjqkgcvq305nywz8m5edsle7gghqdqqcqzpgxqyz5vqz7taykkfas4gszlqj6sh3tmht03le4q3uc988xdfnpkyejkrnrwztlm8lluccfn7kr4330dylxkzn58d2s5f07zq9cpnhwvq07nla2cq6de2z0")
  //  call("listpeers")
  //  call("listinvoices")

  //  call("getinfo")
  //  call("help")
}

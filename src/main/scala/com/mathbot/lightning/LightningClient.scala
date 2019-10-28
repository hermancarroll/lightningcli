package com.mathbot.lightning

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.util.concurrent.atomic.AtomicInteger

import org.scalasbt.ipcsocket.UnixDomainSocket
import spray.json._

import scala.collection.immutable.List
import scala.concurrent.duration.Duration



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

//  def call(method: String, params: JsObject): String = {
//
//    val id = atom.getAndIncrement()
//    val info = JsObject(
//      "method" -> JsString(method),
//      "id" -> JsNumber(id),
//      "params" -> params
//    )
//    println(info.toJson.prettyPrint)
//    out.println(info.toJson)
//    val line = in.readLine
//    println(line)
//    line
//  }

  /**
    * Sets up automatic cleaning of expired invoices. {cycle_seconds} sets
    * the cleaning frequency in seconds (defaults to 3600) and {expired_by}
    * sets the minimum time an invoice should have been expired for to be
    * cleaned in seconds (defaults to 86400).
    * @param cycleSeconds
    * @param expiredBy
    */
  def autoCleanInvoice(cycleSeconds: Int = 3600, expiredBy: Int = 86400): String =
    call("autocleaninvoice", cycleSeconds.toString, expiredBy.toString)

  def pay(bolt11: String): String =
    call("pay", bolt11)


  def listPayments(bol11: String) : String  = listPayments(Some(bol11))

  /**
    * Show outgoing payments, regarding {bolt11} or {payment_hash} if set
    * @param bolt11
    * @return
    */
  def listPayments(bolt11: Option[String]): String =
    call("listpayments", bolt11 getOrElse Array.empty)

  /**
    * Show wallet history
    * @return
    */
  def listTransactions: String = call("listtransactions")

  /**
    * Show invoice {label} (or all, if no {label))
    * @param invoice
    * @return
    */
  def listInvoices(invoice: Option[String]): String = call("listinvoices", invoice getOrElse Array.empty)


  /**
    * Show route to {id} for {msatoshi}, using {riskfactor} and optional {cltv} (default 9). If specified search from {fromid} otherwise use this node as source. Randomize the route with up to {fuzzpercent} (default 5.0). {exclude} an array of short-channel-id/direction (e.g. [ '564334x877x1/0', '564195x1292x0/1' ]) from consideration. Set the {maxhops} the route can take (default 20).
    */
  // def getRoute(id: String, msatoshi: Long, riskFactor: Any, cltv: Option[Int], fromId )
  /**
    * Get a new {bech32, p2sh-segwit} (or all) address to fund a channel (default is bech32)
    * @param addressType
    * @return
    */
  def newAddress(addressType: Option[String]) = {

    call("newaddr", addressType getOrElse Array.empty)
  }
  /**
    * Show all nodes in our local network view, filter on node {id}
    * if provided
    *
    * @param node
    * @return
    */
  def listNodes(node: Option[String]): String = call("listnodes", node getOrElse None)

  /**
    * Create an invoice for {msatoshi} with {label} and {description} with
    * optional {expiry} seconds (default 1 week)
    *
    * @param msatoshi
    * @param label
    * @param description
    * @param expiry
    */
  def invoice(msatoshi: Long, label: String, description: String, expiry: Long = Duration("1 week").toSeconds) =
    call("invoice", msatoshi.toString, label, description, expiry.toString)

  /**
    * Show available commands, or just {command} if supplied.
    * @param command
    * @return
    */
  def help(command: String): String = call("help", command)

  def help: String = call("help")

  def getinfo: String = call("getinfo")

  def decodePay(bolt11: String): String = call("decodepay", bolt11)

  def payStatus(bolt11: Option[String]): String = call("paystatus", bolt11 getOrElse Array.empty)

  def setChannelFee(id: Option[String], base: Option[Int], ppm: Option[Int]): String = {
    call("setchannelfee")
  }

  def close(): Unit =
    client.close()


}


object LightningClientApp extends App {

  val client = new LightningClient()

  client.autoCleanInvoice()

  client.listPayments("lntb100n1pwmv0wdpp5jse9v4pvwywwt7xucvzmnrzr8dlwm7t0tx9c38juncvus02yapxqdqqcqzpgxqyz5vqcn36xtue6yzcmf9nr70h6n0uhfsg6jwc320w66r9hh6zsymqyeh9jwkdqge5u2slpqjdw8j8kggrn3re68pw7ysylxvrt3f9tdf9c0gp4vrfxr")
  client.close()
//  client.call("pay", "lntb100n1pwmvnwspp5qe5jtqym2nfexgvzawcsjhejyjqkgcvq305nywz8m5edsle7gghqdqqcqzpgxqyz5vqz7taykkfas4gszlqj6sh3tmht03le4q3uc988xdfnpkyejkrnrwztlm8lluccfn7kr4330dylxkzn58d2s5f07zq9cpnhwvq07nla2cq6de2z0")
//  client.call("listpayments",  "lntb100n1pwmvnwspp5qe5jtqym2nfexgvzawcsjhejyjqkgcvq305nywz8m5edsle7gghqdqqcqzpgxqyz5vqz7taykkfas4gszlqj6sh3tmht03le4q3uc988xdfnpkyejkrnrwztlm8lluccfn7kr4330dylxkzn58d2s5f07zq9cpnhwvq07nla2cq6de2z0")
  //  call("listpeers")
  //  call("listinvoices")

  //  call("getinfo")
  //  call("help")
}

package com.mathbot.lightning

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.util.concurrent.atomic.AtomicInteger

import org.scalasbt.ipcsocket.UnixDomainSocket
import spray.json._

import scala.concurrent.duration.Duration

class LightningClient(
    path: String =
      "/var/lib/docker/volumes/generated_clightning_bitcoin_datadir/_data/lightning-rpc")
    extends MyJsonProtocol {

  private val atom = new AtomicInteger()

  private val client = new UnixDomainSocket(path)
  private val in     = new BufferedReader(new InputStreamReader(client.getInputStream))
  private val out    = new PrintWriter(client.getOutputStream, true)

  def call(method: String, paramsOpt: Option[String]): String =
    paramsOpt.map(call(method, _)) getOrElse call(method)

  def call(method: String, params: String*): String = {
    val req = Request(method = method, id = atom.getAndIncrement(), params = params.toArray)
    out.println(req.toJson)
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
  def autoCleanInvoice(cycleSeconds: Int = 3600, expiredBy: Int = 86400): String =
    call("autocleaninvoice", cycleSeconds.toString, expiredBy.toString)

  def pay(bolt11: String): String =
    call("pay", bolt11)

  def listPayments(bol11: String): String = listPayments(Some(bol11))

  /**
    * Show outgoing payments, regarding {bolt11} or {payment_hash} if set
    * @param bolt11
    * @return
    */
  def listPayments(bolt11: Option[String]): String =
    call("listpayments", bolt11)

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
  def listInvoices(invoice: Option[String] = None): String =
    call("listinvoices", invoice)

  /**
    * Show route to {id} for {msatoshi}, using {riskfactor} and optional {cltv} (default 9). If specified search from {fromid} otherwise use this node as source. Randomize the route with up to {fuzzpercent} (default 5.0). {exclude} an array of short-channel-id/direction (e.g. [ '564334x877x1/0', '564195x1292x0/1' ]) from consideration. Set the {maxhops} the route can take (default 20).
    */
  // def getRoute(id: String, msatoshi: Long, riskFactor: Any, cltv: Option[Int], fromId )
  /**
    * Get a new {bech32, p2sh-segwit} (or all) address to fund a channel (default is bech32)
    * @param addressType
    * @return
    */
  def newAddress(addressType: Option[String]) =
    call("newaddr", addressType)

  /**
    * Show all nodes in our local network view, filter on node {id}
    * if provided
    *
    * @param node
    * @return
    */
  def listNodes(node: Option[String]): String = call("listnodes", node)

  /**
    * Create an invoice for {msatoshi} with {label} and {description} with
    * optional {expiry} seconds (default 1 week)
    *
    * @param msatoshi
    * @param label
    * @param description
    * @param expiry
    */
  def invoice(msatoshi: Long,
              label: String,
              description: String,
              expiry: Long = Duration("1 week").toSeconds) =
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

  def payStatus(bolt11: Option[String]): String = call("paystatus", bolt11)

  def setChannelFee(id: Option[String], base: Option[Int], ppm: Option[Int]): String = {
    call("setchannelfee")
  }

  def listPeers: String = call("listpeers")

  def close(): Unit =
    client.close()

}

object LightningClientApp extends App {

  val client = new LightningClient()

//  client.autoCleanInvoice()

  client.listPayments(
    "lntb10n1pwmjymupp5avukczwr47wxvc9psxw46k0dmuv4t3lpke3xflgwtpjrcz2c6f7sdqqcqzpgxqyz5vqp6f3s2pj2f38safc75jssv6v4f7nfr436253u06dg4rlzn6a55sh7z0d7y549trhvfzzj488pzlenqwr2j3fjllvwgcwt54xclcvlsqpm62yge")
  client.pay(
    "lntb10n1pwmjymupp5avukczwr47wxvc9psxw46k0dmuv4t3lpke3xflgwtpjrcz2c6f7sdqqcqzpgxqyz5vqp6f3s2pj2f38safc75jssv6v4f7nfr436253u06dg4rlzn6a55sh7z0d7y549trhvfzzj488pzlenqwr2j3fjllvwgcwt54xclcvlsqpm62yge")
  client.listPeers
  client.listInvoices()
  client.getinfo
  client.help
  client.close()
}

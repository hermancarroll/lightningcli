package com.mathbot.lightning

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

sealed trait Req {

  def method: String
  def id: Int
}

case class Request(method: String, id: Int) extends Req
case class RequestWithParams(method: String, id: Int, params: Array[String]) extends Req

trait MyJsonProtocol extends DefaultJsonProtocol {
  implicit val formatRequestWithParams: RootJsonFormat[RequestWithParams] = jsonFormat3(RequestWithParams)
  implicit val formatRequest: RootJsonFormat[Request] = jsonFormat2(Request)
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


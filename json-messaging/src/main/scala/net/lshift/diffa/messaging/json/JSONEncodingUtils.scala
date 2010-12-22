/**
 * Copyright (C) 2010 LShift Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lshift.diffa.messaging.json

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTime
import org.codehaus.jackson.map.ObjectMapper
import collection.mutable.ListBuffer
import net.lshift.diffa.kernel.participants._
import org.codehaus.jettison.json.{JSONObject, JSONArray}
import scala.collection.JavaConversions.asList
import org.slf4j.LoggerFactory
import net.lshift.diffa.kernel.frontend._

/**
 * Standard utilities for JSON encoding.
 */
object JSONEncodingUtils {

  val log = LoggerFactory.getLogger(getClass)

  val dateEncoder = ISODateTimeFormat.dateTime
  val dateParser = ISODateTimeFormat.dateTimeParser

  val mapper = new ObjectMapper

  def maybeDateStr(date:DateTime) = {
    date match {
      case null => null
      case _    => date.toString(JSONEncodingUtils.dateEncoder)
    }
  }

  def maybeParseableDate(s:String) = {
    s match {
      case null => null
      case ""   => null
      case _    => JSONEncodingUtils.dateParser.parseDateTime(s)
    }
  }

  def serializeSimpleMessage(content:String) = serializeSimpleMap("message", content)

  def serializeEntityContent(content:String) = serializeSimpleMap("content", content)
  def deserializeEntityContent(wire:String) = deserializeSimpleMap(wire, "content")
  def serializeEntityContentRequest(id:String) = serializeSimpleMap("id", id)
  def deserializeEntityContentRequest(wire:String) = deserializeSimpleMap(wire, "id")
  def serializeEntityBodyRequest(body:String) = serializeSimpleMap("entityBody", body)
  def deserializeEntityBodyRequest(wire:String) = deserializeSimpleMap(wire, "entityBody")

  def deserializeWireResponse(wire:String) = mapper.readValue(wire, classOf[WireResponse])
  def serializeWireResponse(response:WireResponse) = mapper.writeValueAsString(response)

  def deserializeActionResult(wire:String) = mapper.readValue(wire, classOf[InvocationResult])
  def serializeActionResult(response:InvocationResult) = mapper.writeValueAsString(response)
  def deserializeActionRequest(wire:String) = mapper.readValue(wire, classOf[ActionInvocation])
  def serializeActionRequest(response:ActionInvocation) = mapper.writeValueAsString(response)

  def serialize(constraints:Seq[WireConstraint]) : String = mapper.writeValueAsString(constraints.toArray)
  def deserialize(wire:String) : Seq[WireConstraint]= mapper.readValue(wire, classOf[Array[WireConstraint]])

  def deserializeDigests(wire:String) : Seq[WireDigest] = mapper.readValue(wire, classOf[Array[WireDigest]])
  def serializeDigests(digests:Seq[WireDigest]) : String = mapper.writeValueAsString(digests.toArray)

//  @Deprecated
//  def deserializeEntityVersions(wire:String) : Seq[EntityVersion] = {
//    deserializeDigests(wire, true).asInstanceOf[Seq[EntityVersion]]
//  }
//
//  @Deprecated
//  def deserializeAggregateDigest(wire:String) : Seq[AggregateDigest] = {
//    deserializeDigests(wire, false).asInstanceOf[Seq[AggregateDigest]]
//  }

  def deserializeEvent(wire:String) : WireEvent = mapper.readValue(wire, classOf[WireEvent])
  def serializeEvent(event:WireEvent) = mapper.writeValueAsString(event)

  @Deprecated
  private def deserializeDigests(wire:String, readIdField:Boolean) : Seq[Digest] = {

    log.debug("Attempting to deserialize: " + wire)
    
    val buffer = ListBuffer[Digest]()
    val digestArray = new JSONArray(wire)
    for (val i <- 0 to digestArray.length - 1 ) {
      val jsonObject = digestArray.getJSONObject(i)
      val attributeArray = jsonObject.getJSONArray("attributes")
      val attributes = ListBuffer[String]()
      for (val i <- 0 to attributeArray.length - 1 ) {
        attributes += attributeArray.getString(i)
      }
      val lastUpdated = maybeParseableDate(jsonObject.getString("lastUpdated"))
      val digest = jsonObject.getString("digest")
      if (readIdField) {
        val id = jsonObject.getString("id")
        buffer += EntityVersion(id,attributes,lastUpdated,digest)
      }
      else {
        buffer += AggregateDigest(attributes,lastUpdated,digest)
      }
    }
    buffer
  }

//  def __serializeDigests(digests:Seq[Digest]) : String = {
//    log.debug("About to serialize: " + digests)
//    val digestArray = new JSONArray
//    digests.foreach(d => {
//      val digestObject = new JSONObject
//
//      if (d.isInstanceOf[EntityVersion]) {
//        digestObject.put("id", d.asInstanceOf[EntityVersion].id)
//      }
//
//      digestObject.put("attributes", asList(d.attributes))
//      digestObject.put("lastUpdated", d.lastUpdated)
//      digestObject.put("digest", d.digest)
//      digestArray.put(digestObject)
//    })
//    val wire = digestArray.toString
//    log.debug("Writing to wire: " + wire)
//    wire
//  }

  // Internal plumbing

  private def deserializeSimpleMap(wire:String, id:String) = mapper.readTree(wire).get(id).getTextValue

  private def serializeSimpleMap(id:String, content:String) = {
    val node = mapper.createObjectNode
    node.put(id, content)
    node.toString()
  }
  
}

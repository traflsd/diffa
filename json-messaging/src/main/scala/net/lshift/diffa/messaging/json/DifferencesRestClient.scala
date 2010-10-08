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

import org.joda.time.DateTime
import javax.ws.rs.core.MediaType
import com.sun.jersey.core.util.MultivaluedMapImpl
import com.sun.jersey.api.client.{WebResource, ClientResponse}
import net.lshift.diffa.kernel.client.DifferencesClient
import net.lshift.diffa.kernel.differencing.{SessionScope, SessionEvent}

/**
 * A RESTful client to start a matching session and poll for events from it.
 */
class DifferencesRestClient(serverRootUrl:String)
    extends AbstractRestClient(serverRootUrl, "rest/diffs/")
        with DifferencesClient {
  val supportsStreaming = false
  val supportsPolling = true

  /**
   * Creates a differencing session that can be polled for match events.
   * @return The id of the session containing the events
   */
  def subscribe(scope:SessionScope, start:DateTime, end:DateTime) : String = {
    val params = new MultivaluedMapImpl()
    params.add("start", f(start) )
    params.add("end", f(end) )
    params.add("pairs", scope.includedPairs.foldLeft("") {
      case ("", p)  => p
      case (acc, p) => acc + "," + p
    })

    val p = resource.path("sessions").queryParams(params)
    val response = p.post(classOf[ClientResponse])

    val status = response.getClientResponseStatus    

//    val prefix = url + "/" + context + "/" + root + "/sessions/"

    status.getStatusCode match {
      case 201     => response.getLocation.toString.split("/").last
      case x:Int   => throw new RuntimeException("HTTP " + x + " : " + status.getReasonPhrase)
    }
  }

  /**
   * This will poll the session identified by the id parameter for match events. 
   */
  def poll(sessionId:String) : Array[SessionEvent] =
    pollInternal(resource.path("sessions/" + sessionId))

  /**
   * This will poll the session identified by the id parameter for match events, retrieving any events occurring
   * since the given sequence id.
   */
  def poll(sessionId:String, sinceSeqId:String) : Array[SessionEvent] =
    pollInternal(resource.path("sessions/" + sessionId).queryParam("since", sinceSeqId))

  def eventDetail(sessionId:String, evtSeqId:String) : String = {
    val p = resource.path("events/" + sessionId + "/" + evtSeqId )
    val media = p.accept(MediaType.TEXT_PLAIN_TYPE)
    val response = media.get(classOf[ClientResponse])
    val status = response.getClientResponseStatus
    status.getStatusCode match {
      case 200 => response.getEntity(classOf[String])
      case x:Int   => throw new RuntimeException("HTTP " + x + " : " + status.getReasonPhrase)
    }
  }

  def f (d:DateTime) = {
    d match {
      case null => ""
      case _    => d.toString()
    }    
  }

  private def pollInternal(p:WebResource) : Array[SessionEvent] = {
    val media = p.accept(MediaType.APPLICATION_JSON_TYPE)
    val response = media.get(classOf[ClientResponse])

    val status = response.getClientResponseStatus

    status.getStatusCode match {
      case 200 => response.getEntity(classOf[Array[SessionEvent]])
      case x:Int   => throw new RuntimeException("HTTP " + x + " : " + status.getReasonPhrase)
    }

  }
}
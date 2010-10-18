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

package net.lshift.diffa.participants

import net.lshift.diffa.kernel.protocol.ProtocolHandler
import java.io.File
import net.lshift.diffa.messaging.json.{DownstreamParticipantHandler, UpstreamParticipantHandler}

/**
 * Application entry 
 */
object FilePair extends Application {
  val upstreamPort = 19194
  val downstreamPort = 19195

  ensureDir("target/upstream")
  ensureDir("target/downstream")

  val upstream = new UpstreamFileParticipant("a", "target/upstream", "http://localhost:19093/diffa-agent")
  val downstream = new DownstreamFileParticipant("b", "target/downstream", "http://localhost:19093/diffa-agent")

  forkServer(upstreamPort, new UpstreamParticipantHandler(upstream))
  forkServer(downstreamPort, new DownstreamParticipantHandler(downstream))

  private def ensureDir(path:String) {
    (new File(path)).mkdirs
  }

  private def forkServer(port:Int, handler:ProtocolHandler):Unit = {
    val server = new ParticipantRpcServer(port, handler)
    new Thread { override def run = server.start }.start
  }
}
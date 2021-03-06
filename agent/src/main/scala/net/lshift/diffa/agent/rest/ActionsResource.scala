/**
 * Copyright (C) 2010-2011 LShift Ltd.
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

package net.lshift.diffa.agent.rest

import javax.ws.rs._
import core.UriInfo
import net.lshift.diffa.docgen.annotations.{MandatoryParams, Description}
import net.lshift.diffa.docgen.annotations.MandatoryParams.MandatoryParam
import net.lshift.diffa.kernel.frontend.wire.InvocationResult
import net.lshift.diffa.kernel.client.{Actionable, ActionableRequest, ActionsClient}
import net.lshift.diffa.kernel.config.{DiffaPairRef, RepairAction}

class ActionsResource(val proxy:ActionsClient,
                      val domain:String,
                      val uriInfo:UriInfo) {

  @GET
  @Path("/{pairId}")
  @Produces(Array("application/json"))
  @Description("Returns a list of actions that can be invoked on the pair.")
  @MandatoryParams(Array(new MandatoryParam(name="pairId", datatype="string", description="The identifier of the pair")))
  def listActions(@PathParam("pairId") pairId: String,
                  @QueryParam("scope") scope: String): Array[Actionable] = (scope match {
    case RepairAction.ENTITY_SCOPE => proxy.listEntityScopedActions(DiffaPairRef(pairId,domain))
    case RepairAction.PAIR_SCOPE => proxy.listPairScopedActions(DiffaPairRef(pairId,domain))
    case _ => proxy.listActions(DiffaPairRef(pairId,domain))
  }).toArray

  @POST
  @Path("/{pairId}/{actionId}")
  @Produces(Array("application/json"))
  @Description("Invokes a pair-scoped action on the pair.")
  @MandatoryParams(Array(
    new MandatoryParam(name="pairId", datatype="string", description="The indentifier of the pair"),
    new MandatoryParam(name="actionId", datatype="string", description="The name of the action to invoke")
  ))
  def invokeAction(@PathParam("pairId") pairId:String,
                   @PathParam("actionId") actionId:String)
    = proxy.invoke(ActionableRequest(pairId, domain, actionId, null))

  @POST
  @Path("/{pairId}/{actionId}/{entityId}")
  @Produces(Array("application/json"))  
  @Description("Invokes an entity-scoped action on the pair.")
  @MandatoryParams(Array(
    new MandatoryParam(name="pairId", datatype="string", description="The indentifier of the pair"),
    new MandatoryParam(name="actionId", datatype="string", description="The name of the action to invoke"),
    new MandatoryParam(name="entityId", datatype="string", description="The id of the pair to perform the action on")
  ))
  def invokeAction(@PathParam("pairId") pairId:String,
                   @PathParam("actionId") actionId:String,
                   @PathParam("entityId") entityId:String) : InvocationResult
    = proxy.invoke(ActionableRequest(pairId, domain, actionId, entityId))

}
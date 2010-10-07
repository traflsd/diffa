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

package net.lshift.diffa.kernel.config

import net.lshift.diffa.kernel.util.SessionHelper._ // for 'SessionFactory.withSession'
import net.lshift.diffa.kernel.util.HibernateQueryUtils
import net.lshift.diffa.kernel._
import org.hibernate.{Session, Query, SessionFactory}
import org.slf4j.{Logger, LoggerFactory}
import scala.collection.JavaConversions._

class HibernateConfigStore(val sessionFactory: SessionFactory)
    extends ConfigStore
    with HibernateQueryUtils {
  private val log:Logger = LoggerFactory.getLogger(getClass)

  def createOrUpdateEndpoint(e: Endpoint): Unit = sessionFactory.withSession(s => s.saveOrUpdate(e))

  def deleteEndpoint(name: String): Unit = sessionFactory.withSession(s => {
    val endpoint = getEndpoint(s, name)

    // Delete children manually - Hibernate can't cascade on delete without a one-to-many relationship,
    // which would create an infinite loop in computing the hashCode of pairs and groups
    s.createQuery("DELETE FROM Pair WHERE upstream = :endpoint OR downstream = :endpoint").
            setEntity("endpoint", endpoint).executeUpdate
    
    s.delete(endpoint)
  })

  def listEndpoints: Seq[Endpoint] = sessionFactory.withSession(s => {
    listQuery[Endpoint](s, "allEndpoints", Map())
  })

  def createOrUpdatePair(p: PairDef): Unit = sessionFactory.withSession(s => {
    val up = getEndpoint(s, p.upstreamName)
    val down = getEndpoint(s, p.downstreamName)
    val group = getGroup(s, p.groupKey)
    val toUpdate = new Pair(p.pairKey, up, down, group, p.versionPolicyName, p.matchingTimeout)
    s.saveOrUpdate(toUpdate)
  })

  def deletePair(key: String): Unit = sessionFactory.withSession(s => {
    val pair = getPair(s, key)
    s.delete(pair)
  })

  def createOrUpdateGroup(g: PairGroup): Unit = sessionFactory.withSession( s => s.saveOrUpdate(g) )

  def deleteGroup(key: String): Unit = sessionFactory.withSession(s => {
    val group = getGroup(s, key)

    // Delete children manually - Hibernate can't cascade on delete without a one-to-many relationship,
    // which would create an infinite loop in computing the hashCode of pairs and groups
    s.createQuery("DELETE FROM Pair WHERE group = :group").setEntity("group", group).executeUpdate

    s.delete(group)
  })

  def listGroups: Seq[GroupContainer] = sessionFactory.withSession(s => {
    val groups = listQuery[PairGroup](s, "allGroups", Map())

    groups map (group => {
      val pairs = listQuery[Pair](s, "pairsByGroup", Map("group" -> group))

      new GroupContainer(group, pairs.toArray)
    })
  })


  // TODO Implement CRUD for users

  def createOrUpdateUser(u: User) = sessionFactory.withSession(s => s.saveOrUpdate(u))

  def deleteUser(name: String) = sessionFactory.withSession(s => {
    val user = getUser(s, name)
    s.delete(user)
  })
  
  def listUsers: Seq[User] = sessionFactory.withSession(s => {
    listQuery[User](s, "allUsers", Map())
  })

  def getPairsForEndpoint(epName:String):Seq[Pair] = sessionFactory.withSession(s => {
    val q = s.createQuery("SELECT p FROM Pair p WHERE p.upstream.name = :epName OR p.downstream.name = :epName")
    q.setParameter("epName", epName)

    q.list.map(_.asInstanceOf[Pair]).toSeq
  })

  def getEndpoint(name: String) = sessionFactory.withSession(s => getEndpoint(s, name))
  def getPair(key: String) = sessionFactory.withSession(s => getPair(s, key))
  def getGroup(key: String) = sessionFactory.withSession(s => getGroup(s, key))
  def getUser(name: String) : User = sessionFactory.withSession(s => getUser(s, name))

  private def getEndpoint(s: Session, name: String) = singleQuery[Endpoint](s, "endpointByName", Map("name" -> name), "endpoint")
  private def getUser(s: Session, name: String) = singleQuery[User](s, "userByName", Map("name" -> name), "user")
  private def getEndpointOpt(s: Session, name: String) = singleQueryOpt[Endpoint](s, "endpointByName", Map("name" -> name))
  private def getPair(s: Session, key: String) = singleQuery[Pair](s, "pairByKey", Map("key" -> key), "pair")
  private def getPairOpt(s: Session, key: String) = singleQueryOpt[Pair](s, "pairByKey", Map("key" -> key))
  private def getGroup(s: Session, key: String) = singleQuery[PairGroup](s, "groupByKey", Map("key" -> key), "group")
  private def getGroupOpt(s: Session, key: String) = singleQueryOpt[PairGroup](s, "groupByKey", Map("key" -> key))

}
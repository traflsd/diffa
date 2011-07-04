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

package net.lshift.diffa.kernel.config

import org.junit.Assert._
import org.hibernate.cfg.Configuration
import org.slf4j.{Logger, LoggerFactory}
import net.lshift.diffa.kernel.util.MissingObjectException
import org.hibernate.exception.ConstraintViolationException
import org.junit.{Test, Before}
import scala.collection.Map
import scala.collection.JavaConversions._
import org.joda.time.DateTime

class HibernateConfigStoreTest {
  private val configStore: ConfigStore = HibernateConfigStoreTest.configStore
  private val log:Logger = LoggerFactory.getLogger(getClass)

  val dateCategoryName = "bizDate"
  val dateCategoryLower = new DateTime(1982,4,5,12,13,9,0).toString()
  val dateCategoryUpper = new DateTime(1982,4,6,12,13,9,0).toString()
  val dateRangeCategoriesMap =
    Map(dateCategoryName ->  new RangeCategoryDescriptor("datetime",dateCategoryLower,dateCategoryUpper))

  val setCategoryValues = Set("a","b","c")
  val setCategoriesMap = Map(dateCategoryName ->  new SetCategoryDescriptor(setCategoryValues))

  val intCategoryName = "someInt"
  val stringCategoryName = "someString"

  val intCategoryType = "int"
  val intRangeCategoriesMap = Map(intCategoryName ->  new RangeCategoryDescriptor(intCategoryType))

  val stringPrefixCategoriesMap = Map(stringCategoryName -> new PrefixCategoryDescriptor(1, 3, 1))

  val upstream1 = new Endpoint("TEST_UPSTREAM", "TEST_UPSTREAM_URL", "application/json", null, null, true, dateRangeCategoriesMap)
  val upstream2 = new Endpoint("TEST_UPSTREAM_ALT", "TEST_UPSTREAM_URL_ALT", "application/json", null, null, true, setCategoriesMap)
  val downstream1 = new Endpoint("TEST_DOWNSTREAM", "TEST_DOWNSTREAM_URL", "application/json", null, null, true, intRangeCategoriesMap)
  val downstream2 = new Endpoint("TEST_DOWNSTREAM_ALT", "TEST_DOWNSTREAM_URL_ALT", "application/json", null, null, true, stringPrefixCategoriesMap)

  val groupKey1 = "TEST_GROUP"
  val group = new PairGroup(groupKey1)
  val versionPolicyName1 = "TEST_VPNAME"
  val matchingTimeout = 120
  val versionPolicyName2 = "TEST_VPNAME_ALT"
  val pairKey = "TEST_PAIR"
  val pairDef = new PairDef(pairKey, versionPolicyName1, matchingTimeout, upstream1.name,
    downstream1.name, groupKey1)
  val repairAction = new RepairAction(name="REPAIR_ACTION_NAME",
                                      scope=RepairAction.ENTITY_SCOPE,
                                      url="resend",
                                      pairKey=pairKey,
                                      escalate=false)

  val groupKey2 = "TEST_GROUP2"
  val upstreamRenamed = "TEST_UPSTREAM_RENAMED"
  val groupRenamed = "TEST_GROUP_RENAMED"
  val pairRenamed = "TEST_PAIR_RENAMED"

  val TEST_USER = User("foo","foo@bar.com")

  def declareAll() {
    configStore.createOrUpdateEndpoint(upstream1)
    configStore.createOrUpdateEndpoint(upstream2)
    configStore.createOrUpdateEndpoint(downstream1)
    configStore.createOrUpdateEndpoint(downstream2)
    configStore.createOrUpdateGroup(group)
    configStore.createOrUpdatePair(pairDef)
    configStore.createOrUpdateRepairAction(repairAction)
  }

  @Before
  def setUp: Unit = {
    HibernateConfigStoreTest.clearAllConfig
  }

  def exists (e:Endpoint, count:Int, offset:Int) : Unit = {
    val endpoints = configStore.listEndpoints
    assertEquals(count, endpoints.length)
    assertEquals(e.name, endpoints(offset).name)
    assertEquals(e.url, endpoints(offset).url)
    assertEquals(e.online, endpoints(offset).online)
  }

  def exists (e:Endpoint, count:Int) : Unit = exists(e, count, count - 1)

  @Test
  def testDeclare: Unit = {
    // Declare endpoints
    configStore.createOrUpdateEndpoint(upstream1)
    exists(upstream1, 1)

    configStore.createOrUpdateEndpoint(downstream1)
    exists(downstream1, 2)

    // Declare a group
    configStore.createOrUpdateGroup(group)
    val retrGroups = configStore.listGroups
    assertEquals(1, retrGroups.length)
    assertEquals(groupKey1, retrGroups.first.group.key)
    assertEquals(0, retrGroups.first.pairs.length)

    // Declare a pair
    configStore.createOrUpdatePair(pairDef)
    val retrGroups2 = configStore.listGroups
    assertEquals(1, retrGroups2.length)
    assertEquals(1, retrGroups2.first.pairs.length)
    val retrPair = retrGroups2.first.pairs.first
    assertEquals(pairKey, retrPair.key)
    assertEquals(upstream1.name, retrPair.upstream.name)
    assertEquals(downstream1.name, retrPair.downstream.name)
    assertEquals(groupKey1, retrPair.group.key)
    assertEquals(versionPolicyName1, retrPair.versionPolicyName)
    assertEquals(matchingTimeout, retrPair.matchingTimeout)

    // Declare a repair action
    configStore.createOrUpdateRepairAction(repairAction)
    val retrActions = configStore.listRepairActionsForPair(retrPair)
    assertEquals(1, retrActions.length)
    assertEquals(Some(pairKey), retrActions.headOption.map(_.pairKey))
  }

  @Test
  def testPairsAreValidatedBeforeUpdate() {
    // Declare endpoints
    configStore.createOrUpdateEndpoint(upstream1)
    exists(upstream1, 1)

    configStore.createOrUpdateEndpoint(downstream1)
    exists(downstream1, 2)

    // Declare a group
    configStore.createOrUpdateGroup(group)
    val retrGroups = configStore.listGroups
    assertEquals(1, retrGroups.length)
    assertEquals(groupKey1, retrGroups.first.group.key)
    assertEquals(0, retrGroups.first.pairs.length)

    pairDef.scanCronSpec = "invalid"

    try {
      configStore.createOrUpdatePair(pairDef)
      fail("Should have thrown ConfigValidationException")
    } catch {
      case ex:ConfigValidationException =>
        assertEquals("pair[key=TEST_PAIR]: Schedule 'invalid' is not a valid: Illegal characters for this position: 'INV'", ex.getMessage)
    }
  }

  @Test
  def testEndpointsWithSameURL {
    configStore.createOrUpdateEndpoint(upstream1)

    upstream2.url = upstream1.url
    configStore.createOrUpdateEndpoint(upstream2)

    exists(upstream1, 2, 0)
    exists(upstream2, 2, 1)
  }


  @Test
  def testUpdateEndpoint: Unit = {
    // Create endpoint
    configStore.createOrUpdateEndpoint(upstream1)
    exists(upstream1, 1)

    configStore.deleteEndpoint(upstream1.name)
    expectMissingObject("endpoint") {
      configStore.getEndpoint(upstream1.name)
    }
        
    // Change its name
    configStore.createOrUpdateEndpoint(Endpoint(upstreamRenamed, upstream1.url, "application/json", "changes", "application/json", true))

    val retrieved = configStore.getEndpoint(upstreamRenamed)
    assertEquals(upstreamRenamed, retrieved.name)
    assertTrue(retrieved.online)
  }

  @Test
  def testUpdatePair: Unit = {
    declareAll
    configStore.createOrUpdateGroup(new PairGroup(groupKey2))

    // Rename, change a few fields and swap endpoints by deleting and creating new
    configStore.deletePair(pairKey)
    expectMissingObject("pair") {
      configStore.getPair(pairKey)
    }

    configStore.createOrUpdatePair(new PairDef(pairRenamed, versionPolicyName2, Pair.NO_MATCHING,
      downstream1.name, upstream1.name, groupKey2, "0 0 * * * ?"))
    
    val retrieved = configStore.getPair(pairRenamed)
    assertEquals(pairRenamed, retrieved.key)
    assertEquals(downstream1.name, retrieved.upstream.name) // check endpoints are swapped
    assertEquals(upstream1.name, retrieved.downstream.name)
    assertEquals(versionPolicyName2, retrieved.versionPolicyName)
    assertEquals("0 0 * * * ?", retrieved.scanCronSpec)
    assertEquals(Pair.NO_MATCHING, retrieved.matchingTimeout)
  }

  @Test
  def testUpdateGroup: Unit = {
    // Create a group
    configStore.createOrUpdateGroup(group)

    // Rename it by deleting and re-creating
    configStore.deleteGroup(group.key)
    expectMissingObject("group") {
      configStore.getGroup(group.key)
    }
    configStore.createOrUpdateGroup(new PairGroup(groupRenamed))

    val retrieved = configStore.getGroup(groupRenamed)
    assertEquals(groupRenamed, retrieved.key)
  }

  @Test
  def testGetPairsInGroup {
    declareAll
    val pairKey2 = "TEST_PAIR_ALT"
    val pairDef2 = new PairDef(pairKey2, versionPolicyName2, matchingTimeout, upstream2.name,
                               downstream2.name, groupKey1)
    configStore.createOrUpdatePair(pairDef2)

    val pairs = configStore.getPairsInGroup(group)
    assertEquals(2, pairs.size)
    val pair1 = pairs.find(_.key == pairKey)
    val pair2 = pairs.find(_.key == pairKey2)
    assertTrue(pair1.isDefined)
    assertTrue(pair2.isDefined)
    assertEquals(group, pair1.get.group)
    assertEquals(group, pair2.get.group)
  }

  @Test
  def testDeleteEndpointCascade: Unit = {
    declareAll

    assertEquals(upstream1.name, configStore.getEndpoint(upstream1.name).name)
    configStore.deleteEndpoint(upstream1.name)
    expectMissingObject("endpoint") {
      configStore.getEndpoint(upstream1.name)
    }
    expectMissingObject("pair") {
      configStore.getPair(pairKey) // delete should cascade
    }
  }

  @Test
  def testDeletePair: Unit = {
    declareAll

    assertEquals(pairKey, configStore.getPair(pairKey).key)
    configStore.deletePair(pairKey)
    expectMissingObject("pair") {
      configStore.getPair(pairKey)
    }
  }

  @Test
  def testDeletePairCascade {
    declareAll()
    assertEquals(Some(repairAction.name), configStore.listRepairActions.headOption.map(_.name))
    configStore.deletePair(pairKey)
    expectMissingObject("repair action") {
      configStore.getRepairAction(repairAction.name, pairKey)
    }
  }

  @Test
  def testDeleteRepairAction {
    declareAll
    assertEquals(Some(repairAction.name), configStore.listRepairActions.headOption.map(_.name))

    configStore.deleteRepairAction(repairAction.name, pairKey)
    expectMissingObject("repair action") {
      configStore.getRepairAction(repairAction.name, pairKey)
    }
  }

  @Test
  def testDeleteGroupCascade: Unit = {
    declareAll

    assertEquals(groupKey1, configStore.getGroup(groupKey1).key)
    configStore.deleteGroup(groupKey1)
    expectMissingObject("group") {
      configStore.getGroup(groupKey1)
    }
    expectMissingObject("pair") {
      configStore.getPair(pairKey) // delete should cascade
    }
  }

  @Test
  def testDeleteMissing: Unit = {
    expectMissingObject("endpoint") {
      configStore.deleteEndpoint("MISSING_ENDPOINT")
    }

    expectMissingObject("pair") {
      configStore.deletePair("MISSING_PAIR")
    }

    expectMissingObject("group") {
      configStore.deleteGroup("MISSING_GROUP")
    }
  }

  @Test
  def testDeclarePairNullConstraints: Unit = {
    configStore.createOrUpdateEndpoint(upstream1)
    configStore.createOrUpdateEndpoint(downstream1)
    configStore.createOrUpdateGroup(group)

      // TODO: We should probably get an exception indicating that the constraint was null, not that the object
      //       we're linking to is missing.
    expectMissingObject("endpoint") {
      configStore.createOrUpdatePair(new PairDef(pairKey, versionPolicyName1, Pair.NO_MATCHING, null, downstream1.name, groupKey1))
    }
    expectMissingObject("endpoint") {
      configStore.createOrUpdatePair(new PairDef(pairKey, versionPolicyName1, Pair.NO_MATCHING, upstream1.name, null, groupKey1))
    }
    expectMissingObject("group") {
      configStore.createOrUpdatePair(new PairDef(pairKey, versionPolicyName1, Pair.NO_MATCHING, upstream1.name, downstream1.name, null))
    }
  }

  @Test
  def testRedeclareEndpointSucceeds = {
    configStore.createOrUpdateEndpoint(upstream1)
    configStore.createOrUpdateEndpoint(Endpoint(upstream1.name, "DIFFERENT_URL", "application/json", "changes", "application/json", false))
    assertEquals(1, configStore.listEndpoints.length)
    assertEquals("DIFFERENT_URL", configStore.getEndpoint(upstream1.name).url)
  }

  @Test
  def testQueryingForAssociatedPairsReturnsNothingForUnusedEndpoint {
    configStore.createOrUpdateEndpoint(upstream1)
    assertEquals(0, configStore.getPairsForEndpoint(upstream1.name).length)
  }

  @Test
  def testQueryingForAssociatedPairsReturnsPairUsingEndpointAsUpstream {
    configStore.createOrUpdateEndpoint(upstream1)
    configStore.createOrUpdateEndpoint(downstream1)
    configStore.createOrUpdateGroup(new PairGroup(groupKey1))
    configStore.createOrUpdatePair(new PairDef(pairKey, versionPolicyName2, Pair.NO_MATCHING,
                                               upstream1.name, downstream1.name, groupKey1))

    val res = configStore.getPairsForEndpoint(upstream1.name)
    assertEquals(1, res.length)
    assertEquals(pairKey, res(0).key)
  }

  @Test
  def testQueryingForAssociatedPairsReturnsPairUsingEndpointAsDownstream {
    configStore.createOrUpdateEndpoint(upstream1)
    configStore.createOrUpdateEndpoint(downstream1)
    configStore.createOrUpdateGroup(new PairGroup(groupKey1))
    configStore.createOrUpdatePair(new PairDef(pairKey, versionPolicyName2, Pair.NO_MATCHING,
                                               upstream1.name, downstream1.name, groupKey1))

    val res = configStore.getPairsForEndpoint(downstream1.name)
    assertEquals(1, res.length)
    assertEquals(pairKey, res(0).key)
  }

  @Test
  def rangeCategory = {
    declareAll
    val pair = configStore.getPair(pairKey)
    assertNotNull(pair.upstream.categories)
    assertNotNull(pair.downstream.categories)
    val us_descriptor = pair.upstream.categories(dateCategoryName).asInstanceOf[RangeCategoryDescriptor]
    val ds_descriptor = pair.downstream.categories(intCategoryName).asInstanceOf[RangeCategoryDescriptor]
    assertEquals("datetime", us_descriptor.dataType)
    assertEquals(intCategoryType, ds_descriptor.dataType)
    assertEquals(dateCategoryLower, us_descriptor.lower)
    assertEquals(dateCategoryUpper, us_descriptor.upper)
  }

  @Test
  def setCategory = {
    declareAll
    val endpoint = configStore.getEndpoint(upstream2.name)
    assertNotNull(endpoint.categories)
    val descriptor = endpoint.categories(dateCategoryName).asInstanceOf[SetCategoryDescriptor]
    assertEquals(setCategoryValues, descriptor.values.toSet)
  }

  @Test
  def prefixCategory = {
    declareAll
    val endpoint = configStore.getEndpoint(downstream2.name)
    assertNotNull(endpoint.categories)
    val descriptor = endpoint.categories(stringCategoryName).asInstanceOf[PrefixCategoryDescriptor]
    assertEquals(1, descriptor.prefixLength)
    assertEquals(3, descriptor.maxLength)
    assertEquals(1, descriptor.step)
  }

  @Test
  def testUser = {
    configStore.createOrUpdateUser(TEST_USER)
    val result = configStore.listUsers
    assertEquals(1, result.length)
    assertEquals(TEST_USER, result(0))
    val updated = User(TEST_USER.name, "somethingelse@bar.com")
    configStore.createOrUpdateUser(updated)
    val user = configStore.getUser(TEST_USER.name)
    assertEquals(updated, user)
    configStore.deleteUser(TEST_USER.name)
    val users = configStore.listUsers
    assertEquals(0, users.length)    
  }

  @Test
  def testApplyingDefaultConfigOption = {
    assertEquals("defaultVal", configStore.configOptionOrDefault("some.option", "defaultVal"))
  }

  @Test
  def testReturningNoneForConfigOption {
    assertEquals(None, configStore.maybeConfigOption("some.option"))
  }

  @Test
  def testRetrievingConfigOption = {
    configStore.setConfigOption("some.option2", "storedVal")
    assertEquals("storedVal", configStore.configOptionOrDefault("some.option2", "defaultVal"))
    assertEquals(Some("storedVal"), configStore.maybeConfigOption("some.option2"))
  }

  @Test
  def testUpdatingConfigOption = {
    configStore.setConfigOption("some.option3", "storedVal")
    configStore.setConfigOption("some.option3", "storedVal2")
    assertEquals("storedVal2", configStore.configOptionOrDefault("some.option3", "defaultVal"))
    assertEquals(Some("storedVal2"), configStore.maybeConfigOption("some.option3"))
  }

  @Test
  def testRemovingConfigOption = {
    configStore.setConfigOption("some.option3", "storedVal")
    configStore.clearConfigOption("some.option3")
    assertEquals("defaultVal", configStore.configOptionOrDefault("some.option3", "defaultVal"))
    assertEquals(None, configStore.maybeConfigOption("some.option3"))
  }

  @Test
  def testRetrievingAllOptions = {
    configStore.setConfigOption("some.option3", "storedVal")
    configStore.setConfigOption("some.option4", "storedVal3")
    assertEquals(Map("some.option3" -> "storedVal", "some.option4" -> "storedVal3"), configStore.allConfigOptions)
  }

  @Test
  def testRetrievingOptionsIgnoresInternalOptions = {
    configStore.setConfigOption("some.option3", "storedVal")
    configStore.setConfigOption("some.option4", "storedVal3", isInternal = true)
    assertEquals(Map("some.option3" -> "storedVal"), configStore.allConfigOptions)
  }

  @Test
  def testOptionCanBeUpdatedToBeInternal = {
    configStore.setConfigOption("some.option3", "storedVal")
    configStore.setConfigOption("some.option3", "storedVal3", isInternal = true)
    assertEquals(Map(), configStore.allConfigOptions)
  }

  private def expectMissingObject(name:String)(f: => Unit) {
    try {
      f
      fail("Expected MissingObjectException")
    } catch {
      case e:MissingObjectException => assertTrue(
        "Missing Object Exception for wrong object. Expected for " + name + ", got msg: " + e.getMessage,
        e.getMessage.contains(name))
    }
  }

  private def expectConstraintViolation(f: => Unit) {
    try {
      f
      fail("Expected ConstraintViolationException")
    } catch {
      case e:ConstraintViolationException => 
    }
  }
}

object HibernateConfigStoreTest {
  private val config =
      new Configuration().
        addResource("net/lshift/diffa/kernel/config/Config.hbm.xml").
        setProperty("hibernate.dialect", "org.hibernate.dialect.DerbyDialect").
        setProperty("hibernate.connection.url", "jdbc:derby:target/configStore;create=true").
        setProperty("hibernate.connection.driver_class", "org.apache.derby.jdbc.EmbeddedDriver")

  val sessionFactory = {
    val sf = config.buildSessionFactory
    (new HibernateConfigStorePreparationStep).prepare(sf, config)
    sf
  }

  val configStore = new HibernateConfigStore(sessionFactory)

  def clearAllConfig = {
    val s = sessionFactory.openSession
    s.createCriteria(classOf[User]).list.foreach(u => s.delete(u))
    s.createCriteria(classOf[Pair]).list.foreach(p => s.delete(p))
    s.createCriteria(classOf[PairGroup]).list.foreach(p => s.delete(p))
    s.createCriteria(classOf[Endpoint]).list.foreach(p => s.delete(p))
    s.createCriteria(classOf[ConfigOption]).list.foreach(o => s.delete(o))
    s.createCriteria(classOf[RepairAction]).list.foreach(s.delete)
    s.flush
    s.close
  }
}

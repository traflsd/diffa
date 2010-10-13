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

package net.lshift.diffa.kernel.util

import org.joda.time.DateTime

/**
 * Lots of constant dates for use in tests.
 *
 * The convention used is:
 *   START_2010 indicates the very start of 1-Jan-2010;
 *   END_2010 indicates the very last millisecond of 31-Dec-2010;
 *   JUL_2010 indicates the very start of 1-Jul-2010;
 *   JUL_8_2010 indicates the very start of 8-Jul-2010;
 *   END_JUL_2010 indicates the very last millisecond of 31-Jul-2010;
 *   AUG_11_2011_1 indicates a (at time of writing) random time during 11-Aug-2011;
 */
object Dates {
  val NOV_1_2008 = new DateTime(2008, 11, 1, 0, 0, 0, 0)
  val DEC_1_2008 = new DateTime(2008, 12, 1, 0, 0, 0, 0)

  val START_2009 = new DateTime(2009, 1, 1, 0, 0, 0, 0)
  val JAN_1_2009 = new DateTime(2009, 1, 1, 0, 0, 0, 0)
  val JUN_6_2009_1 = new DateTime(2009, 6, 6, 5, 14, 0, 0)
  val NOV_1_2009 = new DateTime(2009, 11, 1, 0, 0, 0, 0)
  val NOV_2_2009 = new DateTime(2009, 11, 1, 0, 0, 0, 0)
  val NOV_11_2009 = new DateTime(2009, 11, 30, 0, 0, 0, 0)
  val DEC_1_2009 = new DateTime(2009, 12, 1, 0, 0, 0, 0)
  val DEC_2_2009 = new DateTime(2009, 12, 2, 0, 0, 0, 0)
  val DEC_31_2009 = new DateTime(2009, 12, 31, 0, 0, 0, 0)

  val START_2010 = new DateTime(2010, 1, 1, 0, 0, 0, 0)
  val JAN_1_2010 = new DateTime(2010, 1, 1, 0, 0, 0, 0)
  val JAN_1_2010_1 = new DateTime(2010, 1, 1, 5, 40, 0, 0)
  val FEB_12_2010 = new DateTime(2010, 2, 12, 0, 0, 0, 0)
  val FEB_13_2010 = new DateTime(2010, 2, 13, 0, 0, 0, 0)
  val FEB_14_2010 = new DateTime(2010, 2, 14, 0, 0, 0, 0)
  val FEB_15_2010 = new DateTime(2010, 2, 15, 0, 0, 0, 0)
  val JUL_2010 = new DateTime(2010, 7, 1, 0, 0, 0, 0)
  val JUL_1_2010 = new DateTime(2010, 7, 1, 0, 0, 0, 0)
  val JUL_1_2010_1 = new DateTime(2010, 7, 1, 16, 12, 0, 0)
  val JUL_7_2010 = new DateTime(2010, 7, 7, 0, 0, 0, 0)
  val JUL_8_2010 = new DateTime(2010, 7, 8, 0, 0, 0, 0)
  val JUL_8_2010_1 = new DateTime(2010, 7, 8, 9, 17, 0, 0)
  val JUL_8_2010_2 = new DateTime(2010, 7, 8, 9, 22, 0, 0)
  val JUL_9_2010 = new DateTime(2010, 7, 9, 0, 0, 0, 0)
  val JUL_9_2010_1 = new DateTime(2010, 7, 9, 9, 17, 0, 0)
  val JUL_31_2010 = new DateTime(2010, 7, 31, 0, 0, 0, 0)
  val END_JUL_2010 = new DateTime(2010, 7, 31, 23, 59, 59, 999)
  val AUG_2010 = new DateTime(2010, 8, 1, 0, 0, 0, 0)
  val AUG_2_2010_1 = new DateTime(2010, 8, 2, 10, 15, 0, 0)
  val DEC_31_2010 = new DateTime(2010, 12, 31, 0, 0, 0, 0)
  val END_2010 = new DateTime(2010, 12, 31, 23, 59, 59, 999)

  val START_2011 = new DateTime(2011, 1, 1, 0, 0, 0, 0)
  val JAN_2_2011_1 = new DateTime(2011, 1, 2, 13, 15, 0, 0)
  val AUG_11_2011_1 = new DateTime(2011, 8, 11, 12, 13, 0, 0)
}
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

package net.lshift.diffa.kernel.participants

import org.joda.time.DateTime

/**
 * Describes a digest of version information. For an individual entity, the digest should be the version content and the
 * date should be the timestamp of the entity. For an aggregate entity, the digest should be the hashed aggregate of the
 * child entities within the given time range, and the date can be any representative time within the time period. The
 * key is generally ignored in the case of aggregates, but it is suggested that a readable variant of the date is used
 * to enhance understanding when reading digest lists.
 */
case class VersionDigest(val key:String, val date:DateTime, val lastUpdated:DateTime, val digest:String)
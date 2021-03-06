/**
 * Copyright (C) 2012 LShift Ltd.
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
package net.lshift.hibernate.migrations;

import org.junit.Test;

import java.sql.Connection;

import static net.lshift.hibernate.migrations.HibernateHelper.mockExecutablePreparedStatement;
import static org.easymock.EasyMock.*;

public class ScriptLoaderTest {
  @Test
  public void shouldExecuteScriptForDefaultDialectExtension() throws Exception {
    MigrationBuilder mb = new MigrationBuilder(HibernateHelper.configuration());
    mb.executeDatabaseScript("testscript", "net.lshift.hibernate.migrations.scripts");

    Connection conn = createStrictMock(Connection.class);
    expect(conn.prepareStatement("select * from foo;")).andReturn(mockExecutablePreparedStatement());
    replay(conn);

    mb.apply(conn);
    verify(conn);
  }

  @Test
  public void shouldExecuteScriptForOracleDialectExtension() throws Exception {
    MigrationBuilder mb = new MigrationBuilder(HibernateHelper.configuration(HibernateHelper.ORACLE_DIALECT));
    mb.executeDatabaseScript("testscript", "net.lshift.hibernate.migrations.scripts");

    Connection conn = createStrictMock(Connection.class);
    expect(conn.prepareStatement("select nextval() from dual;")).andReturn(mockExecutablePreparedStatement());
    replay(conn);

    mb.apply(conn);
    verify(conn);
  }
}

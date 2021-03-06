/*
 * Copyright 2011 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codefollower.yourbase.test.jdbc.ddl;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.codefollower.yourbase.api.Trigger;
import com.codefollower.yourbase.test.jdbc.TestBase;

public class CreateTriggerTest extends TestBase {

    public static class MyTrigger implements Trigger {

        @Override
        public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type)
                throws SQLException {
            System.out.println("schemaName=" + schemaName + " tableName=" + tableName);

        }

        @Override
        public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
            System.out.println("oldRow=" + oldRow + " newRow=" + newRow);
        }

        @Override
        public void close() throws SQLException {
            System.out.println("com.codefollower.yourbase.test.jdbc.ddl.CreateTriggerTest.MyInsertTrigger.close()");
        }

        @Override
        public void remove() throws SQLException {
            System.out.println("com.codefollower.yourbase.test.jdbc.ddl.CreateTriggerTest.MyInsertTrigger.remove()");
        }

    }

    @Test
    public void run() throws Exception {
        conn.setAutoCommit(false);

        //stmt.executeUpdate("DROP TABLE IF EXISTS CreateTriggerTest");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CreateTriggerTest(id int, name varchar(500))");

        stmt.executeUpdate("CREATE FORCE TRIGGER IF NOT EXISTS MyTrigger1"
                + " BEFORE INSERT,UPDATE,DELETE,SELECT,ROLLBACK ON CreateTriggerTest"
                + " QUEUE 10 NOWAIT CALL \"com.codefollower.yourbase.test.jdbc.ddl.CreateTriggerTest$MyTrigger\"");

        stmt.executeUpdate("CREATE TRIGGER IF NOT EXISTS MyTrigger2"
                + " AFTER INSERT,UPDATE,DELETE,SELECT,ROLLBACK ON CreateTriggerTest FOR EACH ROW"
                + " QUEUE 10 NOWAIT CALL \"com.codefollower.yourbase.test.jdbc.ddl.CreateTriggerTest$MyTrigger\"");

        //INSTEAD OF也是BEFORE类型
        stmt.executeUpdate("CREATE TRIGGER IF NOT EXISTS MyTrigger3"
                + " INSTEAD OF INSERT,UPDATE,DELETE,SELECT,ROLLBACK ON CreateTriggerTest FOR EACH ROW"
                + " QUEUE 10 NOWAIT CALL \"com.codefollower.yourbase.test.jdbc.ddl.CreateTriggerTest$MyTrigger\"");

        //这种语法可查入多条记录
        //null null
        //10 a
        //20 b
        stmt.executeUpdate("INSERT INTO CreateTriggerTest VALUES(DEFAULT, DEFAULT),(10, 'a'),(20, 'b')");

        sql = "select id,name from CreateTriggerTest";
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString(1) + " " + rs.getString(2));
        }

        conn.commit();
    }
}
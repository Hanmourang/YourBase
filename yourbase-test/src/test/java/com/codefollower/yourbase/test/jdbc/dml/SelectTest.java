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
package com.codefollower.yourbase.test.jdbc.dml;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import com.codefollower.yourbase.test.jdbc.TestBase;

public class SelectTest extends TestBase {
    @Test
    public void run() throws Exception {
        createTableIfNotExists("SelectTest");
        testInsert();
        testSelect();
        testAggregate();
    }

    void testInsert() throws Exception {
        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('01', 'a1', 'b', 51)");
        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('02', 'a1', 'b', 61)");
        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('03', 'a1', 'b', 61)");

        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('25', 'a2', 'b', 51)");
        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('26', 'a2', 'b', 61)");
        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('27', 'a2', 'b', 61)");

        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('50', 'a1', 'b', 12)");
        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('51', 'a2', 'b', 12)");
        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('52', 'a1', 'b', 12)");

        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('75', 'a1', 'b', 12)");
        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('76', 'a2', 'b', 12)");
        stmt.executeUpdate("INSERT INTO SelectTest(_rowkey_, f1, cf1.f2, cf2.f3) VALUES('77', 'a1', 'b', 12)");
    }

    void testSelect() throws Exception {
        sql = "select _rowkey_, f1, f2, cf2.f3 from SelectTest";
        printResultSet();

        sql = "select _rowkey_, f1, f2, cf2.f3 from SelectTest";
        stmt.setFetchSize(2);
        printResultSet();

        where();
        orderBy();
        groupBy();
    }

    private void where() throws Exception {
        sql = "SELECT count(*) FROM SelectTest WHERE f1 = 'a2'";
        assertEquals(5, getIntValue(1, true));

        sql = "SELECT count(*) FROM SelectTest WHERE _rowkey_ >= '50' AND f1 = 'a2'";
        assertEquals(2, getIntValue(1, true));

        sql = "SELECT count(*) FROM SelectTest WHERE _rowkey_ = '75' AND f1 = 'a2'";
        assertEquals(0, getIntValue(1, true));
    }

    private void orderBy() throws Exception {
        sql = "FROM SelectTest SELECT f1, f2, cf2.f3 ORDER BY f1 desc";
        printResultSet();
    }

    private void groupBy() throws Exception {
        sql = "SELECT f1, count(f1) FROM SelectTest GROUP BY f1";
        sql = "SELECT f1, count(f1) FROM SelectTest GROUP BY f1 HAVING f1 >= 'a1'";
        printResultSet();
    }

    void testAggregate() throws Exception {
        sql = "select sum(cf2.f3) from SelectTest";
        assertEquals(418, getIntValue(1, true));
        sql = "select avg(cf2.f3) from SelectTest";
        //因为cf2.f3是int，所以内部已进行4舍5入
        assertEquals(35.0, getDoubleValue(1, true), 0.2);
    }
}

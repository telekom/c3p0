package com.mchange.v2.c3p0.test.junit;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import junit.framework.*;
import com.mchange.v2.c3p0.*;

public final class ConnectionBoundariesJUnitTestCase extends TestCase {

  public static void setUpBeforeClass() throws Exception {
    DriverManager.registerDriver(new MockDriver());
  }

  boolean shouldMarkBoundaries( ComboPooledDataSource cpds ) {
      String msb = cpds.getMarkSessionBoundaries();
      return "always".equalsIgnoreCase(msb) || ("if-no-statement-cache".equalsIgnoreCase(msb) && cpds.getMaxStatements() == 0 && cpds.getMaxStatementsPerConnection() == 0);
  }

  public void testConnectionWithBoundaries() throws Exception {
    DriverManager.registerDriver(new MockDriver());
    ComboPooledDataSource cpds = new ComboPooledDataSource();
    cpds.setDriverClass("com.mchange.v2.c3p0.test.junit.MockDriver"); // loads the jdbc driver
    cpds.setJdbcUrl("mock:driver@with-request-boundaries");
    cpds.setUser("dbuser");
    cpds.setPassword("dbpassword");

    if (shouldMarkBoundaries(cpds)) {
      assertEquals("Expect no calls to beginRequest before getConnection", 0, MockDriver.beginRequestCount.get());
      assertEquals("Expect no calls to endRequest before getConnection", 0, MockDriver.endRequestCount.get());
      Connection con = cpds.getConnection();
      assertEquals("Expect a call to beginRequest after getConnection", 1, MockDriver.beginRequestCount.get());
      assertEquals("Expect no calls to endRequest after getConnection", 0, MockDriver.endRequestCount.get());
      con.close();
      assertEquals("Expect a call to beginRequest after close", 1, MockDriver.beginRequestCount.get());
      assertEquals("Expect a call to endRequest after close", 1, MockDriver.endRequestCount.get());

      assertEquals("Expect no calls to beginRequest before getConnection", 1, MockDriver.beginRequestCount.get());
      assertEquals("Expect no calls to endRequest before getConnection", 1, MockDriver.endRequestCount.get());
      con = cpds.getConnection();
      assertEquals("Expect a call to beginRequest after getConnection", 2, MockDriver.beginRequestCount.get());
      assertEquals("Expect no calls to endRequest after getConnection", 1, MockDriver.endRequestCount.get());
      con.close();
      assertEquals("Expect a call to beginRequest after close", 2, MockDriver.beginRequestCount.get());
      assertEquals("Expect a call to endRequest after close", 2, MockDriver.endRequestCount.get());
    }
    else {
      assertEquals("Expect no calls to beginRequest before getConnection", 0, MockDriver.beginRequestCount.get());
      assertEquals("Expect no calls to endRequest before getConnection", 0, MockDriver.endRequestCount.get());
      Connection con = cpds.getConnection();
      assertEquals("Expect no call to beginRequest after getConnection with boundary marking suppressed", 0, MockDriver.beginRequestCount.get());
      con.close();
      assertEquals("Expect no call to endRequest after close with boundary marking suppressed", 0, MockDriver.endRequestCount.get());
    }
  }

  public void testConnectionWithoutBoundaries() throws Exception {
    DriverManager.registerDriver(new MockDriver());
    ComboPooledDataSource cpds = new ComboPooledDataSource();
    cpds.setDriverClass("com.mchange.v2.c3p0.test.junit.MockDriver"); // loads the jdbc driver
    cpds.setJdbcUrl("test:driver@without-request-boundaries");
    cpds.setUser("dbuser");
    cpds.setPassword("dbpassword");

    assertEquals("Expect no calls to beginRequest before getConnection", 0, MockDriver.beginRequestCount.get());
    assertEquals("Expect no calls to endRequest before getConnection", 0, MockDriver.endRequestCount.get());
    Connection con = cpds.getConnection();
    assertEquals("Expect no calls to beginRequest after getConnection", 0, MockDriver.beginRequestCount.get());
    assertEquals("Expect no call to endRequest after getConnection", 0, MockDriver.endRequestCount.get());
    con.close();
    assertEquals("Expect no calls to beginRequest after close", 0, MockDriver.beginRequestCount.get());
    assertEquals("Expect no calls to endRequest after close", 0, MockDriver.endRequestCount.get());

    assertEquals("Expect no calls to beginRequest before getConnection", 0, MockDriver.beginRequestCount.get());
    assertEquals("Expect no calls to endRequest before getConnection", 0, MockDriver.endRequestCount.get());
    con = cpds.getConnection();
    assertEquals("Expect no calls to beginRequest after getConnection", 0, MockDriver.beginRequestCount.get());
    assertEquals("Expect no call to endRequest after getConnection", 0, MockDriver.endRequestCount.get());
    con.close();
    assertEquals("Expect no calls to beginRequest after close", 0, MockDriver.beginRequestCount.get());
    assertEquals("Expect no calls to endRequest after close", 0, MockDriver.endRequestCount.get());
  }

}

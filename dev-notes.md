# dev-notes

* It looks like throwing of PropertyVetoException may not be implemented for ostensibly constrained parameters:
  - `connectionTesterClassName` (should classname params be constrained?)
  - `contextClassLoaderSource`
  - `markSessionBoundaries`
  - `taskRunnerFactoryClassName` (should classname params be constrained?)
  - `userOverridesAsString`
  It might be disruptive to "fix" this now, as installations may have configs that are stable despite
  bad (vetoable) settings.

  If class name properties should be constrained, probably so too ought be
  - `connectionCustomizerClassName`

* When testing against intentionally chaotic configs, we occasionally see, at debug level FINE:
  ```plaintext
  2024-05-06@00:53:59 [FINE] [com.mchange.v2.c3p0.impl.NewPooledConnection] An exception occurred while resetting a closed Connection. Invalidating Connection. 
  org.postgresql.util.PSQLException: This connection has been closed.
      at org.postgresql.jdbc.PgConnection.checkClosed(PgConnection.java:993)
      at org.postgresql.jdbc.PgConnection.getAutoCommit(PgConnection.java:953)
      at com.mchange.v2.c3p0.impl.C3P0ImplUtils.resetTxnState(C3P0ImplUtils.java:207)
      at com.mchange.v2.c3p0.impl.NewPooledConnection.reset(NewPooledConnection.java:429)
      at com.mchange.v2.c3p0.impl.NewPooledConnection.markClosedProxyConnection(NewPooledConnection.java:385)
      at com.mchange.v2.c3p0.impl.NewProxyConnection.close(NewProxyConnection.java:2150)
      at com.mchange.v1.db.sql.ConnectionUtils.attemptClose(ConnectionUtils.java:53)
      at com.mchange.v2.c3p0.impl.ConnectionTesterConnectionTestPath.testPooledConnection(ConnectionTesterConnectionTestPath.java:123)
      at com.mchange.v2.c3p0.impl.C3P0PooledConnectionPool$1PooledConnectionResourcePoolManager.testPooledConnection(C3P0PooledConnectionPool.java:784)
      at com.mchange.v2.c3p0.impl.C3P0PooledConnectionPool$1PooledConnectionResourcePoolManager.testPooledConnection(C3P0PooledConnectionPool.java:775)
      at com.mchange.v2.c3p0.impl.C3P0PooledConnectionPool$1PooledConnectionResourcePoolManager.refurbishIdleResource(C3P0PooledConnectionPool.java:743)
      at com.mchange.v2.resourcepool.BasicResourcePool$AsyncTestIdleResourceTask.run(BasicResourcePool.java:2048)
      at com.mchange.v2.async.ThreadPoolAsynchronousRunner$PoolThread.run(ThreadPoolAsynchronousRunner.java:696)
  ```
  It appears to be harmless -- just a debug notification that a physical Connection has already been closed
  when it is tested as an idle resource. Still, why?
  - It looks like it's just multiple tests backed up in the Connection pool, the first one causes the Connection to be closed,
    a second one sees the Connection already closed. It appears no longer to appear if all of the following params are not set
    * maxConnectionAge
    * maxIdleTime
    * maxIdleTimeExcessConnections
    That is, if Connections aren't timed out, the condition does not appear.

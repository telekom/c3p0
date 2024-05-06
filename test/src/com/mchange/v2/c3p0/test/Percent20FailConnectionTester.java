package com.mchange.v2.c3p0.test;

import java.sql.Connection;
import com.mchange.v2.c3p0.QueryConnectionTester;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;

public final class Percent20FailConnectionTester extends PercentXXFailConnectionTester
{
    public Percent20FailConnectionTester()
    { super(20); }
}


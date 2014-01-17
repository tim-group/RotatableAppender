package com.timgroup.log.appender;

import org.junit.Test;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;

import static org.junit.Assert.assertEquals;

public class NoopRollingPolicyTest {

    private final String logPath = "/var/log/nsa/prism.log";
    private final FileAppender<?> appender = new FileAppender<Void>();

    {
        appender.setFile(logPath);
    }

    @Test
    public void rolloverDoesNothing() throws Exception {
        new NoopRollingPolicy().rollover();
    }

    @Test
    public void theActiveFileNameIsWhateverTheAppenderHas() throws Exception {
        RollingPolicy policy = new NoopRollingPolicy();
        policy.setParent(appender);

        assertEquals(logPath, policy.getActiveFileName());
    }

}

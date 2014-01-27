package com.timgroup.log.appender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import ch.qos.logback.core.encoder.EchoEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RotatableFileAppenderTest {

    @Test
    public void triggeringPolicyIsRotationBased() throws Exception {
        assertInstanceOf(RotationBasedTriggeringPolicy.class, new RotatableFileAppender<Void>().getTriggeringPolicy());
    }

    @Test
    public void rollingPolicyIsNoop() throws Exception {
        assertInstanceOf(NoopRollingPolicy.class, new RotatableFileAppender<Void>().getRollingPolicy());
    }

    private void assertInstanceOf(Class<?> clazz, Object object) {
        assertNotNull(object);
        assertEquals(clazz, object.getClass());
    }

    private File logFile;
    private File rotatedLogFile;

    @Test
    public void itActuallyWorks() throws Exception {
        logFile = File.createTempFile(getClass().getSimpleName(), ".log");
        rotatedLogFile = new File(logFile.getPath() + ".1");

        RotatableFileAppender<String> appender = new RotatableFileAppender<String>();
        appender.setEncoder(new EchoEncoder<String>());
        appender.setFile(logFile.getPath());
        appender.setCheckCachePeriod(0);

        appender.start();

        appender.doAppend("event 1");
        logFile.renameTo(rotatedLogFile);
        appender.doAppend("event 2");

        appender.stop();

        assertEquals("event 1", readTheSingleLineWhichComprises(rotatedLogFile));
        assertEquals("event 2", readTheSingleLineWhichComprises(logFile));
    }

    private String readTheSingleLineWhichComprises(File file) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        String end = reader.readLine();
        assertNull(end);
        return line;
    }

    @After
    public void deleteTempFiles() {
        if (logFile != null) logFile.delete();
        if (rotatedLogFile != null) rotatedLogFile.delete();
    }

}

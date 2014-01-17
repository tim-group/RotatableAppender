package com.timgroup.log.appender;

import java.io.File;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import static com.timgroup.test.FailingImplementation.STRICT;

public class RotationBasedTriggeringPolicyTest {

    private final File existingFile = mock(File.class, STRICT);

    @Test
    public void doesNotTriggerIfFileExists() throws Exception {
        doReturn(true).when(existingFile).exists();

        boolean triggering = new RotationBasedTriggeringPolicy<Void>().isTriggeringEvent(existingFile, null);

        assertFalse(triggering);
    }

    @Test
    public void doesTriggersIfFileDoesNotExist() throws Exception {
        doReturn(false).when(existingFile).exists();

        boolean triggering = new RotationBasedTriggeringPolicy<Void>().isTriggeringEvent(existingFile, null);

        assertTrue(triggering);
    }

}

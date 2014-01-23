package com.timgroup.log.appender;

import java.io.File;

import org.junit.Test;

import com.timgroup.log.appender.RotationBasedTriggeringPolicy.Clock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static com.timgroup.test.FailingImplementation.STRICT;

public class RotationBasedTriggeringPolicyTest {

    private final File existingFile = mock(File.class, STRICT);
    private final Clock clock = mock(Clock.class, STRICT);

    @Test
    public void doesNotTriggerIfFileExists() throws Exception {
        doReturn(true).when(existingFile).exists();

        boolean triggering = new RotationBasedTriggeringPolicy<Void>().isTriggeringEvent(existingFile, null);

        assertFalse(triggering);
    }

    @Test
    public void triggersIfFileDoesNotExist() throws Exception {
        doReturn(false).when(existingFile).exists();

        boolean triggering = new RotationBasedTriggeringPolicy<Void>().isTriggeringEvent(existingFile, null);

        assertTrue(triggering);
    }

    @Test
    public void doesNotCheckForFileExistenceIfHasPositivelyCheckedRecently() throws Exception {
        RotationBasedTriggeringPolicy<Void> policy = new RotationBasedTriggeringPolicy<Void>(clock);

        doReturn(1000000L).when(clock).currentTimeMillis();

        checkNotTriggeredByExistingFile(policy);

        doReturn(1000999L).when(clock).currentTimeMillis();

        checkNotTriggeredByExistingFile(policy);

        verify(existingFile, times(1)).exists();
    }

    @Test
    public void checksForFileExistenceIfHasNotCheckedRecently() throws Exception {
        RotationBasedTriggeringPolicy<Void> policy = new RotationBasedTriggeringPolicy<Void>(clock);

        doReturn(1000000L).when(clock).currentTimeMillis();

        checkNotTriggeredByExistingFile(policy);

        doReturn(1001000L).when(clock).currentTimeMillis();

        checkNotTriggeredByExistingFile(policy);

        verify(existingFile, times(2)).exists();
    }

    @Test
    public void checksForFileExistenceIfHasNegativelyCheckedRecently() throws Exception {
        RotationBasedTriggeringPolicy<Void> policy = new RotationBasedTriggeringPolicy<Void>(clock);

        doReturn(1000000L).when(clock).currentTimeMillis();

        checkTriggeredByMissingFile(policy);

        doReturn(1000001L).when(clock).currentTimeMillis();

        checkNotTriggeredByExistingFile(policy);

        verify(existingFile, times(2)).exists();
    }

    private void checkNotTriggeredByExistingFile(RotationBasedTriggeringPolicy<Void> policy) {
        doReturn(true).when(existingFile).exists();

        boolean triggering = policy.isTriggeringEvent(existingFile, null);

        assertFalse(triggering);
    }

    private void checkTriggeredByMissingFile(RotationBasedTriggeringPolicy<Void> policy) {
        doReturn(false).when(existingFile).exists();

        boolean triggering = policy.isTriggeringEvent(existingFile, null);

        assertTrue(triggering);
    }

}

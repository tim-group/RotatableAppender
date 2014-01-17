package com.timgroup.log.appender;

import java.io.File;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;

public class RotationBasedTriggeringPolicy<E> extends TriggeringPolicyBase<E> {

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        return !activeFile.exists();
    }

}

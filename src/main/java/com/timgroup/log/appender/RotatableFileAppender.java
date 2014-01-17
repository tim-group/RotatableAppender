package com.timgroup.log.appender;

import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;

public class RotatableFileAppender<E> extends RollingFileAppender<E> {

    public RotatableFileAppender() {
        setTriggeringPolicy(new RotationBasedTriggeringPolicy<E>());
        RollingPolicy rollingPolicy = new NoopRollingPolicy();
        setRollingPolicy(rollingPolicy);
        rollingPolicy.setParent(this);
    }

}

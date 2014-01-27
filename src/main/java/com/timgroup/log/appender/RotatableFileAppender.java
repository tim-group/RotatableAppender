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

    public long getCheckCachePeriod() {
        return getTriggeringPolicy().getCheckCachePeriod();
    }

    public void setCheckCachePeriod(long checkCachePeriod) {
        getTriggeringPolicy().setCheckCachePeriod(checkCachePeriod);
    }

    @Override
    public RotationBasedTriggeringPolicy<E> getTriggeringPolicy() {
        return (RotationBasedTriggeringPolicy<E>) super.getTriggeringPolicy();
    }

}

package com.timgroup.log.appender;

import ch.qos.logback.core.rolling.RollingPolicyBase;
import ch.qos.logback.core.rolling.RolloverFailure;

public class NoopRollingPolicy extends RollingPolicyBase {

    @Override
    public void rollover() throws RolloverFailure {}

    @Override
    public String getActiveFileName() {
        return getParentsRawFileProperty();
    }

}

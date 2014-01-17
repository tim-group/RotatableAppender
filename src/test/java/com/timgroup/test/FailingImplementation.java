package com.timgroup.test;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class FailingImplementation implements Answer<Void> {
    public static final FailingImplementation STRICT = new FailingImplementation();

    @Override
    public Void answer(InvocationOnMock invocation) throws Throwable {
        throw new AssertionError("unmocked methods should not be called");
    }
}

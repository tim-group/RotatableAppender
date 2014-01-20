What is it?
===========

`RotatableAppender`_ is an `appender`_ for the popular logging library `Logback`_ that allows log files to be externally rotated. That is, rather than attempting to rotate (or, in Logback terms, 'roll') log files itself, it simply detects rotation by some other agent, and opens a fresh log file.

The goal of RotatableAppender is to make applications interoperate nicely with `logrotate`_. It may be useful if using other log managers.

RotatableAppender will probably not help on Windows, as it requires log rotator to rename open files.

How do i build it?
==================

With `Gradle`_. We are using version 1.7. To build, simply do::

    gradle clean build

This builds a jar file in ``build/libs``. To use this in other projects, you might like to install it in your local Maven repository::

    gradle install

How do i use it?
================

Configuration
-------------

Firstly, get the library onto your classpath. If using a multiple-classloader setup, ensure that Logrotate is visible to Logback.

Then, write an appender declaration like this in your `Logback configuration file`_::

    <appender name="FILE" class="com.timgroup.log.appender.RotatableFileAppender">
        <file>/var/log/foo.log</file>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

The ``file`` element should contain the name of the log file you want to write to, as usual.

The RotatableFileAppender class does not really contain any logic, but rather is simply a convenience for creating a perfectly normal `RollingFileAppender`_ which is configured with two specific policies: a RotationBasedTriggeringPolicy, which detects that the log file has disappeared (as it does when renamed) and opens a fresh one, and a NoopRollingPolicy, which does nothing whatsoever. If you wish, you may configure an appender to use these directly::

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/foo.log</file>
        <rollingPolicy class="com.timgroup.log.appender.NoopRollingPolicy" />
        <triggeringPolicy class="com.timgroup.log.appender.RotationBasedTriggeringPolicy" />
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

Operation
---------

Now, simply rotate your log file!

You can easily test your application's behaviour with respect to rolling by moving its log file manually. You should see that it immediately closes the file, and opens a fresh one at the same location.

If you wish to use logrotate to handle rotation, you will need a file in ``/etc/logrotate.d/`` that looks a bit like this::

    /var/log/foo.log {
        daily
        rotate 7
        compress
        
        delaycompress
        
        # these directives are the default, but they're important, so let's be explicit! 
        nocopytruncate
        nocreate
    }

There are three directives in there which bear further explanation:

delaycompress
    necessary (if you are using compression) because the application may still be writing to the log file immediately after it is moved, and before it has checked to see if it has been moved; compressing the file before the application has finished writing to it would risk losing events
nocopytruncate
    not necessary; the whole point of RotatableAppender is to avoid having to use this dangerous and menacing directive
nocreate
    RotatableAppender works by detecting that the log file has disappeared, so if logrotate were to create a new log file, RotatableAppender would never detect rotation


.. _RotatableAppender: https://github.com/youdevise/RotatableAppender
.. _appender: http://logback.qos.ch/manual/appenders.html
.. _Logback: http://logback.qos.ch/
.. _logrotate: https://fedorahosted.org/logrotate/
.. _Gradle: http://www.gradle.org/
.. _Logback configuration file: http://logback.qos.ch/manual/configuration.html
.. _RollingFileAppender: http://logback.qos.ch/manual/appenders.html#RollingFileAppender

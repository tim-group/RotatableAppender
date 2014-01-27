What is it?
===========

`RotatableAppender`_ provides an `appender`_ for the popular logging library `Logback`_ that allows log files to be externally rotated. That is, rather than attempting to rotate (or, in Logback terms, 'roll') log files itself, it simply detects rotation by some external agent, and responds by opening a fresh log file.

The goal of RotatableAppender is to make applications interoperate nicely with `logrotate`_. It may be useful if using other log managers.

How does it work?
=================

RotatableAppender provides a RotationBasedTriggeringPolicy which triggers Logback's internal rotation when it detects that the log file has been externally rotated. As a convenience, it also provides a NoopRollingPolicy which makes Logback's internal rotation simply re-open the log file, and a RotatableFileAppender which wires both of these into a normal `RollingFileAppender`_.

A RollingFileAppender writes to a stream connected to a log file at a specified path, and delegates to a TriggeringPolicy to decide when to rotate the file. The RotationBasedTriggeringPolicy keeps an eye on whether a file actually exists at the specified path. If at any point it finds that such a file does not exist, it instructs Logback to perform rotation. When Logback performs rotation, it closes the current stream, delegates to a RollingPolicy to perform any specific rotation actions, then opens a new stream to a fresh file at the specified path. The NoopRollingPolicy does nothing, and thus reduces the internal rotation process to simply closing and reopening the log file.

External rotation of the log file begins by renaming it; such renaming manifests as the file ceasing to exist at the specified path, and so triggers the above response. The response is also triggered by the log file simply being deleted, if you want an even simpler log mangement strategy.

The sequence of events in a typical rotation is therefore:

1. The external agent renames the log file
2. RotationBasedTriggeringPolicy detects that the file has disappeared and triggers rotation
3. Logback closes the stream to the log file
4. Logback delegates to the NoopRollingPolicy, which does nothing
5. Logback creates and opens a stream to a fresh log file

This process relies absolutely on the renaming of files which have open filehandles attached to them. It essentially uses the indirection between files and directory entries as a means of communication between the log rotator and the application. This is always allowed on Unix (probably). It is only allowed on Windows if all open filehandles to the file were opened in the `appropriate sharing mode`_, with the ``dwShareMode`` parameter set to ``FILE_SHARE_DELETE``; you may want to check whether your Java implementation opens files with this parameter.

The natural way to monitor the existence of the log file would be to check for its existence before every write of a log event. This could lead to a rather high rate of existence checks; since each existence check may involve a system call, this could introduce undesirable performance overhead. Therefore, RotatableAppender caches the results of the existence check for a limited period of time; it will only check for existence if there has not been a positive check for existence within this period. The period is configured by the ``checkCachePeriod`` property of the RotationBasedTriggeringPolicy, which for convenience is duplicated on the RollingFileAppender. This period is measured in milliseconds, and defaults to 1000 ms. A consequence is that, by default, an application using RotatableAppender is not guaranteed to respond to rotation of its log file until one second after the file has been moved.

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

The log file existence check cache period can be configured with a ``checkCachePeriod`` element::

    <appender name="FILE" class="com.timgroup.log.appender.RotatableFileAppender">
        <file>/var/log/foo.log</file>
        <checkCachePeriod>100</checkCachePeriod>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

If you wish, you may instead configure an appender to use the RotationBasedTriggeringPolicy and NoopRollingPolicy directly::

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/foo.log</file>
        <rollingPolicy class="com.timgroup.log.appender.NoopRollingPolicy" />
        <triggeringPolicy class="com.timgroup.log.appender.RotationBasedTriggeringPolicy">
            <checkCachePeriod>100</checkCachePeriod>
        </triggeringPolicy>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

Operation
---------

Now, simply rotate your log file!

You can easily test your application's behaviour with respect to rotation by moving its log file manually. You should see that it immediately closes the file, and opens a fresh one at the same location.

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
.. _RollingFileAppender: http://logback.qos.ch/manual/appenders.html#RollingFileAppender
.. _appropriate sharing mode: http://msdn.microsoft.com/en-us/library/aa363858%28v=vs.85%29.aspx
.. _Gradle: http://www.gradle.org/
.. _Logback configuration file: http://logback.qos.ch/manual/configuration.html

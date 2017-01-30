package com.mobgen.halo.android.framework.common.helpers.logger;

import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static com.mobgen.halo.android.framework.mock.FrameworkMock.createSameThreadFrameworkWithFilePolicy;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class LoggerTest extends HaloRobolectricTest {

    @Test
    public void loggerTest() {
        //Cover the halog creation
        DefaultLogFormatter formatter = mock(DefaultLogFormatter.class);
        final boolean[] result = new boolean[6];
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                result[0] = true;
                return null;
            }
        }).when(formatter).d(getClass(), "Test debug");
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                result[1] = true;
                return null;
            }
        }).when(formatter).i(getClass(), "Test info");
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                result[2] = true;
                return null;
            }
        }).when(formatter).w(getClass(), "Test warning");
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                result[3] = true;
                return null;
            }
        }).when(formatter).wtf(getClass(), "Test wtf");
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                result[4] = true;
                return null;
            }
        }).when(formatter).v(getClass(), "Test verbose");
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                result[5] = true;
                return null;
            }
        }).when(formatter).e(getClass(), "Test error");

        Halog.overrideFormatter(formatter);
        Halog.printDebug(false);
        Halog.d(getClass(), "Test debug");
        Halog.wtf(getClass(), "Test wtf");
        Halog.v(getClass(), "Test verbose");

        for (boolean resultElem : result) {
            Assert.assertFalse(resultElem);
        }

        Halog.printDebug(true);
        Halog.d(getClass(), "Test debug");
        Halog.wtf(getClass(), "Test wtf");
        Halog.v(getClass(), "Test verbose");
        Halog.i(getClass(), "Test info");
        Halog.e(getClass(), "Test error");
        Halog.w(getClass(), "Test warning");

        for (boolean resultElem : result) {
            Assert.assertTrue(resultElem);
        }
    }

    @Test
    public void coverDefaultFormatterTest() {
        DefaultLogFormatter formatter = new DefaultLogFormatter();
        formatter.d(getClass(), "Test");
        formatter.i(getClass(), "Test");
        formatter.w(getClass(), "Test");
        formatter.wtf(getClass(), "Test");
        formatter.e(getClass(), "Test");
        formatter.v(getClass(), "Test");
    }

    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Constructor<Halog> constructor = Halog.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void isPrintingTest() {
        Halog.printDebug(true);
        Assert.assertTrue(Halog.isPrinting());
        Halog.printDebug(false);
        Assert.assertFalse(Halog.isPrinting());
    }

    @Test
    public void thatDoNotPrintToFileWithouDebugEnabled() {
        Halog.printDebug(false);
        HaloFramework framework = createSameThreadFrameworkWithFilePolicy("http://mytest",-1);
        Halog.setupPrintLogToFile(framework);
        Halog.v(LoggerTest.class,"test msg");
        assertThat(Halog.getLogFilePath()).isNull();
    }

    @Test
    public void thatDoNotPrintToFile() {
        Halog.printDebug(true);
        HaloFramework framework = createSameThreadFrameworkWithFilePolicy("http://mytest",1);
        Halog.setupPrintLogToFile(framework);
        Halog.v(LoggerTest.class,"test msg");
        assertThat(Halog.getLogFilePath()).isNull();
    }

    @Test
    public void thatPrintToSingleFile() {
        Halog.printDebug(true);
        HaloFramework framework = createSameThreadFrameworkWithFilePolicy("http://mytest",2);
        Halog.setupPrintLogToFile(framework);
        Halog.v(LoggerTest.class,"test msg");
        assertThat(Halog.getLogFilePath()).isNotNull();
    }

    @Test
    public void thatPrintToMultipleFiles() {
        Halog.printDebug(true);
        HaloFramework framework = createSameThreadFrameworkWithFilePolicy("http://mytest",3);
        Halog.setupPrintLogToFile(framework);
        Halog.v(LoggerTest.class,"test msg");
        assertThat(Halog.getLogFilePath()).isNotNull();
    }
}

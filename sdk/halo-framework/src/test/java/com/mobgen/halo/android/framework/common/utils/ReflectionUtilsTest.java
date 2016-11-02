package com.mobgen.halo.android.framework.common.utils;


import com.mobgen.halo.android.framework.common.exceptions.HaloReflectionException;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Table;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import junit.framework.Assert;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ReflectionUtilsTest extends HaloRobolectricTest {

    @Test
    public void toClassTest() {
        Class clazz = ReflectionUtils.toClass("failed.Class");
        Assert.assertEquals(null, clazz);

        clazz = ReflectionUtils.toClass(getClass().getCanonicalName());
        Assert.assertEquals(getClass(), clazz);

        clazz = ReflectionUtils.toClass(null);
        Assert.assertEquals(null, clazz);
    }

    @Test
    public void newInstanceTest() {
        PublicConstructorNoParam instanciable = ReflectionUtils.newInstance(PublicConstructorNoParam.class);
        Assert.assertNotNull(instanciable);
    }

    @Test
    public void newInstancePrivateConstructorTest() {
        Assert.assertNotNull(ReflectionUtils.newInstance(PrivateConstructor.class));
    }

    @Test(expected = HaloReflectionException.class)
    public void newInstanceExceptionTest() {
        ReflectionUtils.newInstance(PublicConstructorParam.class);
    }

    @Test
    public void getConstructor() {
        Assert.assertNotNull(ReflectionUtils.getConstructor(PublicConstructorParam.class, PublicConstructorNoParam.class));
    }

    @Test(expected = HaloReflectionException.class)
    public void getConstructorExceptionTest() {
        Assert.assertNotNull(ReflectionUtils.getConstructor(PrivateConstructor.class, PublicConstructorNoParam.class));
    }

    @Test
    public void buildTest() {
        PublicConstructorParam instance = ReflectionUtils.build(ReflectionUtils.getConstructor(PublicConstructorParam.class, PublicConstructorNoParam.class), new PublicConstructorNoParam());
        Assert.assertNotNull(instance);
    }

    @Test(expected = HaloReflectionException.class)
    public void buildExceptionTest() {
        ReflectionUtils.build(ReflectionUtils.getConstructor(PublicConstructorParam.class, PublicConstructorNoParam.class), new PrivateConstructor());
    }

    @Test(expected = HaloReflectionException.class)
    public void buildException2Test() {
        ReflectionUtils.build(ReflectionUtils.getConstructor(PrivateConstructorParameter.class, PublicConstructorNoParam.class), new PrivateConstructor());
    }

    @Test
    public void thatCanGetReflectionField(){
        Field [] fields = ReflectionUtils.getFields(PrivateConstructorWithField.class,false);
        assertThat(fields).isNotNull();
    }

    public static class PublicConstructorNoParam {
        public PublicConstructorNoParam() {
        }
    }

    public static class PublicConstructorParam {
        public PublicConstructorParam(PublicConstructorNoParam instanciable) {
        }
    }

    public static class PrivateConstructor {
        private PrivateConstructor() {
        }

    }

    public static class PrivateConstructorParameter {
        private PrivateConstructorParameter(PublicConstructorNoParam instance) {
        }

    }

    public static class PrivateConstructorWithField{
        private String mField;
        public PrivateConstructorWithField(String field) {
            mField = field;
        }
    }
}

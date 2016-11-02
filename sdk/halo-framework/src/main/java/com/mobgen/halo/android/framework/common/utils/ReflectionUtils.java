package com.mobgen.halo.android.framework.common.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloReflectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Utils to perform some reflection in the Halo framework.
 */
public final class ReflectionUtils {

    /**
     * Utils classes have empty constructors to avoid new instances.
     */
    private ReflectionUtils() {
    }

    /**
     * Checks if a given class is available in the classpath.
     *
     * @param name The name of the class.
     * @return The class or null if it is not available.
     */
    @Api(2.0)
    @Nullable
    public static Class<?> toClass(@Nullable String name) {
        if (!TextUtils.isEmpty(name)) {
            try {
                return Class.forName(classNameToInnerFilter(name.trim()));
            } catch (ClassNotFoundException ignored) {
            }
        }
        return null;
    }

    /**
     * Creates a new instance of a given class.
     *
     * @param clazz The class to be created.
     * @return The new instance created.
     */
    @Api(2.0)
    @Nullable
    public static <T> T newInstance(@NonNull Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            if (constructor != null) {
                constructor.setAccessible(true);
                return constructor.newInstance();
            }
            return null;
        } catch (InstantiationException e) {
            throw new HaloReflectionException("The instance for " + clazz.getCanonicalName() + " can not be created.", e);
        } catch (IllegalAccessException e) {
            throw new HaloReflectionException("The default constructor of " + clazz.getCanonicalName() + " is not visible to create the instance.", e);
        } catch (NoSuchMethodException e) {
            throw new HaloReflectionException("No default constructor found for " + clazz.getCanonicalName() + ".", e);
        } catch (InvocationTargetException e) {
            throw new HaloReflectionException("The default constructor could not be invoked on " + clazz.getCanonicalName() + ".", e);
        }
    }

    /**
     * Provides the constructor for a given class and a given parameter.
     *
     * @param constructedClass      The class that contains the constructor.
     * @param innerConstructorClass The inner constructor class.
     * @return The constructor obtained.
     */
    @Api(2.0)
    @NonNull
    public static <T, D> Constructor<T> getConstructor(@NonNull Class<T> constructedClass, @NonNull Class<D> innerConstructorClass) {
        try {
            return constructedClass.getConstructor(innerConstructorClass);
        } catch (NoSuchMethodException e) {
            throw new HaloReflectionException("The constructor for " + constructedClass.getCanonicalName() + " and first parameter of class " + innerConstructorClass.getCanonicalName() + " does not exist.", e);
        }
    }

    /**
     * Creates an instance given a constructor and the data passed to it.
     *
     * @param constructor The constructor used to install the instance.
     * @param data        The data to pass to the constructor.
     * @return The built instance.
     */
    @Api(2.0)
    @NonNull
    public static <T> T build(@NonNull Constructor<T> constructor, @Nullable Object... data) {
        try {
            return constructor.newInstance(data);
        } catch (InstantiationException e) {
            throw new HaloReflectionException("It is not possible to create an instance of class " + constructor.getDeclaringClass().getCanonicalName() + ".", e);
        } catch (IllegalAccessException e) {
            throw new HaloReflectionException("The constructor is not accessible for class " + constructor.getDeclaringClass().getCanonicalName() + ".", e);
        } catch (InvocationTargetException e) {
            throw new HaloReflectionException("Exception thrown by invoked constructor for class " + constructor.getDeclaringClass().getCanonicalName() + ".", e);
        } catch (IllegalArgumentException e) {
            throw new HaloReflectionException("Exception thrown for invalid parameters in class " + constructor.getDeclaringClass().getCanonicalName() + ".", e);
        }
    }

    /**
     * Filters the name of a class to be an inner class from '.' to '$'.
     *
     * @param className The class name.
     * @return The filtered string.
     */
    @Api(2.0)
    @NonNull
    public static String classNameToInnerFilter(@NonNull String className) {
        String[] filteredArr = className.split("\\.");
        int i = 0;
        for (String filteredToken : filteredArr) {
            if (Character.isUpperCase(filteredToken.charAt(0))) {
                break;
            }
            i++;
        }
        return TextUtils.join(".", Arrays.copyOfRange(filteredArr, 0, i)) + "." +
                TextUtils.join("$", Arrays.copyOfRange(filteredArr, i, filteredArr.length));
    }

    /**
     * Provides the fields of a class.
     *
     * @param clazz        The fields.
     * @param bringParents True to get the parent fields too.
     * @return The field array.
     */
    @Api(2.0)
    @NonNull
    public static Field[] getFields(@NonNull Class clazz, boolean bringParents) {
        return bringParents ? clazz.getFields() : clazz.getDeclaredFields();
    }

    /**
     * Provides the value of a field.
     *
     * @param field     The value of the field.
     * @param reference The referenced object.
     * @param clazz     The casting class.
     * @return The value.
     */
    @Api(2.0)
    @NonNull
    public static <T> T valueOf(Field field, Object reference, Class<T> clazz) {
        try {
            return clazz.cast(field.get(reference));
        } catch (IllegalAccessException e) {
            throw new HaloReflectionException("The value for the field " + field.getName() + " could not be retrieved.", e);
        }
    }
}

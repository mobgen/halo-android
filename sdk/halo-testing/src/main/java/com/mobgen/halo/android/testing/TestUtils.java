package com.mobgen.halo.android.testing;

import android.app.Service;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.assertj.core.internal.Paths;
import org.jetbrains.annotations.NotNull;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.Charset;

import static junit.framework.Assert.assertTrue;

/**
 * Contains some useful helper methods for the application tests.
 */
public class TestUtils {

    /**
     * Mocks the test coverage for enums that generate the values and valueOf methods. This
     * is needed to get 100% on code coverage with Jacoco on those methods that are created synthetically.
     *
     * @param enumClass The enum class to partially test cover.
     */
    public static void shallowEnumCodeCoverage(Class<? extends Enum<?>> enumClass) {
        try {
            for (Object object : (Object[]) enumClass.getMethod("values").invoke(null)) {
                enumClass.getMethod("valueOf", String.class).invoke(null, object.toString());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test the parcel reading and writing.
     *
     * @param item    The item to parcel.
     * @param creator The creator for the item.
     */
    public static <T extends Parcelable> T testParcel(T item, Parcelable.Creator<T> creator) {
        // Obtain a Parcel object and write the parcelable object to it:
        Parcel parcel = Parcel.obtain();
        item.writeToParcel(parcel, 0);

        // After you're done with writing, you need to reset the parcel for reading:
        parcel.setDataPosition(0);

        // Reconstruct object from parcel and asserts:
        return creator.createFromParcel(parcel);
    }

    /**
     * Tries to instanciate a private constructor item.
     *
     * @param clazz The clazz.
     * @throws Exception error.
     */
    @SuppressWarnings("unchecked")
    public static void testPrivateConstructor(Class clazz) throws Exception {
        Constructor constructor = clazz.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * Flushes the robolectric thread until the end of the posted items.
     *
     * @return looped times.
     */
    public static int flushMainThread() {
        int amount = 0;
        while ((ShadowApplication.getInstance().getForegroundThreadScheduler().advanceToLastPostedRunnable())) {
            //Posting to the next item
            amount++;
        }
        return amount;
    }

    /**
     * Prepares the context of a service mocked.
     * @param instance The instance to prepare.
     * @param context The context prepared.
     * @return The instance prepared.
     */
    public static <T extends Service> T prepareService(T instance, @NotNull Context context) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = instance.getClass()
                .getSuperclass().getSuperclass().getSuperclass().getSuperclass()
                .getDeclaredMethod("attachBaseContext", Context.class);
        method.setAccessible(true);
        method.invoke(instance, RuntimeEnvironment.application);
        method.setAccessible(false);
        return instance;
    }
}

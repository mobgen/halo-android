-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.squareup.picasso.**
-dontwarn rx.internal.util.unsafe.**


-dontwarn com.crittercism.**
-keep public class com.crittercism.**
-keepclassmembers public class com.crittercism.**
{
    *;
}

-keep class com.facebook.stetho.** { *; }
-dontwarn com.facebook.stetho.**

-dontwarn com.google.android.gms.**

# Allow the icons from iconify
-keep class com.joanzapata.** { *; }

-keep public class * extends android.support.v7.widget.RecyclerView$LayoutManager {
    public <init>(...);
}

-dontwarn icepick.**
-keep class icepick.** { *; }
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}

-dontwarn uk.co.senab.photoview.**
-keep class uk.co.senab.photoview.** { *;}

# HALO
-keepattributes Signature
-keep class com.mobgen.halo.android.sdk.core.internal.storage.HaloManagerContract$* {*;}
-keep class com.mobgen.halo.android.sdk.content.storage.HaloContentContract$* {*;}
-keep class com.mobgen.halo.android.translations.api.HaloTranslationsContract$* {*;}
-keep class com.mobgen.halo.android.framework.storage.database.dsl.annotations.* {*;}
-keep class android.support.v7.widget.SearchView { *; }
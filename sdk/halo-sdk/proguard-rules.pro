# Keep the exceptions
-keepattributes Exceptions
-keepattributes EnclosingMethod
-keepparameternames
-keep class com.mobgen.halo.android.sdk.BuildConfig { *; }

# Keep logan square info
-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }
-keepparameternames
-keepattributes EnclosingMethod

-keep class com.mobgen.halo.android.content.processor.**
-dontwarn com.mobgen.halo.android.content.processor.**

-keep class com.mobgen.halo.android.content.generated.**
-dontwarn com.mobgen.halo.android.content.generated.**

-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }
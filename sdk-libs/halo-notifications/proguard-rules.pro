-keepparameternames
-keepattributes EnclosingMethod

-dontwarn com.squareup.picasso.**
-dontwarn com.bumptech.glide.**

-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }
-keepattributes Signature
-keepattributes EnclosingMethod
-keep class com.codyy.erpsportal.** { *; }
-dontwarn com.codyy.erpsportal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @com.codyy.erpsportal.urlbuilder.* <fields>;
}

-keepclasseswithmembernames class * {
    @com.codyy.erpsportal.urlbuilder.* <methods>;
}
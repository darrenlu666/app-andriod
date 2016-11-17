-keep class com.artifex.mupdfdemo.** {*;}
-dontwarn com.artifex.mupdfdemo.**
#保持 native 方法不被混淆
 -keepclasseswithmembernames class * {
     native <methods>;
 }
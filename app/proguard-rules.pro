# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.kongzue.dialogx.**{*;}
-keep class cn.lliiooll.ppbuff.startup.**{*;}
-keep class cn.lliiooll.ppbuff.ffmpeg.**{*;}
-keep class cn.lliiooll.ppbuff.utils.Natives{*;}
-keep class com.tencent.**{*;}
-keep class * implements java.io.Serializable { *; }
-keep class com.android.** {*;}
-keep class androidx.** {*;}
-keep class android.** {*;}
-keep class kotlin.** {*;}
-keep class kotlinx.** {*;}
-keep class cn.hutool.** {*;}
-keep class io.luckypray.** {*;}
-keep class com.google.** {*;}
-keep class com.github.** {*;}
-keep class com.microsoft.** {*;}
-keep class org.** {*;}
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

-obfuscationdictionary MRules.txt
-classobfuscationdictionary MRules.txt
-packageobfuscationdictionary MRules.txt
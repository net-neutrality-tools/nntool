# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/stephan/Development/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

-dontwarn com.google.**
-dontwarn com.zafaco.**

-keeppackagenames org.jsoup.nodes

-keepattributes SourceFile,LineNumberTable
-keep class com.zafaco.**
-keepclassmembers class com.zafaco.** { *; }
-keep enum com.zafaco.**
-keepclassmembers enum com.zafaco.** { *; }
-keep interface com.zafaco.**
-keepclassmembers interface com.zafaco.** { *; }

-keepattributes Signature

# Google Map
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }

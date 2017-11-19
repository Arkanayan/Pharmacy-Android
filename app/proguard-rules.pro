# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/arka/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class **$$ViewBinder { *; }
-keep class com.mobsandgeeks.saripaar.** {*;}
-keep @com.mobsandgeeks.saripaar.annotation.ValidateUsing class * {*;}

-keepattributes Signature

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-dontwarn okio.**
-dontwarn java.lang.invoke.*

-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}


# RxJava 0.21

-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}

-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

-keepattributes *Annotation*
-keep public class * extends android.support.design.widget.CoordinatorLayout.Behavior { *; }
-keep public class * extends android.support.design.widget.ViewOffsetBehavior { *; }

-keep class org.apache.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**

# Only necessary if you downloaded the SDK jar directly instead of from maven.
-keep class com.shaded.fasterxml.jackson.** { *; }


-keep class rx.android.plugins.RxAndroidPlugins {
    public static <methods>;
}

-keep class rx.android.plugins.RxAndroidSchedulersHook {
    public static <methods>;
}

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keepclassmembers class com.ahanapharmacy.app.models.User {
    *;
}

-keepclassmembers class com.ahanapharmacy.app.models.Address {
    *;
}

-keepclassmembers class com.ahanapharmacy.app.models.Order {
    *;
}

-keepclassmembers class com.ahanapharmacy.app.models.Order$** {
    *;
}

-keep class com.google.firebase.database.core.** {
    *;
}

-keepattributes *Annotation*

-keep class com.google.firebase.quickstart.database.viewholder.** {
    *;
}

-keepclassmembers class com.google.firebase.quickstart.database.models.** {
    *;
}

-keep class com.cloudinary.** { *; }

-keep class com.crashlytics.android.** { *; }
-keep class io.fabric.sdk.android.** { *; }
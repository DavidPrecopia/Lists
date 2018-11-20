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


# You can keep Instant Run enabled with these options.
-keep public class com.google.firebase.provider.FirebaseInitProvider
-keep public class com.firebase.ui.auth.data.client.AuthUiInitProvider
-keep public class com.google.firebase.iid.FirebaseInstanceIdService
-keep public class androidx.lifecycle.ProcessLifecycleOwnerInitializer
-keep public class com.example.david.lists.ui.view.MainActivity
-keep public class com.example.david.lists.widget.configactivity.WidgetConfigActivity
-keep public class com.example.david.lists.widget.MyAppWidgetProvider
-keepclassmembers class com.example.david.lists.data.datamodel.** { *; }

# Per Firebase Authentication
-keepattributes Signature
-keepattributes *Annotation*
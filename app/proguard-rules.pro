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

# TODO REMOVE POST REPO REFACTOR
-keepclassmembers class com.example.david.lists.data.datamodel.** { *; }
-keep public class com.google.firebase.provider.FirebaseInitProvider
-keep public class com.firebase.ui.authResult.data.client.AuthUiInitProvider
-keep public class com.google.firebase.iid.FirebaseInstanceIdService
# Per Firebase Authentication
-keepattributes Signature
-keepattributes *Annotation*


-keep public class androidx.lifecycle.ProcessLifecycleOwnerInitializer
-keep public class com.example.david.lists.ui.MainActivity
-keep public class com.example.david.lists.widget.configactivity.WidgetConfigView
-keep public class com.example.david.lists.widget.view.MyAppWidgetProvider
-keep interface com.example.david.lists.view.preferences.dialogs.ConfirmAccountDeletionDialog$DeleteAccountListener
-keep public class com.crashlytics.android.CrashlyticsInitProvider

# Perserve file paths and lines numbers for crash reports - per Fabric Crashlytics.
-keepattributes SourceFile,LineNumberTable
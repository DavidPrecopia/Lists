-keepclassmembers class com.precopia.androiddata.datamodel.** { *; }
-keepclassmembers class com.precopia.domain.datamodel.** { *; }
-keep public class com.google.firebase.provider.FirebaseInitProvider
-keep public class com.firebase.ui.authResult.data.client.AuthUiInitProvider
-keep public class com.google.firebase.iid.FirebaseInstanceIdService

# Per Firebase Authentication
-keepattributes Signature
-keepattributes *Annotation*
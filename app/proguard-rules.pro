# ─── ProGuard / R8 Rules for Schedulyy (Tuntas) ───────────────────────────────

# Preserve Annotations & Signatures
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# 1. Android Jetpack & Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# 2. Room Database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class com.pemmob.schedulyy.data.local.entity.** { *; }
-keep class com.pemmob.schedulyy.data.local.dao.** { *; }

# 3. WorkManager
-keep class * extends androidx.work.Worker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class com.pemmob.schedulyy.data.notification.** { *; }

# 4. Kotlinx Serialization
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# 5. Domain Models & Gemini DTOs
-keep class com.pemmob.schedulyy.domain.model.** { *; }
-keep class com.pemmob.schedulyy.data.remote.gemini.** { *; }
-keep class com.pemmob.schedulyy.data.backup.** { *; }

# 6. Google Tink Keystore
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**

# 7. Hilt & Dagger
-keep class * extends androidx.lifecycle.ViewModel
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }

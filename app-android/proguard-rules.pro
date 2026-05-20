# Add Application specific rules here.

# General config
-keepattributes InnerClasses,Signature,Exceptions,EnclosingMethod,SourceFile,LineNumberTable,*Annotation*
-renamesourcefileattribute SourceFile

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
}

# Exceptions
-keepclasseswithmembernames class * extends java.lang.Throwable

# org.jetbrains.kotlinx:kotlinx-serialization
-dontwarn kotlinx.serialization.KSerializer
-dontwarn kotlinx.serialization.Serializable
-keep @kotlinx.serialization.Serializable class * {*;}

# androidx.security:security-crypto
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.CheckReturnValue
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.concurrent.GuardedBy

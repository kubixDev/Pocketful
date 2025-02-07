plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.kubixdev.pocketful"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kubixdev.pocketful"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.annotation:annotation:1.9.1")
    implementation("androidx.annotation:annotation-jvm:1.9.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("com.google.firebase:firebase-firestore:25.1.2")
    implementation("com.google.firebase:firebase-auth:23.2.0")
    implementation("com.google.firebase:firebase-storage:21.0.1")

    implementation("com.google.mlkit:text-recognition:16.0.1")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
plugins {
    alias(libs.plugins.android.application)
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.android") version "1.8.20"
    id("org.jetbrains.kotlin.kapt") version "1.8.20"
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.movision"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.movision"
        minSdk = 28
        targetSdk = 34
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

    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // RxJava3
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.11.0")
    // Navigation
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-preferences-rxjava3:1.1.1")
    // Paging
    val paging_version = "3.3.2"
    implementation("androidx.paging:paging-runtime:$paging_version")
    implementation("androidx.paging:paging-rxjava3:$paging_version") // RxJava3 support
    // Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version") // RxJava3 support
    implementation("androidx.room:room-paging:$room_version") // Paging 3 support
    // AutoDispose
    implementation ("com.uber.autodispose2:autodispose:2.2.1")
    implementation("com.uber.autodispose2:autodispose-lifecycle:2.2.1")
    implementation("com.uber.autodispose2:autodispose-androidx-lifecycle:2.2.1")
}

kapt {
    correctErrorTypes = true
}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.shoparoo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.shoparoo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    //ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-compose-android:2.8.6")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Slider
    implementation("com.google.accompanist:accompanist-pager:0.36.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.32.0") // For indicators
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //coil for images
    implementation("io.coil-kt:coil-compose:2.0.0")

    //SwipeToRefresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.28.0") // Check for the latest version

    //google icons
    implementation("androidx.compose.material:material-icons-extended-android:1.7.2")


    //rating bar
    implementation("com.github.SmartToolFactory:Compose-RatingBar:1.1.1")
   implementation("com.github.SmartToolFactory:Compose-RatingBar:1.0.0")
    implementation ("com.github.a914-gowtham:compose-ratingbar:1.3.12")

    //Animation
    implementation("com.airbnb.android:lottie-compose:6.0.0")
    implementation("com.github.stevdza-san:OneTapCompose:1.0.14")

    //implementation ("com.paymob.accept:acceptsdk:2.3.5")

    implementation ("androidx.webkit:webkit:1.5.0")

    // OkHttp Logging (optional, for debugging)
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.github.mumayank:AirLocation:2.5.2")

    implementation("com.github.KeepSafe:TapTargetView:1.13.2")





}
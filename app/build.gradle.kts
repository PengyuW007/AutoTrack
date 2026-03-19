plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.areonedev.autotrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.areonedev.autotrack"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }

    dependencies {
        // ... existing dependencies ...

        // Add this line for ActivityTestRule
        androidTestImplementation("androidx.test:rules:1.6.1")

        // Optional: If you want the modern ActivityScenarioRule as well
        androidTestImplementation("androidx.test.ext:junit:1.2.1")
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.protolite.well.known.types)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
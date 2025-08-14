plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.huchadigital"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.huchadigital"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions { 
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15" 
    }
} 

kotlin { 
    jvmToolchain(17)
}

dependencies {
    
    implementation(libs.androidx.core.ktx) 

    implementation(libs.com.google.code.gson)

    
    implementation(libs.androidx.lifecycle.runtime.ktx) 

    
    implementation(libs.androidx.activity.compose) 

    
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.appcompat)
    debugImplementation(libs.androidx.ui.tooling)

    
    implementation(libs.androidx.navigation.compose)

    implementation("androidx.biometric:biometric-ktx:1.4.0-alpha02")
    implementation("com.google.android.material:material:1.12.0") // Corrected version
    
    implementation(libs.androidx.constraintlayout) 
}
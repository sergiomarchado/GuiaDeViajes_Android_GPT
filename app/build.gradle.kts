import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    alias(libs.plugins.google.gms.google.services)
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains" && requested.name == "annotations") {
            useVersion("23.0.0")
        }
    }
    exclude(group = "com.intellij", module = "annotations")
}

android {
    namespace = "com.example.guiadeviajes_android_gpt"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.guiadeviajes_android_gpt"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 1) Cargar keys desde local.properties
        val localProperties = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }

        val apiKey = localProperties["API_KEY"] as String
        val apiKeyG = localProperties["API_KEYG"] as String

        
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "API_KEYG", "\"$apiKeyG\"")

        // 2) Inyectar la Maps Key en el manifest (meta-data)
        val mapsKey = localProperties.getProperty("API_KEYG")
        manifestPlaceholders["com.google.android.geo.API_KEY"] = mapsKey

        buildConfigField(type  = "String", name  = "FIREBASE_DB_URL", value = "\"https://guiaviajesia-default-rtdb.europe-west1.firebasedatabase.app/\"")


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

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
    }
}

dependencies {
    // AndroidX & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.ui.text)

    // Material y compatibilidad

    implementation(libs.androidx.material3.window.size.class1)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Retrofit y JSON
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi.converter)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.moshi)
    implementation(libs.kotlinx.serialization.json)
    ksp(libs.moshi.kotlin.codegen)

    // OkHttp Logging
    implementation(libs.okhttp.logging.interceptor)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.analytics)
    implementation(libs.ads.mobile.sdk)

    // Maps y ubicación
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places.sdk)
    implementation(libs.android.maps.utils)
    implementation(libs.maps.compose)

    // Hilt + navegación
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Imagen (Coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Accompanist (barra de estado/navigation bar)
    implementation(libs.accompanist.systemuicontroller)

    // Markdown
    implementation(libs.commonmark)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
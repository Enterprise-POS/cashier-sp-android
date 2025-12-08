plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("androidx.room")
    id("kotlin-kapt")
}

apply { plugin("com.android.application") }
apply { plugin("com.google.dagger.hilt.android") }

android {
    namespace = "com.pos.cashiersp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pos.cashiersp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.pos.cashiersp.HiltTestRunner"

        //val mode = project.findProperty("mode") as String
        //buildConfigField("String", "MODE", "\"$mode\"")
    }

    testBuildType = "uiTest"

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField("String", "MODE", "\"dev\"")
            buildConfigField("String", "DS_NAME", "\"jwt_store\"")
        }
        release {
            isMinifyEnabled = true
            buildConfigField("String", "MODE", "\"prod\"")
            buildConfigField("String", "DS_NAME", "\"jwt_store\"")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        create("uiTest") {
            // Get all 'debug' configurations
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
            isMinifyEnabled = false
            buildConfigField("String", "MODE", "\"uitest\"")
            buildConfigField("String", "DS_NAME", "\"jwt_store_test\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packagingOptions {
        resources {
            //excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Compose dependencies
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.9.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation("androidx.window:window:1.5.0")

    // Coroutine
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-compiler:2.57.2")

    // Hilt instrumentation tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.57.2")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.57.2")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.57.1")

    // Unit test
    testImplementation("com.google.dagger:hilt-android-testing:2.57.2")
    kaptTest("com.google.dagger:hilt-compiler:2.57.2")

    // Room
    implementation("androidx.room:room-runtime:2.8.4")
    kapt("androidx.room:room-compiler:2.8.4")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")

    // Coil (async image)
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.28.0")

    implementation("androidx.datastore:datastore-preferences:1.2.0")
    implementation("androidx.security:security-crypto:1.1.0")


    // Test Implementation
    testImplementation("androidx.test:core:1.7.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    testImplementation("com.google.truth:truth:1.4.5")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.3.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")

    // Android Test Implementation
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test:rules:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    androidTestImplementation("androidx.compose.ui:ui-test:1.9.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.9.5")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:5.3.2")
    androidTestImplementation("com.jakewharton.espresso:okhttp3-idling-resource:1.0.0")
}
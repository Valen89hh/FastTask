plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")

    id("androidx.room")
}

room{
    schemaDirectory("$projectDir/schemas")
}
android {
    namespace = "com.valendev.fasttask"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.valendev.fasttask"
        minSdk = 24
        targetSdk = 33
        versionCode = 2
        versionName = "1.1"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
    }


}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    val navVersion = "2.7.0"

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")

    // Room
    implementation ("androidx.room:room-runtime:2.6.0")
    ksp ("androidx.room:room-compiler:2.6.0")
    // Kotlin Extensions and Coroutines support for Room
    implementation ("androidx.room:room-ktx:2.6.0")

    //Circle Image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //viewpager Indicator
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    //StepView
    implementation("com.github.shuhart:stepview:1.5.1")

    //NavComponent
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")


    val hilVersion = "2.48"
    //DagerrHilt
    implementation("com.google.dagger:hilt-android:$hilVersion")
    ksp("com.google.dagger:hilt-compiler:$hilVersion")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
}
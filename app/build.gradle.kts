plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.cms_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cms_android"
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
        debug {
            // 启用调试功能以支持App Inspection
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.21")
        }
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    
    // Room components
    implementation("androidx.room:room-runtime:2.5.0")
    annotationProcessor("androidx.room:room-compiler:2.5.0")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    
    // CardView
    implementation("androidx.cardview:cardview:1.0.0")
    
    // 添加 Roboto 字体依赖
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.0")
}
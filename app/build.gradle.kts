plugins {
    id 'com.android.application'
    id 'kotlin-android'
}

android {
    compileSdk 34
    // Debe coincidir exactamente con el Package name definido
    namespace 'com.company.androidsecurity'

    defaultConfig {
        applicationId "com.company.androidsecurity"
        minSdk 23
        targetSdk 34
        versionCode 1
        versionName "1.0.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.debug
        }
        debug {
            applicationIdSuffix ".debug"
            debuggable true
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
                targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = '1.8'
    }

    buildFeatures {
        viewBinding true
    }
}

dependencies {
    // Core Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.10.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    // Dependencias de Seguridad y Red
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'              // Cliente HTTP para enviar reportes
    implementation 'com.google.code.gson:gson:2.10.1'                // Para serializar y deserializar JSON

    // Pruebas (se mantienen por defecto)
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}

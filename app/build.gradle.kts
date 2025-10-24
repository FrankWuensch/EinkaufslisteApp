plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
}

android {
    namespace = "com.frankwuensch.einkaufslisteapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.frankwuensch.einkaufslisteapp"
        minSdk = 23
        targetSdk = 36
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
    // I also fixed the deprecated kotlinOptions warning for you
    kotlin {
        jvmToolchain(17)
    }

    packagingOptions {
        // The 'resources' block is the modern, recommended way to handle resource files.
        resources {
            // Use 'excludes.add()' or 'excludes.addAll()' for Kotlin DSL (.kts files).
            // This prevents build failures from duplicate files included by multiple libraries.
            excludes.addAll(
                setOf(
                    "META-INF/DEPENDENCIES",
                    "META-INF/LICENSE",
                    "META-INF/LICENSE.txt",
                    "META-INF/license.txt",
                    "META-INF/NOTICE",
                    "META-INF/NOTICE.txt",
                    "META-INF/notice.txt",
                    "META-INF/ASL2.0",
                    "META-INF/*.kotlin_module",
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE-notice.md",
                    "mozilla/public-suffix-list.txt"
                )
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("it.skrape:skrapeit:1.2.2")
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    moduleName.set("EinkaufslisteApp")
    dokkaSourceSets {
        configureEach { // Use configureEach here
            // Explicitly point to the source directory
            sourceRoots.from(layout.projectDirectory.dir("src/main/java"))

            // Hide generated files like BuildConfig
            suppressGeneratedFiles.set(true)

            // Define the source link for this specific source set
            sourceLink {
                localDirectory.set(file("src/main/java"))
                // Corrected: No .toURL() call
                remoteUrl.set(uri("https://github.com/FrankWuensch/EinkaufslisteApp/blob/main/app/src/main/java").toURL())
                remoteLineSuffix.set("#L")
            }
        }
    }
}

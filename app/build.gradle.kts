import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.serialization)
}

android {
  namespace = "com.silence.performance.tracker"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.silence.performance"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    debug {
      isDebuggable = true
      isMinifyEnabled = false
      isShrinkResources = false
    }
    release {
      isMinifyEnabled = true
      isDebuggable = false
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    create("benchmark") {
      initWith(buildTypes.getByName("release"))
      isMinifyEnabled = false
      isShrinkResources = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-benchmark-rules.txt")
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks += listOf("debug")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
  }
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.fromTarget("17"))
    freeCompilerArgs.addAll(
      listOf(
        "-opt-in=androidx.benchmark.ExperimentalBenchmarkConfigApi",
        "-opt-in=androidx.benchmark.perfetto.ExperimentalPerfettoCaptureApi",
      )
    )
  }
}

dependencies {
  implementation(project(":tracker"))
  implementation(libs.firebase.perf)
  implementation(libs.sentry.android) { exclude(module = "sentry-android-sdk") }
  implementation(libs.androidx.startup)
  implementation(libs.retrofit)
  implementation(libs.retrofit.serialization)
  implementation(libs.kotlinx.serialization)

  implementation(libs.hilt)
  ksp(libs.hiltCompiler)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidxComposeRuntimeTracing)
  implementation(libs.androidxTracingKtx)
  implementation(libs.lottie)
  implementation(libs.coil.core)
  implementation(libs.coil)
  implementation(libs.coil.gif)
  implementation(libs.coil.network)
  implementation(libs.androidxProfileInstaller)
  implementation(libs.kotlinx.collections.immutable)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}
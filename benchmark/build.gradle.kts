import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.test)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.silence.performance.benchmark"
  compileSdk = 36

  defaultConfig {
    minSdk = 24
    targetSdk = 36

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR, DEBUGGABLE, LOW-BATTERY, NOT-SELF-INSTRUMENTING"
    testInstrumentationRunnerArguments["androidx.benchmark.fullTracing.enable"] = "true"
    testInstrumentationRunnerArguments["androidx.benchmark.output.enable"] = "true"
    testInstrumentationRunnerArguments["additionalTestOutputDir"] = "/sdcard/Download/"
    testInstrumentationRunnerArguments["no-isolated-storage"] = "true"
    testInstrumentationRunnerArguments["useTestStorageService"] = "true"
  }

  buildTypes {
    // This benchmark buildType is used for benchmarking, and should function like your
    // release build (for example, with minification on). It"s signed with a debug key
    // for easy local/CI testing.
    create("benchmark") {
      signingConfig = getByName("debug").signingConfig
      matchingFallbacks += listOf("debug")
      isMinifyEnabled = false
      isShrinkResources = false
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  targetProjectPath = ":app"
  experimentalProperties["android.experimental.self-instrumenting"] = true
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
  implementation(libs.androidx.junit)
  implementation(libs.androidx.espresso.core)
  implementation(libs.androidx.uiautomator)
  implementation(libs.androidx.benchmark.macro.junit4)
  implementation(libs.tracingPerfetto)
  implementation(libs.tracingPerfettoBinary)
  implementation(libs.androidxComposeRuntimeTracing)
  implementation(libs.androidxTracingKtx)
  implementation(libs.androidxProfileInstaller)
}

androidComponents {
  beforeVariants(selector().all()) {
    it.enable = it.buildType == "benchmark"
  }
}
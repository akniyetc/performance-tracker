package com.silence.performance.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
  private val COMMAND_GRANT_PERMISSION =
    "pm grant com.silence.performance android.permission.ACCESS_FINE_LOCATION"
  @get:Rule
  val benchmarkRule = MacrobenchmarkRule()

  @Test
  fun startup() = benchmarkRule.measureRepeated(
    packageName = "com.silence.performance",
    metrics = listOf(StartupTimingMetric(), FrameTimingMetric()),
    iterations = 2,
    compilationMode = CompilationMode.Full(),
    startupMode = StartupMode.COLD,
  ) {
    pressHome()
    killProcess()
    device.executeShellCommand("pm grant com.silence.performance android.permission.READ_MEDIA_IMAGES")
    device.executeShellCommand("pm grant com.silence.performance android.permission.READ_EXTERNAL_STORAGE")
    startActivityAndWait()
    device.wait(Until.hasObject(By.res("content")), 10000)
    val list = UiScrollable(UiSelector().resourceId("content"))
    if (list.exists()) {
      list.flingToEnd(10)
      list.flingToBeginning(10)
    }
    device.wait(Until.hasObject(By.res("second_activity_content")), 10000)
    device.findObject(By.res("second_activity_content")).click()
    device.wait(Until.hasObject(By.res("second_content")), 10000)
    val secondList = UiScrollable(UiSelector().resourceId("second_content"))
    if (secondList.exists()) {
      secondList.flingToEnd(20)
      secondList.flingToBeginning(20)
    }
  }
}
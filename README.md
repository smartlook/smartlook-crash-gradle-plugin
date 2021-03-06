# Smartlook Crash Gradle Plugin

The Smartlook Crash Gradle Plugin is a Gradle/Maven dependency, which will automatically find the mappings of all your build types and upload them.
The plugin hooks on to the build phase of your application, and if the `minifyEnabled` property of the specific build type is set to `true`,
then the plugin mapping will be available for the plugin to grab & upload.

**Expected behaviour:**
If the upload fails, then a checked exception is thrown to fail the entire build phase. This is to ensure that no mappings are missing if the upload
to the Smartlook Crash API fails.

## Usage of the plugin via Gradle

Firstly, set the `minifyEnabled` property of build types to `true` in the `build.gradle` file of your Android project:

```groovy
android {
    buildTypes {
        debug {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }

        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

Include the Smartlook Crash Gradle ID `com.smartlook.crash-gradle-plugin` into your `build.gradle` file as a plugin along with its optional version:

```groovy
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'com.smartlook.crash-gradle-plugin' version '1.0.0'
}
```

In the same `build.gradle` file, define the configuration for the plugin and include in your Smartlook API key:

```groovy
smartlookCrashGradlePlugin {
    apiKey = "your-smartlook-api-key"
    force = true // or false
}
```

## Development

Create `local.properties` file on the root of this project and paste in the path to your Android SDK:

```
sdk.dir=/path/to/your/Android/sdk
```

The plugin can be developed and tested locally by running the following command after any changes to the code:

```groovy
gradle publishToMavenLocal
```

To install the plugin locally, scaffold a basic Android application and in the top-level (root) `settings.gradle` file, make sure you include mavenLocal() as one of the sources for the dependency repositories:

```groovy
// Top-level build file or gradle settings file where you can add configuration options common to all sub-projects/modules.
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
```

Afterwards, follow the integration steps above in [the Usage of the Plugin via Gradle](#usage-of-the-plugin-via-gradle) and run the build phases to compile the basic scaffold of the Android application.

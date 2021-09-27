plugins {
  `java-gradle-plugin`
  id("org.jetbrains.kotlin.jvm") version "1.4.31"
  id("maven-publish")
}

repositories {
  mavenCentral()
  google()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  compileOnly("com.android.tools.build:gradle:4.2.0")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

group = "com.smartlook"
version = "1.0.0"

gradlePlugin {
  // Define the plugin
  val smartlookCrashGradlePlugin by plugins.creating {
    id = "com.smartlook.crash-gradle-plugin"
    implementationClass = "com.smartlook.plugins.src.SmartlookUploadPlugin"
  }
  println("Plugin '${smartlookCrashGradlePlugin.id}' has been defined with implementation class '${smartlookCrashGradlePlugin.implementationClass}'")
}

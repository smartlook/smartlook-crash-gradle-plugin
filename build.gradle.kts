plugins {
  `java-gradle-plugin`
  id("org.jetbrains.kotlin.jvm") version "1.4.31"
  id("maven-publish")
  id("signing")
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

  withSourcesJar()
  withJavadocJar()
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

apply(plugin = "maven-publish")
apply(plugin = "signing")

publishing {
  repositories {
    maven {
      val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
      val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
      url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

      credentials {
        username=project.properties["ossrhUsername"].toString()
        password=project.properties["ossrhPassword"].toString()
      }
    }
  }

  publications {
    create<MavenPublication>("maven") {
      pom {
        name.set("smartlook-crash-gradle-plugin")
        description.set("The Smartlook Crash Gradle Plugin will automatically find the mappings of all your build types and upload them.")
        url.set("https://www.smartlook.com/")

        licenses {
          license {
            name.set("MIT")
            url.set("http://opensource.org/licenses/MIT")
          }
        }

        developers {
          developer {
            id.set("pajk")
            name.set("Pavel Pokorny")
            email.set("pavel.pokorny@smartlook.com")
          }
        }

        scm {
          connection.set("git@github.com:smartlook/smartlook-crash-gradle-plugin.git")
          developerConnection.set("git@github.com:smartlook/smartlook-crash-gradle-plugin.git")
          url.set("https://github.com/smartlook/smartlook-crash-gradle-plugin.git")
        }
      }
    }
  }
}

signing {
  sign(configurations.archives.get())
}

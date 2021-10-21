package com.smartlook.plugins.src

import com.android.build.gradle.AppExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

const val EXTENSION_NAME = "smartlookCrashGradlePlugin"

interface SmartlookUploadPluginConfig {
    val apiKey: Property<String>
    val force: Property<Boolean>
}

class SmartlookUploadPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_NAME, SmartlookUploadPluginConfig::class.java)

        // Android configuration is set after the evaluation phase
        project.afterEvaluate {
            project.pluginManager.withPlugin("com.android.application") {
                val androidExtension = project.extensions.getByType(AppExtension::class.java)

                println("> Smartlook Plugin: Compile SDK Version: ${androidExtension.compileSdkVersion}")
                println("> Smartlook Plugin: Application Variants: ${androidExtension.applicationVariants}")
                println("> Smartlook Plugin: Build Type Count: ${androidExtension.buildTypes.size}")

                androidExtension.applicationVariants.all { applicationVariant ->
                    val appVariantName = applicationVariant.name
                    val minifiedEnabled = applicationVariant.buildType.isMinifyEnabled
                    val versionName = applicationVariant.versionName

                    println("> Smartlook Plugin: App variant: $appVariantName, minifyEnabled: $minifiedEnabled")

                    if (minifiedEnabled) {
                        val mappings = applicationVariant.mappingFileProvider.get()
                        val tasks = project.getTasksByName("minify${appVariantName.capitalize()}WithR8", true)
                        println("> Smartlook Plugin: Minification Task Found: $tasks")

                        val uploadTask = project.tasks.register(
                            "uploadTask${appVariantName.capitalize()}",
                            UploadTask::class.java,
                            mappings.files.first(),
                            versionName
                        )

                        tasks.forEach { task ->
                            println("> Smartlook Plugin: Setting finalized by: ${uploadTask.name}")
                            task.finalizedBy(uploadTask)
                        }

                        // Make bundle (for aab) and package tasks dependent on upload so that packages are not created when upload fails
                        applicationVariant.packageApplicationProvider?.configure { task -> task.dependsOn(uploadTask) }
                        project.tasks.named("package${appVariantName.capitalize()}Bundle")
                            .configure { it.dependsOn(uploadTask) }
                    }
                }
            }
        }
    }
}

open class UploadTask @Inject constructor(@InputFile val inputFile: File, private val versionName: String) : DefaultTask() {
    @TaskAction
    fun execute() {
        val extension = project.extensions.findByName(EXTENSION_NAME) as SmartlookUploadPluginConfig
        println("> Smartlook Plugin: Uploading ${inputFile.path} to Smartlook Crash API")
        uploadFile(extension.apiKey.get(), versionName, inputFile, extension.force.get())
    }
}
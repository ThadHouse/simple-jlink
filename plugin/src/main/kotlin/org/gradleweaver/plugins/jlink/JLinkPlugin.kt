package org.gradleweaver.plugins.jlink

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.create

open class JLinkPlugin : Plugin<Project> {

    companion object {
        const val EXTENSION_NAME = "jlink"
        const val JLINK_TASK_GROUP = "JLink"
        const val JLINK_TASK_NAME = "jlinkGenerate"
        const val JLINK_ZIP_TASK_NAME = "jlinkZip"
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, JLinkExtension::class, project)

        project.tasks.register(JLINK_TASK_NAME, JLinkTask::class.java, JLinkOptions("default")).configure {
            group = JLINK_TASK_GROUP
            description = "Generates a native Java runtime image."
        }

        val jlinkZipTask = project.tasks.register(JLINK_ZIP_TASK_NAME, Zip::class.java) {
            group = JLINK_TASK_GROUP
            description = "Generates a ZIP file of a native Java runtime image."
            from(JLINK_TASK_NAME)
        }

        // UGLY UGLY UGLY
        project.afterEvaluate {
            extension.configure.forEach { options ->
                generateTasks(options, project)
            }
        }
    }

    private fun generateTasks(options: JLinkOptions, project: Project) {
        val jlinkTask = project.tasks.register("$JLINK_TASK_NAME${options.name.capitalize()}", JLinkTask::class.java, options).configure {
            group = JLINK_TASK_GROUP
            description = "Generates a native Java runtime image."
        }
        project.tasks.register("$JLINK_ZIP_TASK_NAME${options.name.capitalize()}", Zip::class.java) {
            group = JLINK_TASK_GROUP
            description = "Generates a ZIP file of a native Java runtime image."
            from(jlinkTask)
        }
    }

}

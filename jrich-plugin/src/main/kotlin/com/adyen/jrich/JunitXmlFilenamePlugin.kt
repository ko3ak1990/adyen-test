package com.adyen.jrich

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class JunitXmlFilenamePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(
            "jRich",
            JRichExtension::class.java
        )
        extension.reportDir.convention(project.layout.buildDirectory.dir("reports/jrich"))

        val enrichTaskProvider = project.tasks.register("enrichJunitXmlReports", EnrichJunitXmlReportTask::class.java) {
            it.group = "verification"
            it.description = "Enriches JUnit XML output for all Test tasks in the project"
            it.inputReportDir.set(project.layout.buildDirectory.dir("test-results"))
            it.projectRootDir.set(project.layout.projectDirectory)
            it.outputReportDir.set(extension.reportDir)
        }

        project.tasks.withType(Test::class.java).configureEach { testTask ->
            testTask.finalizedBy(enrichTaskProvider)
        }
    }
}

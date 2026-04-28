package com.adyen.jrich

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

abstract class EnrichJunitXmlReportTask : DefaultTask() {
    @get:Optional
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputReportDir: DirectoryProperty

    @get:Internal
    abstract val projectRootDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputReportDir: DirectoryProperty

    @TaskAction
    fun enrich() {
        val reportDir = inputReportDir.get().asFile.toPath()
        if (!Files.exists(reportDir)) return

        val projectDir = projectRootDir.get().asFile.toPath()
        val outputRoot = outputReportDir.get().asFile.toPath()
        JunitXmlTaskProcessor.processTask(projectDir, reportDir, outputRoot)
    }
}


package com.adyen.junitxmlfilename

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

abstract class EnrichJunitXmlReportTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:Optional
    abstract val inputReportDir: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val projectRootDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputReportDir: DirectoryProperty

    @TaskAction
    fun enrich() {
        val reportDir = inputReportDir.orNull?.asFile?.toPath() ?: return
        if (!Files.exists(reportDir)) return

        val projectDir = projectRootDir.get().asFile.toPath()
        val outputRoot = outputReportDir.get().asFile.toPath()
        JunitXmlTaskProcessor.processTask(projectDir, reportDir, outputRoot)
    }
}


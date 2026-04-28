package com.adyen.junitxmlfilename

import org.gradle.api.tasks.testing.Test
import org.gradle.testfixtures.ProjectBuilder
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JunitXmlFilenamePluginTest {
    @Test
    fun `registers extension with default output directory`() {
        val projectDir = Files.createTempDirectory("plugin-project").toFile()
        val project = ProjectBuilder.builder().withProjectDir(projectDir).build()

        project.pluginManager.apply("com.adyen.junit-xml-report-enricher")

        val extension = project.extensions.getByType(JunitXmlFilenameExtension::class.java)
        val expected = project.layout.buildDirectory.dir("reports/junit-enriched").get().asFile

        assertEquals(expected, extension.outputDir.get().asFile)
    }

    @Test
    fun `registers enrichment task and finalizes test tasks with it`() {
        val projectDir = Files.createTempDirectory("plugin-project-task").toFile()
        val project = ProjectBuilder.builder().withProjectDir(projectDir).build()

        project.pluginManager.apply("com.adyen.junit-xml-report-enricher")
        val testTask = project.tasks.create("sampleTest", Test::class.java)
        val enrichTask = project.tasks.findByName("enrichJunitXmlReports")

        assertNotNull(enrichTask)
        val finalizedBy = testTask.finalizedBy.getDependencies(testTask)
        assertEquals(setOf(enrichTask), finalizedBy)
    }
}


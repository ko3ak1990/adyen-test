package com.adyen.jrich

import org.gradle.api.tasks.testing.Test as GradleTestTask
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

        project.pluginManager.apply("com.adyen.jrich")

        val extension = project.extensions.getByType(JRichExtension::class.java)
        val expected = project.layout.buildDirectory.dir("reports/jrich").get().asFile

        assertEquals(expected, extension.reportDir.get().asFile)
    }

    @Test
    fun `registers enrichment task for test tasks`() {
        val projectDir = Files.createTempDirectory("plugin-project-task").toFile()
        val project = ProjectBuilder.builder().withProjectDir(projectDir).build()

        project.pluginManager.apply("com.adyen.jrich")
        project.tasks.register("sampleTest", GradleTestTask::class.java).get()
        val enrichTask = project.tasks.findByName("enrichJunitXmlReports")

        assertNotNull(enrichTask)
        assertEquals("verification", enrichTask.group)
    }
}

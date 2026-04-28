package com.adyen.pathfilter

import kotlin.test.Test
import kotlin.test.assertEquals
import java.nio.file.Files

class PathFilterCliIntegrationTest {
    @Test
    fun `writes expected env output`() {
        val tempDir = Files.createTempDirectory("path-filter-it")
        val config = tempDir.resolve("filters.yaml")
        val output = tempDir.resolve("result.env")

        config.toFile().writeText(
            """
            filters:
              kotlin_sources:
                - "**/*.kt"
                - "!**/test/**"
              tests:
                - "**/test/**/*.kt"
              documentation:
                - "README.md"
                - "docs/**"
            """.trimIndent()
        )

        val exit = PathFilterCli.run(
            arrayOf(
                "--config", config.toString(),
                "--output", output.toString(),
                "app/src/Main.kt",
                "app/src/test/FiltersTest.kt",
                "README.md"
            )
        )

        assertEquals(0, exit)
        assertEquals(
            """
            kotlin_sources=true
            tests=true
            documentation=true
            """.trimIndent() + "\n",
            output.toFile().readText()
        )
    }
}


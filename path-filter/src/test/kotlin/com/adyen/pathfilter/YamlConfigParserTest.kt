package com.adyen.pathfilter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.nio.file.Files

class YamlConfigParserTest {
    @Test
    fun `parses valid config`() {
        val file = Files.createTempFile("filters", ".yaml")
        file.toFile().writeText(
            """
            filters:
              backend:
                - "app/src/**/*.kt"
                - "!app/src/test/**"
            """.trimIndent()
        )

        val parsed = YamlConfigParser().parse(file.toString())

        assertEquals(listOf("app/src/**/*.kt", "!app/src/test/**"), parsed.filters["backend"]?.patterns)
    }

    @Test
    fun `fails on missing file`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            YamlConfigParser().parse("/definitely/missing/filters.yaml")
        }
        kotlin.test.assertTrue(ex.message!!.contains("does not exist"))
    }

    @Test
    fun `fails on malformed yaml`() {
        val file = Files.createTempFile("filters-bad", ".yaml")
        file.toFile().writeText("filters: [broken")

        val ex = assertThrows(IllegalArgumentException::class.java) {
            YamlConfigParser().parse(file.toString())
        }
        kotlin.test.assertTrue(ex.message!!.contains("Failed to parse YAML"))
    }
}


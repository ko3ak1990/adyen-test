package com.adyen.demo

import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test

class PluginFailureSampleTest {
    @Test
    fun `fails intentionally when demoFailTest flag is enabled`() {
        val shouldFail = System.getProperty("demo.failTest", "false").toBoolean()
        assumeTrue("Enable with -PdemoFailTest=true", shouldFail)

        // Intentional failure used to verify filename/line enrichment on failing tests.
        assertEquals("expected", "actual")
    }
}


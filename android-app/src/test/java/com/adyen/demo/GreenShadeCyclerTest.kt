package com.adyen.demo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GreenShadeCyclerTest {
    @Test
    fun `cycles through shades and wraps around`() {
        val cycler = GreenShadeCycler(intArrayOf(10, 20, 30))

        assertEquals(10, cycler.current())
        assertEquals(20, cycler.next())
        assertEquals(30, cycler.next())
        assertEquals(10, cycler.next())
    }

    @Test
    fun `throws when shade list is empty`() {
        val cycler = GreenShadeCycler(intArrayOf())

        assertThrows(IllegalArgumentException::class.java) {
            cycler.current()
        }
    }
}


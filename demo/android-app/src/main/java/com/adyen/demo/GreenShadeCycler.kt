package com.adyen.demo

class GreenShadeCycler(
    private val shades: IntArray,
    startIndex: Int = 0
) {
    private var index = if (shades.isEmpty()) 0 else startIndex.mod(shades.size)

    fun current(): Int {
        require(shades.isNotEmpty()) { "Shades must not be empty" }
        return shades[index]
    }

    fun next(): Int {
        require(shades.isNotEmpty()) { "Shades must not be empty" }
        index = (index + 1) % shades.size
        return shades[index]
    }
}


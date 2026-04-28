package com.adyen.jrich

import org.gradle.api.file.DirectoryProperty
import javax.inject.Inject

abstract class JRichExtension @Inject constructor() {
    abstract val reportDir: DirectoryProperty
}

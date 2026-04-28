package com.adyen.junitxmlfilename

import org.gradle.api.file.DirectoryProperty
import javax.inject.Inject

abstract class JunitXmlFilenameExtension @Inject constructor() {
    abstract val outputDir: DirectoryProperty
}


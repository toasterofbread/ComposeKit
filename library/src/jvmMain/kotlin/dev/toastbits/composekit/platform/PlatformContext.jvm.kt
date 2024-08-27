package dev.toastbits.composekit.platform

import java.io.File

expect fun PlatformFile.Companion.fromFile(file: File, context: PlatformContext): PlatformFile

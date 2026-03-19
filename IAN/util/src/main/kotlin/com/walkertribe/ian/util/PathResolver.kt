package com.walkertribe.ian.util

import okio.BufferedSource
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath

/**
 * A PathResolver is an object which can accept a path to a particular resource and return an
 * InputStream to it. This is used by IAN when it needs to read in a resource. Note that all
 * resource paths are expressed relative to the Artemis install directory.
 *
 * @author rjwut
 */
interface PathResolver {
    /** The file system that contains the resource. */
    val fileSystem: FileSystem

    /** The base directory that contains the resource. */
    val baseDirectory: Path

    companion object {
        val DAT = "dat".toPath()
    }
}

/** Returns an InputStream from which the data at the given path can be read. */
@Throws(IOException::class)
inline fun <T> PathResolver.resolve(path: Path, readerAction: BufferedSource.() -> T): T =
    fileSystem.read(baseDirectory / path, readerAction)

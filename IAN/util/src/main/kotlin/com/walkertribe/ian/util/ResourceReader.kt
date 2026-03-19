package com.walkertribe.ian.util

import okio.BufferedSource
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath

/**
 * A ResourceReader is an object which can accept a path to a particular resource and read data from
 * it. Note that all resource paths are expressed relative to the Artemis install directory.
 *
 * Implementations of ResourceReader must specify the filesystem and the base directory, which
 * contains the `dat` folder. In an Artemis install directory, `dat` is where all the resources are
 * stored. The same resources should likewise be stored where the ResourceReader can access them.
 *
 * @author Jordan Longstaff
 */
interface ResourceReader {
    /** The file system that contains the resource. */
    val fileSystem: FileSystem

    /** The base directory that contains the resource. */
    val baseDirectory: Path

    companion object {
        val DAT = "dat".toPath()
    }
}

/** Reads data from the given path. */
@Throws(IOException::class)
inline fun <T> ResourceReader.read(path: Path, readerAction: BufferedSource.() -> T): T {
    val normalizedPath = path.normalized()
    require(normalizedPath.isRelative) { "Path must be relative to base directory" }
    require(".." !in normalizedPath.segments) { "Path must not traverse parent directories" }

    return fileSystem.read(baseDirectory / normalizedPath, readerAction)
}

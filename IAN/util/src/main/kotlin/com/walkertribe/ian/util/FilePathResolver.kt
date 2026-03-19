package com.walkertribe.ian.util

import okio.FileSystem
import okio.Path

/**
 * An implementation of PathResolver that reads resources relative to a specified directory on disk.
 * This should be the Artemis install directory, or another directory that contains the appropriate
 * resources in the same paths.
 *
 * @author rjwut
 */
class FilePathResolver(override val baseDirectory: Path) : PathResolver {
    override val fileSystem: FileSystem =
        FileSystem.SYSTEM.apply {
            require(exists(baseDirectory)) { "Directory does not exist" }
            require(metadata(baseDirectory).isDirectory) { "Not a directory" }
        }
}

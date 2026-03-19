package com.walkertribe.ian.util

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.engine.spec.tempdir
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import java.io.File
import okio.IOException
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath

class ResourceReaderTest :
    DescribeSpec({
        val tmpFile = tempfile()
        val tmpDir = tempdir()
        val tmpDirPath = tmpDir.toOkioPath()

        describe("ResourceReader") {
            it("Path dat/") { ResourceReader.DAT.name shouldBeEqual "dat" }
        }

        describe("FileSystemResourceReader") {
            it("Can create") { FileSystemResourceReader(tmpDirPath) }

            it("Can create input stream from file") {
                File(tmpDir, "foo").createNewFile()
                val reader = FileSystemResourceReader(tmpDirPath)
                shouldNotThrow<IOException> {
                    reader
                        .read("foo".toPath()) { readUtf8() }
                        .shouldNotBeNull()
                        .shouldBeInstanceOf<String>()
                }
            }

            it("Throws if parent file is not a directory") {
                shouldThrow<IllegalArgumentException> {
                    FileSystemResourceReader(tmpFile.toOkioPath())
                }
            }

            it("Throws if directory does not exist") {
                shouldThrow<IllegalArgumentException> {
                    FileSystemResourceReader(tmpDirPath / "bar")
                }
            }
        }
    })

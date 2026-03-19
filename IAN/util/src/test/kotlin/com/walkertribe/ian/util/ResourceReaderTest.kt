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
            lateinit var reader: FileSystemResourceReader

            it("Can create") { reader = FileSystemResourceReader(tmpDirPath) }

            it("Can read from file") {
                File(tmpDir, "foo").createNewFile()
                shouldNotThrow<IOException> {
                    reader
                        .read("foo".toPath()) { readUtf8() }
                        .shouldNotBeNull()
                        .shouldBeInstanceOf<String>()
                }
            }

            describe("Base directory requirements") {
                it("Throws if not a directory") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            FileSystemResourceReader(tmpFile.toOkioPath())
                        }
                    ex.message.shouldNotBeNull() shouldBeEqual "Not a directory"
                }

                it("Throws if directory does not exist") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            FileSystemResourceReader(tmpDirPath / "bar")
                        }
                    ex.message.shouldNotBeNull() shouldBeEqual "Directory does not exist"
                }
            }

            describe("Path requirements") {
                it("Throws if absolute") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            reader.read(tmpDirPath / "foo") { readUtf8() }
                        }
                    ex.message.shouldNotBeNull() shouldBeEqual
                        "Path must be relative to base directory"
                }

                it("Throws if traversing parent directories") {
                    val parentPath = "..".toPath()

                    listOf(parentPath, parentPath / "foo").forEach { path ->
                        val ex =
                            shouldThrow<IllegalArgumentException> {
                                reader.read(path) { readUtf8() }
                            }
                        ex.message.shouldNotBeNull() shouldBeEqual
                            "Path must not traverse parent directories"
                    }
                }
            }
        }
    })

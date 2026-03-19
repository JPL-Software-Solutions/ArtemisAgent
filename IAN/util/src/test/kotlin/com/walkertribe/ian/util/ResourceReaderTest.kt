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
            val reader: FileSystemResourceReader by lazy { FileSystemResourceReader(tmpDirPath) }

            it("Can create") { reader.shouldBeInstanceOf<FileSystemResourceReader>() }

            it("Can read from file") {
                File(tmpDir, "foo").createNewFile()
                shouldNotThrow<IOException> {
                    reader
                        .read("foo".toPath()) { readUtf8() }
                        .shouldNotBeNull()
                        .shouldBeInstanceOf<String>()
                }
            }

            fun testRequirement(expectedMessage: String, block: () -> Any?) {
                val ex = shouldThrow<IllegalArgumentException> { block() }
                ex.message.shouldNotBeNull() shouldBeEqual expectedMessage
            }

            describe("Base directory requirements") {
                it("Throws if not a directory") {
                    testRequirement("Not a directory") {
                        FileSystemResourceReader(tmpFile.toOkioPath())
                    }
                }

                it("Throws if directory does not exist") {
                    testRequirement("Directory does not exist") {
                        FileSystemResourceReader(tmpDirPath / "bar")
                    }
                }
            }

            describe("Path requirements") {
                it("Throws if absolute") {
                    testRequirement("Path must be relative to base directory") {
                        reader.read(tmpDirPath / "foo") { readUtf8() }
                    }
                }

                it("Throws if traversing parent directories") {
                    val parentPath = "..".toPath()

                    listOf(parentPath, parentPath / "foo").forEach { path ->
                        testRequirement("Path must not traverse parent directories") {
                            reader.read(path) { readUtf8() }
                        }
                    }
                }
            }
        }
    })

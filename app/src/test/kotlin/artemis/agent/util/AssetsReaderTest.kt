package artemis.agent.util

import android.content.res.AssetManager
import com.walkertribe.ian.util.ResourceReader
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import java.io.File
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import okio.source

class AssetsReaderTest :
    DescribeSpec({
        describe("AssetsReader") {
            val assetManager = mockk<AssetManager>()
            val assetsReader by lazy { AssetsReader(assetManager) }

            afterSpec { clearMocks(assetManager) }

            val srcDir = tempdir()
            val datDir = File(srcDir, "dat")
            datDir.mkdir()

            repeat(2) { index ->
                val testFile = File(datDir, "test${index + 1}.txt")
                testFile.writeText("test")
            }

            it("Can construct using AssetManager") {
                assetsReader.shouldBeInstanceOf<ResourceReader>()
                assetsReader.fileSystem.shouldBeInstanceOf<FileSystem>()
            }

            describe("Base directory") {
                it("Points to root path") {
                    assetsReader.baseDirectory shouldBeEqual AssetsReader.ROOT
                }

                it("Is root path") {
                    assetsReader.baseDirectory.toString() shouldBeEqual "/"
                    assetsReader.baseDirectory.isRoot.shouldBeTrue()
                }
            }

            describe("Copying to another location") {
                val spyFileSystem = spyk(FileSystem.SYSTEM)
                every { spyFileSystem.list(any()) } answers
                    {
                        File(srcDir, firstArg<Path>().toString()).list()!!.map { it.toPath() }
                    }

                every { spyFileSystem.source(any()) } answers
                    {
                        val path = srcDir.toOkioPath() / firstArg<Path>()
                        path.toFile().source()
                    }

                val spyReader = spyk(assetsReader)
                every { spyReader.fileSystem } returns spyFileSystem
                every { spyReader.baseDirectory } returns srcDir.toOkioPath()

                afterScope { clearMocks(spyReader, spyFileSystem) }

                withData(
                    nameFn = { it.first },
                    Triple("Succeeds", "test1", "test"),
                    Triple("Does nothing when file already exists", "test2", ""),
                ) { (_, filename, contents) ->
                    val destDir = tempdir()
                    val destDat = File(destDir, "dat")

                    if (contents.isEmpty()) {
                        destDat.mkdir()
                        val existingFile = File(destDat, "$filename.txt")
                        existingFile.createNewFile()
                    }

                    spyReader.copyVesselDataTo(destDat).shouldBeTrue()

                    val outFile = File(destDat, "$filename.txt")
                    outFile.shouldExist()
                    outFile.readText() shouldBeEqual contents
                }

                it("Fails with IOException") {
                    every { spyReader.fileSystem } returns FileSystem.SYSTEM
                    spyReader.copyVesselDataTo(tempdir()).shouldBeFalse()
                }
            }
        }
    })

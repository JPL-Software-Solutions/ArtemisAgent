package artemis.agent.util

import android.content.res.AssetManager
import com.walkertribe.ian.util.PathResolver
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import okio.Path
import okio.Path.Companion.toPath
import okio.assetfilesystem.asFileSystem

class AssetsResolver(manager: AssetManager) : PathResolver {
    override val fileSystem = manager.asFileSystem()
    override val baseDirectory: Path = ROOT

    fun copyVesselDataTo(datDir: File): Boolean =
        try {
            if (!datDir.exists()) datDir.mkdirs()

            fileSystem.list(PathResolver.DAT).forEach {
                val outFile = File(datDir, it.name)
                if (outFile.exists()) return@forEach
                fileSystem.read(PathResolver.DAT / it) {
                    FileOutputStream(outFile).use { outStream -> outStream.write(readByteArray()) }
                }
            }
            true
        } catch (_: IOException) {
            false
        }

    private companion object {
        val ROOT = "/".toPath()
    }
}

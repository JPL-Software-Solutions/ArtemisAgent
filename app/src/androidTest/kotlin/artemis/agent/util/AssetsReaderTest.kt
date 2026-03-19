package artemis.agent.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.walkertribe.ian.util.read
import okio.FileSystem
import okio.Path.Companion.toPath
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AssetsReaderTest {
    @Test
    fun readTest() {
        reader.read("dat".toPath() / "test.txt") { Assert.assertEquals("TEST", readUtf8Line()) }
    }

    @Test
    fun copyTest() {
        val tempPath = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "dat"
        Assert.assertTrue(reader.copyVesselDataTo(tempPath.toFile()))
    }

    private companion object {
        val context by lazy {
            checkNotNull(InstrumentationRegistry.getInstrumentation().context) {
                "Failed to get context"
            }
        }

        val reader by lazy { AssetsReader(context.assets) }
    }
}

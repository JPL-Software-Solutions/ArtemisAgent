package artemis.agent

import android.os.Build
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SdkSuppress
import artemis.agent.screens.MainScreen
import artemis.agent.screens.MainScreen.denyPermissions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.kakao.dialog.KAlertDialog
import io.github.kakaocup.kakao.text.KButton
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.TIRAMISU)
@LargeTest
class PermissionRationaleTest : TestCase() {
    @get:Rule val activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun permissionRationaleDialogPositiveTest() {
        run {
            testPermissionDialog {
                step("Click positive button") { clickRationaleDialogButton { positiveButton } }
            }
        }
    }

    @Test
    fun permissionRationaleDialogNegativeTest() {
        run {
            testPermissionDialog {
                step("Click negative button") { clickRationaleDialogButton { negativeButton } }

                step("Deny permissions again") { denyPermissions(isFirstTime = false) }
            }
        }
    }

    private companion object {
        inline fun clickRationaleDialogButton(button: KAlertDialog.() -> KButton) {
            MainScreen.alertDialog.button().click()
        }

        fun TestContext<*>.testPermissionDialog(withDialog: TestContext<*>.() -> Unit) {
            MainScreen {
                step("Deny permissions") { denyPermissions(isFirstTime = true) }

                repeat(numPermissionDialogs) {
                    step("Check for permission rationale dialog") {
                        assertPermissionRationaleDialogOpen()
                    }

                    withDialog()
                }

                step("Check for changelog") {
                    assertChangelogOpen()
                    Assert.assertFalse(device.permissions.isDialogVisible())
                }
            }
        }
    }
}

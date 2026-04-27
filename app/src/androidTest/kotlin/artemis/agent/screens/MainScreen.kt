package artemis.agent.screens

import android.os.Build
import androidx.annotation.StringRes
import androidx.test.espresso.NoActivityResumedException
import artemis.agent.MainActivity
import artemis.agent.R
import artemis.agent.isDisplayedWithText
import artemis.agent.isRemoved
import com.kaspersky.kaspresso.device.permissions.Permissions
import com.kaspersky.kaspresso.screens.KScreen
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.kakao.check.KCheckBox
import io.github.kakaocup.kakao.dialog.KAlertDialog
import io.github.kakaocup.kakao.text.KButton
import org.junit.Assert

object MainScreen : KScreen<MainScreen>() {
    override val layoutId: Int = R.layout.activity_main
    override val viewClass: Class<*> = MainActivity::class.java

    val setupPageButton = KCheckBox { withId(R.id.setupPageButton) }
    val gamePageButton = KCheckBox { withId(R.id.gamePageButton) }
    val helpPageButton = KCheckBox { withId(R.id.helpPageButton) }

    val updateButton = KButton { withId(R.id.updateButton) }

    val alertDialog = KAlertDialog()

    val numPermissionDialogs by lazy {
        listOf(Build.VERSION_CODES.TIRAMISU, Build.VERSION_CODES.CINNAMON_BUN).count {
            Build.VERSION.SDK_INT >= it
        }
    }

    inline fun TestContext<*>.mainScreenTest(
        backButtonShouldCloseApp: Boolean = true,
        crossinline test: MainScreen.() -> Unit,
    ) {
        this@MainScreen {
            acceptPermissions()
            step("Dismiss changelog") {
                alertDialog.isCompletelyDisplayed()
                pressBack()
            }
            test()
            if (backButtonShouldCloseApp)
                step("Back button should close the app") { assertCloseOnBackButton() }
        }
    }

    fun assertCloseOnBackButton() {
        try {
            pressBack()
            Assert.fail("Expected back button to close the app")
        } catch (_: NoActivityResumedException) {
            // Success
        }
    }

    fun TestContext<*>.acceptPermissions() {
        repeat(numPermissionDialogs) { i ->
            val time = i + 1
            step("Check #$time that permissions dialog is open") { assertPermissionsDialogOpen() }
            step("Accept permissions #$time") { device.permissions.allowViaDialog() }
        }
    }

    fun TestContext<*>.denyPermissions(isFirstTime: Boolean) {
        repeat(if (isFirstTime) numPermissionDialogs else 1) { i ->
            val time = i + 1
            step("Check #$time that permissions dialog is open") { assertPermissionsDialogOpen() }
            step("Deny permissions #$time") {
                device.permissions.denyViaDialog(
                    if (isFirstTime) Permissions.Button.DENY
                    else Permissions.Button.DENY_AND_DONT_ASK_AGAIN
                )
            }
        }
    }

    fun TestContext<*>.assertPermissionsDialogOpen() {
        Assert.assertTrue(device.permissions.isDialogVisible())
    }

    fun assertPermissionRationaleDialogOpen(@StringRes rationaleMessage: Int) {
        alertDialog {
            isCompletelyDisplayed()
            title.isRemoved()
            message.isDisplayedWithText(rationaleMessage)
            positiveButton.isDisplayedWithText(R.string.yes)
            negativeButton.isDisplayedWithText(R.string.no)
            neutralButton.isRemoved()
        }
    }

    fun assertChangelogOpen() {
        alertDialog {
            isCompletelyDisplayed()
            title.isDisplayedWithText(R.string.app_version)
            message.isCompletelyDisplayed()
            positiveButton.isRemoved()
            negativeButton.isRemoved()
            neutralButton.isRemoved()
        }
    }

    fun assertVesselDataWarningOpen() {
        alertDialog {
            isCompletelyDisplayed()
            title.isDisplayedWithText(R.string.vessel_data)
            message.isDisplayedWithText(R.string.xml_location_warning)
            positiveButton.isDisplayedWithText(R.string.yes)
            negativeButton.isDisplayedWithText(R.string.no)
            neutralButton.isRemoved()
        }
    }

    fun assertExitWarningOpen() {
        alertDialog {
            isCompletelyDisplayed()
            title.isRemoved()
            message.isDisplayedWithText(R.string.exit_message)
            positiveButton.isDisplayedWithText(R.string.yes)
            negativeButton.isDisplayedWithText(R.string.no)
            neutralButton.isRemoved()
        }
    }
}

package net.safefleet.focus.helpers

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.lawmobile.presentation.ui.fileList.simpleList.SimpleFileListAdapter
import org.hamcrest.Matcher

object CustomCheckboxAction {

    private fun clickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Click in specific ID"
            }

            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun perform(uiController: UiController?, view: View) {
                val v = view.findViewById<View>(id)
                v.isPressed = true
                v.performClick()
                v.isPressed = false
            }
        }
    }

    fun selectCheckboxOnRecyclerPosition(
        @IdRes recyclerID: Int,
        @IdRes targetCheckBox: Int,
        position: Int
    ) {
        Espresso.onView(ViewMatchers.withId(recyclerID))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<SimpleFileListAdapter.SimpleListViewHolder>(
                    position,
                    clickChildViewWithId(targetCheckBox)
                )
            )
    }
}

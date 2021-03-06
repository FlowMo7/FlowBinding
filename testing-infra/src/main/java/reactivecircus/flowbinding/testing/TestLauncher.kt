package reactivecircus.flowbinding.testing

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import reactivecircus.blueprint.testing.RobotActions
import reactivecircus.blueprint.testing.currentActivity
import com.google.android.material.R as MaterialR

inline fun <reified F : Fragment> launchTest(
    block: TestLauncher.(FragmentScenario<F>) -> Unit
) {
    val scenario = launchFragmentInContainer<F>(themeResId = MaterialR.style.Theme_MaterialComponents_DayNight)
    Espresso.onIdle()
    val testScope = MainScope()
    TestLauncher(testScope).block(scenario)
    testScope.cancel()
}

class TestLauncher(val testScope: CoroutineScope) : RobotActions {

    fun <T : View> getViewById(@IdRes viewId: Int): T {
        return currentActivity()!!.findViewById(viewId)
    }

    val rootView: View
        get() = currentActivity()!!.window.decorView.rootView

    val fragment: Fragment
        get() = (currentActivity() as FragmentActivity)
            .supportFragmentManager
            .findFragmentByTag(FRAGMENT_TAG)!!

    fun cancelTestScope() {
        testScope.cancel()
        Espresso.onIdle()
    }

    companion object {
        private const val FRAGMENT_TAG = "FragmentScenario_Fragment_Tag"
    }
}

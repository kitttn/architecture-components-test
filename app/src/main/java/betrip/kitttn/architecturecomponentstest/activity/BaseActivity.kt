package betrip.kitttn.architecturecomponentstest.activity

import android.support.v7.app.AppCompatActivity
import betrip.kitttn.architecturecomponentstest.app.App
import betrip.kitttn.architecturecomponentstest.di.components.DaggerModelViewComponent
import betrip.kitttn.architecturecomponentstest.di.modules.LifecycleAwareModule
import betrip.kitttn.architecturecomponentstest.view.BackPressHandable

/**
 * @author kitttn
 */

open class BaseActivity : AppCompatActivity() {
    val lifecycleComponent by lazy {
        DaggerModelViewComponent.builder()
                .appComponent(App.graph)
                .lifecycleAwareModule(LifecycleAwareModule())
                .build()
    }

    override fun onBackPressed() {
        val somebodyReacted = supportFragmentManager.fragments
                .filter { it is BackPressHandable }
                .map { (it as BackPressHandable).onBackPressProcessed() }
                .reduce { previous, next -> previous or next }

        if (!somebodyReacted)
            super.onBackPressed()
    }
}
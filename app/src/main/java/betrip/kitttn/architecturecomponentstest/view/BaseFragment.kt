package betrip.kitttn.architecturecomponentstest.view

import android.support.v4.app.Fragment
import betrip.kitttn.architecturecomponentstest.app.App
import betrip.kitttn.architecturecomponentstest.di.components.DaggerModelViewComponent
import betrip.kitttn.architecturecomponentstest.di.components.ModelViewComponent

/**
 * @author kitttn
 */

open class ComponentFragment : Fragment() {
    val lifecycleComponent: ModelViewComponent by lazy {
        DaggerModelViewComponent.builder()
                .appComponent(App.graph)
                .build()
    }
}
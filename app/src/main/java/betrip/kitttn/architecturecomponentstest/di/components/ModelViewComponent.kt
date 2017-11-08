package betrip.kitttn.architecturecomponentstest.di.components

import betrip.kitttn.architecturecomponentstest.di.LifecycleAware
import betrip.kitttn.architecturecomponentstest.di.modules.LifecycleAwareModule
import betrip.kitttn.architecturecomponentstest.di.modules.SearchResultsViewModelFactory
import betrip.kitttn.architecturecomponentstest.view.SearchFragment
import dagger.Component

/**
 * @author kitttn
 */

@LifecycleAware
@Component(modules = arrayOf(LifecycleAwareModule::class), dependencies = arrayOf(AppComponent::class))
interface ModelViewComponent {
    val searchFactory: SearchResultsViewModelFactory
    fun inject(fragment: SearchFragment)
}
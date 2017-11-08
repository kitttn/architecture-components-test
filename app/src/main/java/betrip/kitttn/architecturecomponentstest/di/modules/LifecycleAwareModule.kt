package betrip.kitttn.architecturecomponentstest.di.modules

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import betrip.kitttn.architecturecomponentstest.di.LifecycleAware
import betrip.kitttn.architecturecomponentstest.services.CountryLoader
import betrip.kitttn.architecturecomponentstest.vm.SearchResultsViewModel
import dagger.Module
import dagger.Provides

/**
 * @author kitttn
 */

@Module @LifecycleAware
class LifecycleAwareModule {
    @Provides @LifecycleAware
    fun provideSearchViewModelFactory(countryLoader: CountryLoader) =
            Factory(SearchResultsViewModel(countryLoader))
}

class Factory<C : Any>(private val model: C) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(model::class.java))
            return model as T

        throw Error("Can't create AllViewModel, type: ${modelClass.canonicalName}")
    }
}
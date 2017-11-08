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
    fun provideSearchResults(countryLoader: CountryLoader) = SearchResultsViewModel(countryLoader)

    @Provides @LifecycleAware
    fun provideSearchFactory(model: SearchResultsViewModel) =
            SearchResultsViewModelFactory(model)
}

class SearchResultsViewModelFactory(private val model: SearchResultsViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(SearchResultsViewModel::class.java))
            return model as T

        throw Error("Can't create SearchResultsViewModel, type: ${modelClass.canonicalName}")
    }
}
package betrip.kitttn.architecturecomponentstest.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import betrip.kitttn.architecturecomponentstest.model.CountryName
import betrip.kitttn.architecturecomponentstest.plusAssign
import betrip.kitttn.architecturecomponentstest.services.CountryLoader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author kitttn
 */

sealed class SearchResultState

data class SearchResultLoading(val loading: Boolean = true) : SearchResultState()

data class SearchResultSuccess(val data: List<CountryName>) : SearchResultState()

data class SearchResultError(val errorReason: String, val errorCode: Int) : SearchResultState() {
    companion object {
        const val ERROR_NO_NETWORK = 1
        const val ERROR_OTHER = 999
    }
}

class SearchResultsViewModel @Inject constructor(private val countryLoader: CountryLoader) : ViewModel() {
    val searchResults = MutableLiveData<SearchResultState>()
    private val composite = CompositeDisposable()

    fun startSearch(query: String) {
        // TODO: add DB loading via Repository, now just mock some data
        searchResults.postValue(SearchResultLoading())
        composite += countryLoader.getCountryNames(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    searchResults.postValue(SearchResultSuccess(it))
                }, {
                    searchResults.postValue(SearchResultError(it.localizedMessage, SearchResultError.ERROR_OTHER))
                })
    }

    override fun onCleared() {
        composite.clear()
        super.onCleared()
    }
}
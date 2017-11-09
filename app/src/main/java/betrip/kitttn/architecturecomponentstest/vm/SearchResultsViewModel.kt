package betrip.kitttn.architecturecomponentstest.vm

import android.arch.lifecycle.ViewModel
import betrip.kitttn.architecturecomponentstest.model.CountryName
import betrip.kitttn.architecturecomponentstest.plusAssign
import betrip.kitttn.architecturecomponentstest.services.CountryLoader
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

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

class SearchResultsViewModel(private val countryLoader: CountryLoader) : ViewModel() {
    private val searchResults = BehaviorSubject.create<SearchResultState>()
    private val composite = CompositeDisposable()

    fun startSearch(query: String) {
        searchResults.onNext(SearchResultLoading())
        composite += countryLoader.getCountryNames(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    searchResults.onNext(SearchResultLoading(false))
                    searchResults.onNext(SearchResultSuccess(it))
                }, {
                    searchResults.onNext(SearchResultLoading(false))
                    searchResults.onNext(SearchResultError("Some reason", SearchResultError.ERROR_OTHER))
                })
    }

    fun getSearchResults(): Observable<SearchResultState> = searchResults

    override fun onCleared() {
        composite.clear()
        super.onCleared()
    }
}
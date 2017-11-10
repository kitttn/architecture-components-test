package betrip.kitttn.architecturecomponentstest.vm

import android.arch.lifecycle.ViewModel
import betrip.kitttn.architecturecomponentstest.model.CountryName
import betrip.kitttn.architecturecomponentstest.plusAssign
import betrip.kitttn.architecturecomponentstest.services.CountryLoader
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * @author kitttn
 */

sealed class SearchResultState

class InitialSearchResultState : SearchResultState()

class SearchResultLoading : SearchResultState()

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

    init {
        searchResults.onNext(InitialSearchResultState())

        composite += countryLoader.countries
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ searchResults.onNext(SearchResultSuccess(it)) },
                        { searchResults.onNext(SearchResultError(it.localizedMessage, SearchResultError.ERROR_OTHER)) })
    }

    fun startSearch(query: String) {
        if (query.isEmpty()) {
            fetchData { countryLoader.fetchAllCountries() }
            return
        }

        fetchData { countryLoader.fetchCountries(query) }
    }

    private fun fetchData(completable: () -> Completable) {
        searchResults.onNext(SearchResultLoading())
        composite += completable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, Throwable::printStackTrace)
    }

    fun getSearchResults(): Observable<SearchResultState> = searchResults

    override fun onCleared() {
        composite.clear()
        super.onCleared()
    }
}
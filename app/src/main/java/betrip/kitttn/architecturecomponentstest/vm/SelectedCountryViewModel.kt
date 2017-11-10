package betrip.kitttn.architecturecomponentstest.vm

import android.arch.lifecycle.ViewModel
import betrip.kitttn.architecturecomponentstest.model.CountryDetails
import betrip.kitttn.architecturecomponentstest.plusAssign
import betrip.kitttn.architecturecomponentstest.services.CountryDetailsLoader
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers

/**
 * @author kitttn
 */

sealed class SelectedCountryState

class CountryLoadingState : SelectedCountryState()

data class CountryLoadSuccessState(val country: CountryDetails) : SelectedCountryState()
data class CountryLoadErrorState(val error: String) : SelectedCountryState()

class SelectedCountryViewModel(private val detailsLoader: CountryDetailsLoader) : ViewModel() {
    private val states = PublishProcessor.create<SelectedCountryState>()
    private val composite = CompositeDisposable()

    fun fetchCountryDetails(countryName: String) {
        states.onNext(CountryLoadingState())
        composite += detailsLoader.getCountryDetails(countryName)
                .subscribeOn(Schedulers.io())
                .subscribe({ states.onNext(CountryLoadSuccessState(it)) },
                        { states.onNext(CountryLoadErrorState(it.localizedMessage)) })
    }

    fun countryDetails(): Flowable<SelectedCountryState> = states

    override fun onCleared() {
        composite.clear()
        super.onCleared()
    }
}
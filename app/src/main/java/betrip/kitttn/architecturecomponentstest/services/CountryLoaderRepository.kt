package betrip.kitttn.architecturecomponentstest.services

import betrip.kitttn.architecturecomponentstest.model.CountriesApi
import betrip.kitttn.architecturecomponentstest.model.CountryName
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.processors.PublishProcessor

/**
 * @author kitttn
 */

interface CountryNamesLoader {
    /**
     * Subscribe on this Flowable to get country lists;
     */
    val countries: Flowable<List<CountryName>>

    fun fetchCountries(query: String): Completable
    fun fetchAllCountries(): Completable
}

class RestCountryNamesLoaderRepository(private val api: CountriesApi) : CountryNamesLoader {
    private val loadedCountries = mutableListOf<CountryName>()
    private val countriesProcessor = PublishProcessor
            .create<List<CountryName>>()

    companion object {
        private val TAG = RestCountryNamesLoaderRepository::class.java.simpleName
    }

    override val countries: Flowable<List<CountryName>>
        get() = countriesProcessor

    override fun fetchCountries(query: String): Completable {
        return loadCountries()
                .map { it.filter { it.name.contains(query, true) } }
                .doOnEvent { list, error -> if (error != null) countriesProcessor.onError(error) else countriesProcessor.onNext(list) }
                .toCompletable()

    }

    override fun fetchAllCountries(): Completable = loadCountries()
            .doOnEvent { list, error -> if (error != null) countriesProcessor.onError(error) else countriesProcessor.onNext(list) }
            .toCompletable()

    private fun loadCountries(): Single<List<CountryName>> {
        return Single.just(true)
                .flatMap {
                    if (loadedCountries.isEmpty())
                        api.getAllCountries()
                                .map { CountriesCache(it, true) }
                    else Single.just(CountriesCache(loadedCountries, false))
                }
                .map {
                    if (it.shouldCache) {
                        loadedCountries.clear()
                        loadedCountries += it.countries
                    }
                    it.countries
                }
    }
}

data class CountriesCache(val countries: List<CountryName>, val shouldCache: Boolean)
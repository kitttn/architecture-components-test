package betrip.kitttn.architecturecomponentstest.services

import android.util.Log
import betrip.kitttn.architecturecomponentstest.model.CountriesApi
import betrip.kitttn.architecturecomponentstest.model.CountryName
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * @author kitttn
 */

interface CountryLoader {
    fun getCountryNames(query: String): Single<List<CountryName>>
    fun reset()
}

class TestCountryLoaderRepository : CountryLoader {
    override fun reset() {
    }

    override fun getCountryNames(query: String): Single<List<CountryName>> {
        return Single
                .just(listOf("ABC", "CDE", "DEF", "EFG").map { CountryName(it) })
                .delay(3, TimeUnit.SECONDS)
    }
}

class RestCountryLoaderRepository(private val api: CountriesApi) : CountryLoader {
    private var previousQuery = ""
    private var previousResults = emptyList<CountryName>()
    private var countryNamesSubject = BehaviorSubject.create<List<CountryName>>()

    companion object {
        private val TAG = RestCountryLoaderRepository::class.java.simpleName
    }

    override fun getCountryNames(query: String): Single<List<CountryName>> {
        Log.i(TAG, "getCountryNames: Querying $query...")

        return countryNamesSubject
                .flatMapSingle {
                    val shouldDownload = query.length < previousQuery.length || previousQuery.isEmpty()
                    if (shouldDownload) {
                        previousQuery = query
                        api.getCountriesList(query)
                    } else Single.just(previousResults)
                }
                .doOnNext { previousResults = it }
                .map { it.filter { it.name.contains(query, true) } }
                .first(emptyList())
    }

    override fun reset() {
        previousQuery = ""
        if (previousResults.isEmpty())
            countryNamesSubject.onNext(emptyList())
        previousResults = emptyList()
    }
}
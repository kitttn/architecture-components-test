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
}

class TestCountryLoaderRepository : CountryLoader {
    override fun getCountryNames(query: String): Single<List<CountryName>> {
        return Single
                .just(listOf("ABC", "CDE", "DEF", "EFG").map { CountryName(it) })
                .delay(3, TimeUnit.SECONDS)
    }
}

class RestCountryLoaderRepository(private val api: CountriesApi) : CountryLoader {
    private var previousQuery = ""
    private var countryNamesSubject = BehaviorSubject.create<List<CountryName>>()

    companion object {
        private val TAG = RestCountryLoaderRepository::class.java.simpleName
    }

    override fun getCountryNames(query: String): Single<List<CountryName>> {
        Log.i(TAG, "getCountryNames: Querying $query...")
        if (query.isEmpty()) {
            previousQuery = query
            return Single.just(emptyList())
        }

        if (query.length < previousQuery.length || previousQuery.isEmpty()) {
            previousQuery = query
            return api.getCountriesList(query)
                    .doAfterSuccess { countryNamesSubject.onNext(it) }
        }

        return countryNamesSubject
                .map { it.filter { it.name.contains(query, true) } }
                .first(emptyList())
    }
}
package betrip.kitttn.architecturecomponentstest.services

import betrip.kitttn.architecturecomponentstest.model.CountryName
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * @author kitttn
 */

interface CountryLoader {
    fun getCountryNames(query: String): Observable<List<CountryName>>
}

class TestCountryLoaderRepository : CountryLoader {
    override fun getCountryNames(query: String): Observable<List<CountryName>> {
        return Observable
                .just(listOf("ABC", "CDE", "DEF", "EFG").map { CountryName(it) })
                .delay(3, TimeUnit.SECONDS)
    }

}
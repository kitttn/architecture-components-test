package betrip.kitttn.architecturecomponentstest.model

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author kitttn
 */

interface CountriesApi {
    @GET("name/{partName}?fields=name;nativeName;flag")
    fun getCountriesList(@Path("partName") countryPartialName: String): Single<List<CountryName>>
}
package betrip.kitttn.architecturecomponentstest.model

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author kitttn
 */

interface CountriesApi {
    @GET("name/{partName}?fields=name;nativeName;alpha2Code")
    fun getCountriesList(@Path("partName") countryPartialName: String): Single<List<CountryName>>

    @GET("all?fields=name;nativeName;alpha2Code")
    fun getAllCountries(): Single<List<CountryName>>

    @GET("name/{fullName}?fullText=true")
    fun getCountryByFullName(@Path("fullName") countryFullName: String): Single<List<CountryDetails>>
}
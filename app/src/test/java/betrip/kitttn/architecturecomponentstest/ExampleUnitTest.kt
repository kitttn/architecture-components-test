package betrip.kitttn.architecturecomponentstest

import betrip.kitttn.architecturecomponentstest.model.CountriesApi
import betrip.kitttn.architecturecomponentstest.model.entities.CountryDetails
import betrip.kitttn.architecturecomponentstest.model.entities.CountryName
import betrip.kitttn.architecturecomponentstest.services.implementation.RestCountryNamesLoader
import betrip.kitttn.architecturecomponentstest.vm.EnteredTextViewModel
import io.reactivex.Single
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun lastQueryProducesRealLastResult() {
        val enteredTextViewModel = EnteredTextViewModel()
        enteredTextViewModel.textChanged("Hello")

        enteredTextViewModel.enteredText
                .subscribe({ println(it) }, Throwable::printStackTrace)

        enteredTextViewModel.lastQuery
                .test()
                .assertValues("Hello")
                .assertComplete()

        enteredTextViewModel.textChanged("World!")

        enteredTextViewModel.lastQuery
                .test()
                .assertValues("World!")
                .assertComplete()
    }

    @Test
    fun countryLoaderRepositoryEmitsToProcessor() {
        val testApi = object : CountriesApi {
            override fun getCountryByFullName(countryFullName: String): Single<List<CountryDetails>> {
                return Single.just(emptyList())
            }

            override fun getCountriesList(countryPartialName: String): Single<List<CountryName>> {
                return Single.just(listOf("AAA", "BBB", "CCC", "DDD").map { CountryName(it) })
            }

            override fun getAllCountries(): Single<List<CountryName>> {
                return Single.just(listOf("AAA", "BBB", "CCC", "DDD", "EEE", "FFF", "GGG", "HHH").map { CountryName(it) })
            }
        }

        val countryLoaderRepository = RestCountryNamesLoader(testApi)
        countryLoaderRepository.countries
                .subscribe({ println("Result: $it") }, Throwable::printStackTrace)

        countryLoaderRepository.fetchAllCountries()
                .subscribe()

        countryLoaderRepository.fetchCountries("bb")
                .subscribe()
    }
}

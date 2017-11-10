package betrip.kitttn.architecturecomponentstest

import betrip.kitttn.architecturecomponentstest.model.CountriesApi
import betrip.kitttn.architecturecomponentstest.model.CountryName
import betrip.kitttn.architecturecomponentstest.services.RestCountryLoaderRepository
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

        enteredTextViewModel.getEnteredText()
                .subscribe({ println(it) }, Throwable::printStackTrace)

        enteredTextViewModel.getLastQuery()
                .test()
                .assertValues("Hello")
                .assertComplete()

        enteredTextViewModel.textChanged("World!")

        enteredTextViewModel.getLastQuery()
                .test()
                .assertValues("World!")
                .assertComplete()
    }

    @Test
    fun countryLoaderRepositoryEmitsToProcessor() {
        val testApi = object : CountriesApi {
            override fun getCountriesList(countryPartialName: String): Single<List<CountryName>> {
                return Single.just(listOf("AAA", "BBB", "CCC", "DDD").map { CountryName(it) })
            }

            override fun getAllCountries(): Single<List<CountryName>> {
                return Single.just(listOf("AAA", "BBB", "CCC", "DDD", "EEE", "FFF", "GGG", "HHH").map { CountryName(it) })
            }
        }

        val countryLoaderRepository = RestCountryLoaderRepository(testApi)
        countryLoaderRepository.countries
                .subscribe({ println("Result: $it") }, Throwable::printStackTrace)

        countryLoaderRepository.fetchAllCountries()
                .subscribe()

        countryLoaderRepository.fetchCountries("bb")
                .subscribe()
    }
}

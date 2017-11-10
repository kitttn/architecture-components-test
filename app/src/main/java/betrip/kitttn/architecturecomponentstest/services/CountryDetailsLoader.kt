package betrip.kitttn.architecturecomponentstest.services

import betrip.kitttn.architecturecomponentstest.model.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * @author kitttn
 */

interface CountryDetailsLoader

class CachedCountryDetailsLoader(private val api: CountriesApi, private val room: RoomCountryDatabase) : CountryDetailsLoader {
    fun getCountryDetails(countryName: String): Single<CountryDetails> {
        val existing = room.roomCountryDao().findCountryByName(countryName)
        if (existing != null) {
            val languages = room.roomCountryDao().getCountryLanguages(countryName)
            return Single.just(fromDao(existing, languages))
        }

        // need remote loading
        return api.getCountryByFullName(countryName)
                .subscribeOn(Schedulers.io())
                .map { it.first() }
                .doAfterSuccess {
                    val (dao, languages) = toDao(it)
                    room.roomCountryDao().addCountry(dao)
                    room.roomCountryDao().addCountryLanguages(languages.toTypedArray())
                }
    }
}
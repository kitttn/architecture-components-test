package betrip.kitttn.architecturecomponentstest.services

import betrip.kitttn.architecturecomponentstest.model.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * @author kitttn
 */

interface CountryDetailsLoader {
    fun getCountryDetails(countryName: String): Single<CountryDetails>
}

class CachedCountryDetailsLoader(private val api: CountriesApi, private val room: RoomCountryDatabase) : CountryDetailsLoader {
    override fun getCountryDetails(countryName: String): Single<CountryDetails> {
        return Single.just(true)
                .observeOn(Schedulers.io())
                .flatMap {
                    val existing = room.roomCountryDao().findCountryByName(countryName)
                    if (existing != null) {
                        val languages = room.roomCountryDao().getCountryLanguages(countryName)
                        Single.just(fromDao(existing, languages))
                    } else api.getCountryByFullName(countryName)
                            .map { it.first() }
                }
                .doAfterSuccess {
                    val (dao, languages) = toDao(it)
                    room.roomCountryDao().addCountry(dao)
                    room.roomCountryDao().addCountryLanguages(languages.toTypedArray())
                }
    }
}
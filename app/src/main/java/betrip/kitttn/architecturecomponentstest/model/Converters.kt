package betrip.kitttn.architecturecomponentstest.model

/**
 * @author kitttn
 */

fun fromDao(country: RoomCountry, languages: List<RoomCountryLanguage>) =
        CountryDetails(country.name, country.nativeName, country.region, country.subRegion,
                country.capital, country.isoCode, country.population, country.area, mapOf("de" to country.translation),
                languages.map { fromDao(it) }, listOf(country.lat, country.lng))

fun toDao(country: CountryDetails): Pair<RoomCountry, List<RoomCountryLanguage>> =
        RoomCountry(country.name, country.nativeName, country.region, country.subRegion, country.capital,
                country.population, country.area, country.translations.getOrDefault("de", ""),
                country.isoCode, country.location[0], country.location[1]) to
                country.languages.map { RoomCountryLanguage(country.name, it.name, it.nativeName) }

fun fromDao(language: RoomCountryLanguage) =
        Language(language.languageName, language.nativeName)
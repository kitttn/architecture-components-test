package betrip.kitttn.architecturecomponentstest.services.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import betrip.kitttn.architecturecomponentstest.model.CountriesApi;
import betrip.kitttn.architecturecomponentstest.model.RoomCountry;
import betrip.kitttn.architecturecomponentstest.model.RoomCountryDatabase;
import betrip.kitttn.architecturecomponentstest.model.RoomCountryLanguage;
import betrip.kitttn.architecturecomponentstest.model.entities.CountryDetails;
import betrip.kitttn.architecturecomponentstest.model.entities.Language;
import betrip.kitttn.architecturecomponentstest.services.CountryDetailsLoader;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * @author kitttn
 */

public class CachedCountryDetailsLoader implements CountryDetailsLoader {
    private RoomCountryDatabase room;
    private CountriesApi api;

    public CachedCountryDetailsLoader(RoomCountryDatabase room, CountriesApi api) {
        this.room = room;
        this.api = api;
    }

    @Override public Single<CountryDetails> getCountryDetails(String countryName) {
        return Single.just(true)
                .observeOn(Schedulers.io())
                .flatMap(b -> {
                    RoomCountry existing = room.roomCountryDao().findCountryByName(countryName);
                    if (existing != null) {
                        List<RoomCountryLanguage> languages = room.roomCountryDao().getCountryLanguages(countryName);
                        return Single.just(fromDao(existing, languages));
                    } else return api.getCountryByFullName(countryName)
                            .map(it -> it.get(0));
                })
                .doAfterSuccess(country -> {
                    CountryAndLanguages it = toDao(country);
                    room.roomCountryDao().addCountry(it.country);
                    RoomCountryLanguage[] out = new RoomCountryLanguage[it.languages.size()];
                    room.roomCountryDao().addCountryLanguages(it.languages.toArray(out));
                });
    }

    private CountryDetails fromDao(RoomCountry existing, List<RoomCountryLanguage> languages) {
        CountryDetails details = new CountryDetails();
        details.name = existing.getName();
        details.nativeName = existing.getNativeName();
        details.area = existing.getArea();
        details.capital = existing.getCapital();
        details.isoCode = existing.getIsoCode();
        details.location = Arrays.asList(existing.getLat(), existing.getLng());
        details.population = existing.getPopulation();
        details.region = existing.getRegion();
        details.subRegion = existing.getSubRegion();

        List<Language> result = new ArrayList<>();
        for (RoomCountryLanguage lang : languages)
            result.add(new Language(lang.getLanguageName(), lang.getNativeName()));
        details.languages = result;

        return details;
    }

    private CountryAndLanguages toDao(CountryDetails details) {
        RoomCountry country = new RoomCountry(details.name, details.nativeName, details.region, details.subRegion,
                details.capital, details.population, details.area, "", details.isoCode,
                details.location.get(0), details.location.get(1));

        List<RoomCountryLanguage> languages = new ArrayList<>();
        for (Language language : details.languages)
            languages.add(new RoomCountryLanguage(details.name, language.name, language.nativeName));

        return new CountryAndLanguages(country, languages);
    }

    private class CountryAndLanguages {
        RoomCountry country;
        List<RoomCountryLanguage> languages;

        public CountryAndLanguages(RoomCountry country, List<RoomCountryLanguage> languages) {
            this.country = country;
            this.languages = languages;
        }
    }
}

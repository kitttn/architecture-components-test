package betrip.kitttn.architecturecomponentstest.services.implementation;

import java.util.ArrayList;
import java.util.List;

import betrip.kitttn.architecturecomponentstest.model.CountriesApi;
import betrip.kitttn.architecturecomponentstest.model.entities.CountryName;
import betrip.kitttn.architecturecomponentstest.services.CountryNamesLoader;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.processors.PublishProcessor;

/**
 * @author kitttn
 */

public class RestCountryNamesLoader implements CountryNamesLoader {
    private PublishProcessor<List<CountryName>> countriesProcessor = PublishProcessor.create();
    private List<CountryName> loadedCountries = new ArrayList<>();
    private CountriesApi api;

    public RestCountryNamesLoader(CountriesApi api) {
        this.api = api;
    }

    @Override public Flowable<List<CountryName>> getCountries() {
        return countriesProcessor;
    }

    @Override public Completable fetchCountries(String query) {
        return loadCountries()
                .flatMapObservable(Observable::fromIterable)
                .filter(it -> it.name.toLowerCase().contains(query.toLowerCase()))
                .toList()
                .doOnEvent((list, error) -> {
                    if (error == null) countriesProcessor.onNext(list);
                })
                .toCompletable();
    }

    @Override public Completable fetchAllCountries() {
        return loadCountries()
                .doOnEvent((list, error) -> {
                    if (error == null) countriesProcessor.onNext(list);
                })
                .toCompletable();
    }

    private Single<List<CountryName>> loadCountries() {
        return Single.just(true)
                .flatMap(b -> {
                    if (loadedCountries.isEmpty())
                        return api.getAllCountries()
                                .map(it -> new CountriesCache(it, true));
                    else return Single.just(new CountriesCache(loadedCountries, false));
                })
                .map(it -> {
                    if (it.shouldCache) {
                        loadedCountries.clear();
                        loadedCountries.addAll(it.countries);
                    }
                    return it.countries;
                });
    }

    private class CountriesCache {
        public List<CountryName> countries;
        public boolean shouldCache = false;

        public CountriesCache(List<CountryName> countryNames, boolean shouldCache) {
            this.countries = countryNames;
            this.shouldCache = shouldCache;
        }
    }
}

package betrip.kitttn.architecturecomponentstest.services;

import java.util.List;

import betrip.kitttn.architecturecomponentstest.model.entities.CountryName;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * @author kitttn
 */

public interface CountryNamesLoader {
    Flowable<List<CountryName>> getCountries();

    Completable fetchCountries(String query);

    Completable fetchAllCountries();
}

package betrip.kitttn.architecturecomponentstest.services;

import betrip.kitttn.architecturecomponentstest.model.entities.CountryDetails;
import io.reactivex.Single;

/**
 * @author kitttn
 */

public interface CountryDetailsLoader {
    Single<CountryDetails> getCountryDetails(String countryName);
}

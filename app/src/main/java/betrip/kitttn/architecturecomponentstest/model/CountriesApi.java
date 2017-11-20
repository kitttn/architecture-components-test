package betrip.kitttn.architecturecomponentstest.model;

import java.util.List;

import betrip.kitttn.architecturecomponentstest.model.entities.CountryDetails;
import betrip.kitttn.architecturecomponentstest.model.entities.CountryName;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author kitttn
 */

public interface CountriesApi {
    @GET("name/{partName}?fields=name;nativeName;alpha2Code")
    Single<List<CountryName>> getCountriesList(@Path("partName") String countryPartialName);

    @GET("all?fields=name;nativeName;alpha2Code")
    Single<List<CountryName>> getAllCountries();

    @GET("name/{fullName}?fullText=true")
    Single<List<CountryDetails>> getCountryByFullName(@Path("fullName") String countryFullName);
}
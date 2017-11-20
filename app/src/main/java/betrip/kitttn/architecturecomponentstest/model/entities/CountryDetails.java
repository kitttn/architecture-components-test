package betrip.kitttn.architecturecomponentstest.model.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kitttn
 */

public class CountryDetails {
    public String name = "";
    public String nativeName = "";
    public String region = "";
    public @SerializedName("subregion") String subRegion = "";
    public String capital = "";
    public @SerializedName("alpha2Code") String isoCode = "";
    public Long population = 0L;
    public Long area = 0L;
    public Map<String, String> translations = new HashMap<>();
    public List<Language> languages = new ArrayList<>();
    public @SerializedName("latlng") List<Double> location = new ArrayList<>();

    public String getName() {
        return name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public String getRegion() {
        return region;
    }

    public String getSubRegion() {
        return subRegion;
    }

    public String getCapital() {
        return capital;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public Long getPopulation() {
        return population;
    }

    public Long getArea() {
        return area;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public List<Double> getLocation() {
        return location;
    }
}

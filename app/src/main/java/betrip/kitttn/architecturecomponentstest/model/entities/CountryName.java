package betrip.kitttn.architecturecomponentstest.model.entities;

import com.google.gson.annotations.SerializedName;

/**
 * @author kitttn
 */

public class CountryName {
    public String name = "";
    public String nativeName = "";
    @SerializedName("alpha2Code") public String isoCode = "";

    public CountryName(String name) {
        this.name = name;
        this.nativeName = name;
    }

    public CountryName() {
    }
}

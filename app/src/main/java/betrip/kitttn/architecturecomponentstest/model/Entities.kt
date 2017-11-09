package betrip.kitttn.architecturecomponentstest.model

import com.google.gson.annotations.SerializedName

/**
 * @author kitttn
 */

data class CountryName(val name: String, val nativeName: String = name,
                       @SerializedName("alpha2Code") val isoCode: String = "")
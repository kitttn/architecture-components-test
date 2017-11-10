package betrip.kitttn.architecturecomponentstest.model

import com.google.gson.annotations.SerializedName

/**
 * @author kitttn
 */

data class CountryName(val name: String, val nativeName: String = name,
                       @SerializedName("alpha2Code") val isoCode: String = "")

data class Language(val name: String, val nativeName: String)

data class CountryDetails(val name: String, val nativeName: String, val region: String,
                          @SerializedName("subregion") val subRegion: String, val capital: String,
                          @SerializedName("alpha2Code") val isoCode: String, val population: Long,
                          val area: Long, val translations: Map<String, String>, val languages: List<Language>,
                          @SerializedName("latlng") val location: List<Double>)
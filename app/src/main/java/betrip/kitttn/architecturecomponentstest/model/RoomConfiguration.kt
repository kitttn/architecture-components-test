package betrip.kitttn.architecturecomponentstest.model

import android.arch.persistence.room.*

/**
 * @author kitttn
 */

@Entity(primaryKeys = arrayOf("countryName", "languageName"))
data class RoomCountryLanguage(val countryName: String, val languageName: String, val nativeName: String)

@Entity
data class RoomCountry(
        @PrimaryKey val name: String, val nativeName: String, val region: String,
        val subRegion: String, val capital: String, val population: Long,
        val area: Long, val translation: String,
        val isoCode: String, val lat: Double, val lng: Double)

@Dao
interface RoomCountryDao {
    @Query("select * from RoomCountry where name is (:name) limit 1")
    fun findCountryByName(name: String): RoomCountry?

    @Query("select * from RoomCountryLanguage where countryName is (:countryName)")
    fun getCountryLanguages(countryName: String): List<RoomCountryLanguage>

    @Insert
    fun addCountry(country: RoomCountry)

    @Insert
    fun addCountryLanguages(language: Array<RoomCountryLanguage>)
}

@Database(entities = arrayOf(RoomCountry::class, RoomCountryLanguage::class), version = 1)
abstract class RoomCountryDatabase : RoomDatabase() {
    abstract fun roomCountryDao(): RoomCountryDao
}
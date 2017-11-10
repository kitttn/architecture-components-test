package betrip.kitttn.architecturecomponentstest.di.components

import betrip.kitttn.architecturecomponentstest.di.modules.AppModule
import betrip.kitttn.architecturecomponentstest.model.RoomCountryDatabase
import betrip.kitttn.architecturecomponentstest.services.CountryDetailsLoader
import betrip.kitttn.architecturecomponentstest.services.CountryNamesLoader
import dagger.Component
import javax.inject.Singleton

/**
 * @author kitttn
 */

@Singleton @Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    var namesLoader: CountryNamesLoader
    val detailsLoader: CountryDetailsLoader
    val database: RoomCountryDatabase
}
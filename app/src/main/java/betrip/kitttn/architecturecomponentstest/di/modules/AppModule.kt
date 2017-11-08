package betrip.kitttn.architecturecomponentstest.di.modules

import betrip.kitttn.architecturecomponentstest.services.CountryLoader
import betrip.kitttn.architecturecomponentstest.services.TestCountryLoaderRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author kitttn
 */

@Module
class AppModule {
    @Provides @Singleton
    fun provideSmth() = "Smth"

    @Provides @Singleton
    fun provideCountryLoader(): CountryLoader = TestCountryLoaderRepository()
}
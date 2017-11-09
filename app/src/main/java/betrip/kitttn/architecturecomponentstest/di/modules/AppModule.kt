package betrip.kitttn.architecturecomponentstest.di.modules

import betrip.kitttn.architecturecomponentstest.model.CountriesApi
import betrip.kitttn.architecturecomponentstest.services.CountryLoader
import betrip.kitttn.architecturecomponentstest.services.RestCountryLoaderRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * @author kitttn
 */

@Module
class AppModule(private val serverUrl: String = "https://restcountries.eu/rest/v2/") {
    @Provides @Singleton
    fun providesApi(): CountriesApi {
        val interceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val okHttp = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(serverUrl)
                .client(okHttp)
                .build()

        return retrofit.create(CountriesApi::class.java)
    }

    @Provides @Singleton
    fun provideCountryLoader(api: CountriesApi): CountryLoader = RestCountryLoaderRepository(api)
}
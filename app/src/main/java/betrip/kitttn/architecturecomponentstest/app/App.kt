package betrip.kitttn.architecturecomponentstest.app

import android.app.Application
import betrip.kitttn.architecturecomponentstest.di.components.AppComponent
import betrip.kitttn.architecturecomponentstest.di.components.DaggerAppComponent
import betrip.kitttn.architecturecomponentstest.di.modules.AppModule

/**
 * @author kitttn
 */

class App : Application() {
    companion object {
        lateinit var graph: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        graph = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}
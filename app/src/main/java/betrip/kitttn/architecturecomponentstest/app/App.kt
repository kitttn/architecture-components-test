package betrip.kitttn.architecturecomponentstest.app

import android.app.Application
import betrip.kitttn.architecturecomponentstest.di.components.DaggerAppComponent

/**
 * @author kitttn
 */

class App : Application() {
    companion object {
        val graph by lazy {
            DaggerAppComponent.create()
        }
    }
}
package betrip.kitttn.architecturecomponentstest.di.components

import betrip.kitttn.architecturecomponentstest.di.modules.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * @author kitttn
 */

@Singleton @Component(modules = arrayOf(AppModule::class))
interface AppComponent {

}
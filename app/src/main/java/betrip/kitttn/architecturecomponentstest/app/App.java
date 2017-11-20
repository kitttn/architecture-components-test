package betrip.kitttn.architecturecomponentstest.app;

import android.app.Application;

import betrip.kitttn.architecturecomponentstest.di.components.AppComponent;
import betrip.kitttn.architecturecomponentstest.di.components.DaggerAppComponent;
import betrip.kitttn.architecturecomponentstest.di.modules.AppModule;

/**
 * @author kitttn
 */

public class App extends Application {
    public static AppComponent component;

    @Override public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this, "https://restcountries.eu/rest/v2/"))
                .build();
    }
}

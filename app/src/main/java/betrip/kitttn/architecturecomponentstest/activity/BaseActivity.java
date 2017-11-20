package betrip.kitttn.architecturecomponentstest.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import betrip.kitttn.architecturecomponentstest.ExtensionsKt;
import betrip.kitttn.architecturecomponentstest.app.App;
import betrip.kitttn.architecturecomponentstest.di.components.DaggerModelViewComponent;
import betrip.kitttn.architecturecomponentstest.di.components.ModelViewComponent;
import betrip.kitttn.architecturecomponentstest.di.modules.LifecycleAwareModule;

/**
 * @author kitttn
 */

public class BaseActivity extends AppCompatActivity {
    public @Nullable ModelViewComponent lifecycleComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleComponent = DaggerModelViewComponent.builder()
                .appComponent(App.component)
                .lifecycleAwareModule(new LifecycleAwareModule())
                .build();
    }

    public int getFlagIdByCode(String code) {
        return ExtensionsKt.getFlagResIdByCode(this, code);
    }
}

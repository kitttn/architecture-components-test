package betrip.kitttn.architecturecomponentstest.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import betrip.kitttn.architecturecomponentstest.R;
import betrip.kitttn.architecturecomponentstest.view.CountryDetailsFragment;
import betrip.kitttn.architecturecomponentstest.view.SearchFragment;
import betrip.kitttn.architecturecomponentstest.vm.ActivityViewModel;

/**
 * @author kitttn
 */

public class MainActivity extends BaseActivity {
    private ActivityViewModel viewModel;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(ActivityViewModel.class);
        if (viewModel.currentState == ActivityViewModel.State.STARTED)
            openSearch();
    }

    public void openSearch() {
        viewModel.openSearch();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new SearchFragment(), "search")
                .commit();
    }

    public void openCountryDetails(String countryName) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, CountryDetailsFragment.newInstance(countryName))
                .addToBackStack("details")
                .commit();
    }

    @Override protected void onStop() {
        if (isFinishing())
            viewModel.closeApp();
        super.onStop();
    }
}

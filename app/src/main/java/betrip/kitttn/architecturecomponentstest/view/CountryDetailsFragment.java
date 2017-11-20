package betrip.kitttn.architecturecomponentstest.view;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import betrip.kitttn.architecturecomponentstest.R;
import betrip.kitttn.architecturecomponentstest.activity.BaseActivity;
import betrip.kitttn.architecturecomponentstest.di.modules.Factory;
import betrip.kitttn.architecturecomponentstest.model.entities.CountryDetails;
import betrip.kitttn.architecturecomponentstest.model.entities.Language;
import betrip.kitttn.architecturecomponentstest.vm.CountryLoadErrorState;
import betrip.kitttn.architecturecomponentstest.vm.CountryLoadSuccessState;
import betrip.kitttn.architecturecomponentstest.vm.CountryLoadingState;
import betrip.kitttn.architecturecomponentstest.vm.SelectedCountryState;
import betrip.kitttn.architecturecomponentstest.vm.SelectedCountryViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author kitttn
 */

public class CountryDetailsFragment extends Fragment {
    private String countryName = "";
    @Inject Factory<SelectedCountryViewModel> factory;
    private CompositeDisposable composite = new CompositeDisposable();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.mapView) MapView mapView;
    @BindView(R.id.countryDetailsLoader) ProgressBar loader;
    @BindView(R.id.countryDetailsName) TextView countryDetailsName;
    @BindView(R.id.countryDetailsArea) TextView countryDetailsArea;
    @BindView(R.id.countryDetailsCapital) TextView countryDetailsCapital;
    @BindView(R.id.countryDetailsLanguages) TextView countryDetailsLanguages;
    @BindView(R.id.countryDetailsPopulation) TextView countryDetailsPopulation;
    @BindView(R.id.countryDetailsRegion) TextView countryDetailsRegion;

    public static CountryDetailsFragment newInstance(String _countryName) {
        CountryDetailsFragment fragment = new CountryDetailsFragment();
        Bundle b = new Bundle();
        b.putString("name", _countryName);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_country_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override public void onDestroy() {
        mapView.onDestroy();
        composite.clear();
        super.onDestroy();
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null)
            countryName = getArguments().getString("name", "");

        BaseActivity activity = ((BaseActivity) getActivity());
        if (activity != null) {
            if (activity.lifecycleComponent != null)
                activity.lifecycleComponent.inject(this);

            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

            SelectedCountryViewModel vm = ViewModelProviders.of(activity, factory).get(SelectedCountryViewModel.class);
            Disposable d = vm.countryDetails()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::parseState, err -> this.parseState(new CountryLoadErrorState(err.getLocalizedMessage())));

            composite.add(d);

            vm.fetchCountryDetails(countryName);
            mapView.onCreate(savedInstanceState);
            toolbar.setNavigationOnClickListener(v -> activity.getSupportFragmentManager().popBackStack());
        }
    }

    private void parseState(SelectedCountryState state) {
        loader.setVisibility(View.GONE);
        if (state instanceof CountryLoadingState) loader.setVisibility(View.VISIBLE);
        if (state instanceof CountryLoadSuccessState)
            bindFragment(((CountryLoadSuccessState) state).getCountry());
        if (state instanceof CountryLoadErrorState)
            Toast.makeText(getActivity(), ((CountryLoadErrorState) state).getError(), Toast.LENGTH_LONG).show();
    }

    private void bindFragment(CountryDetails countryDetails) {
        countryDetailsArea.setText(getString(R.string.area_1_d, countryDetails.getArea()));
        countryDetailsCapital.setText(getString(R.string.capital_1_d, countryDetails.getCapital()));
        List<String> languages = new ArrayList<>();
        for (Language language : countryDetails.getLanguages())
            languages.add(getString(R.string._1_s_2_s, language.name, language.nativeName));
        String lngs = TextUtils.join(", ", languages);
        countryDetailsLanguages.setText(getString(R.string.languages_1_s, lngs));
        countryDetailsName.setText(getString(R.string._1_s_2_s, countryDetails.getName(), countryDetails.getNativeName()));
        countryDetailsPopulation.setText(getString(R.string.population_1_d, countryDetails.getPopulation()));
        countryDetailsRegion.setText(getString(R.string._1_s_2_s, countryDetails.getRegion(), countryDetails.getSubRegion()));

        mapView.getMapAsync(map -> {
            LatLng latLng = new LatLng(countryDetails.getLocation().get(0), countryDetails.getLocation().get(1));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f));

            int flagCode = ((BaseActivity) getActivity()).getFlagIdByCode(countryDetails.getIsoCode());
            if (flagCode != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), flagCode);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.3),
                        (int) (bitmap.getHeight() * 0.3), true);

                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                        .position(latLng));

                scaledBitmap.recycle();
                bitmap.recycle();
            }
        });
    }
}

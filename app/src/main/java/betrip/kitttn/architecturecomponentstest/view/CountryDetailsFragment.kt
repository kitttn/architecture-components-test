package betrip.kitttn.architecturecomponentstest.view

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.activity.BaseActivity
import betrip.kitttn.architecturecomponentstest.di.modules.Factory
import betrip.kitttn.architecturecomponentstest.getFlagResIdByCode
import betrip.kitttn.architecturecomponentstest.model.CountryDetails
import betrip.kitttn.architecturecomponentstest.plusAssign
import betrip.kitttn.architecturecomponentstest.vm.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_country_details.*
import javax.inject.Inject

/**
 * @author kitttn
 */

class CountryDetailsFragment : Fragment() {
    private val countryName: String by lazy { arguments.getString("name", "") }
    @Inject lateinit var factory: Factory<SelectedCountryViewModel>

    private val selectedCountry by lazy { ViewModelProviders.of(activity, factory).get(SelectedCountryViewModel::class.java) }
    private val composite = CompositeDisposable()

    companion object {
        private val TAG = CountryDetailsFragment::class.java.simpleName
        fun newInstance(_countryName: String): CountryDetailsFragment =
                CountryDetailsFragment().apply {
                    arguments = Bundle().apply { putString("name", _countryName) }
                }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_country_details, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(activity as BaseActivity) {
            lifecycleComponent.inject(this@CountryDetailsFragment)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        composite += selectedCountry.countryDetails()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::parseState, { parseState(CountryLoadErrorState(it.localizedMessage)) })

        selectedCountry.fetchCountryDetails(countryName)

        mapView?.onCreate(savedInstanceState)
        toolbar.setNavigationOnClickListener { activity.supportFragmentManager.popBackStack() }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    private fun parseState(state: SelectedCountryState) {
        countryDetailsLoader?.visibility = View.GONE
        when (state) {
            is CountryLoadingState -> countryDetailsLoader?.visibility = View.VISIBLE
            is CountryLoadSuccessState -> bindFragment(state.country)
            is CountryLoadErrorState -> Toast.makeText(activity, state.error, Toast.LENGTH_LONG).show()
        }
    }

    private fun bindFragment(countryDetails: CountryDetails) {
        Log.i(TAG, "bindFragment: Country details: $countryDetails")
        countryDetailsArea.text = getString(R.string.area_1_d, countryDetails.area)
        countryDetailsCapital.text = getString(R.string.capital_1_d, countryDetails.capital)
        countryDetailsLanguages.text = getString(R.string.languages_1_s,
                countryDetails.languages.joinToString(", ") { getString(R.string._1_s_2_s, it.name, it.nativeName) })
        countryDetailsName.text = getString(R.string._1_s_2_s, countryDetails.name, countryDetails.nativeName)
        countryDetailsPopulation.text = getString(R.string.population_1_d, countryDetails.population)
        countryDetailsRegion.text = getString(R.string._1_s_2_s, countryDetails.region, countryDetails.subRegion)
        countryDetailsTranslation.text = getString(R.string.in_german_1_s, countryDetails.translations.getOrDefault("de", ""))

        mapView?.getMapAsync { map ->
            val latLng = LatLng(countryDetails.location[0], countryDetails.location[1])
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))

            val flagCode = activity.getFlagResIdByCode(countryDetails.isoCode)
            if (flagCode != 0) {
                val bitmap = BitmapFactory.decodeResource(resources, flagCode)
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.width * 0.3).toInt(),
                        (bitmap.height * 0.3).toInt(), true)

                map.addMarker(MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                        .position(latLng))

                scaledBitmap.recycle()
                bitmap.recycle()
            }
        }
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        composite.clear()
        super.onDestroy()
    }
}
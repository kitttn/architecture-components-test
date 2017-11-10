package betrip.kitttn.architecturecomponentstest.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.activity.BaseActivity
import betrip.kitttn.architecturecomponentstest.vm.SelectedCountryViewModel
import kotlinx.android.synthetic.main.fragment_country_details.*
import javax.inject.Inject

/**
 * @author kitttn
 */

class CountryDetailsFragment : Fragment() {
    private lateinit var countryName: String
    @Inject lateinit var selectedCountry: SelectedCountryViewModel

    companion object {
        private val TAG = CountryDetailsFragment::class.java.simpleName
        fun newInstance(_countryName: String): CountryDetailsFragment =
                CountryDetailsFragment().apply { countryName = _countryName }
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
        }
    }
}
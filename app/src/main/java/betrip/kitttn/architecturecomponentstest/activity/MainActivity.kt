package betrip.kitttn.architecturecomponentstest.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.view.CountryDetailsFragment
import betrip.kitttn.architecturecomponentstest.view.SearchFragment
import betrip.kitttn.architecturecomponentstest.vm.ActivityViewModel
import betrip.kitttn.architecturecomponentstest.vm.AppStartedState

/**
 * @author kitttn
 */

class MainActivity : BaseActivity() {
    private val activityViewModel by lazy { ViewModelProviders.of(this).get(ActivityViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (activityViewModel.currentState is AppStartedState)
            openSearch()
    }

    private fun openSearch() {
        activityViewModel.openSearch()
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SearchFragment(), "search")
                .commit()
    }

    fun openCountryDetails(countryName: String) {
        activityViewModel.openCountryDetails(countryName)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CountryDetailsFragment.newInstance(countryName))
                .addToBackStack("details")
                .commit()
    }

    override fun onStop() {
        if (isFinishing)
            activityViewModel.closeApp()
        super.onStop()
    }
}
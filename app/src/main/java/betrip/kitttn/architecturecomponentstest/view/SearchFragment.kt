package betrip.kitttn.architecturecomponentstest.view

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.activity.BaseActivity
import betrip.kitttn.architecturecomponentstest.di.LifecycleAware
import betrip.kitttn.architecturecomponentstest.di.modules.Factory
import betrip.kitttn.architecturecomponentstest.plusAssign
import betrip.kitttn.architecturecomponentstest.view.adapters.CountryNameFlagAdapter
import betrip.kitttn.architecturecomponentstest.vm.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

/**
 * @author kitttn
 */

@LifecycleAware
class SearchFragment : Fragment() {
    companion object {
        private val TAG = "SearchFragment"
    }

    @Inject lateinit var factory: Factory<SearchResultsViewModel>
    private val composite = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).lifecycleComponent.inject(this)

        val enteredTextVM = ViewModelProviders.of(this).get(EnteredTextViewModel::class.java)
        val searchResultsVM = ViewModelProviders.of(activity, factory).get(SearchResultsViewModel::class.java)
        Log.i(TAG, "onViewCreated: Factory: $factory")
        Log.i(TAG, "onViewCreated: VM instance: $searchResultsVM")
        Log.i(TAG, "onViewCreated: Fragment $this is created!")

        searchBtn.setOnClickListener { searchResultsVM.startSearch(searchText.text.toString()) }

        searchResultsRV.layoutManager = LinearLayoutManager(activity)
        val adapter = CountryNameFlagAdapter(mutableListOf())
        searchResultsRV.adapter = adapter

        composite += searchResultsVM.getSearchResults()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ parseSearchResultResponse(it, adapter) }, Throwable::printStackTrace)
    }

    override fun onStop() {
        super.onStop()
        composite.clear()
        Log.i(TAG, "onStop: Fragment $this is stopped!")
    }

    private fun parseSearchResultResponse(response: SearchResultState, adapter: CountryNameFlagAdapter) {
        Log.i(TAG, "parseSearchResultResponse: Got new VM state: $response")
        when (response) {
            is SearchResultLoading -> refreshLayout.isRefreshing = response.loading
            is SearchResultError -> Toast.makeText(activity, "${response.errorCode}; Reason: ${response.errorReason}", Toast.LENGTH_LONG).show()
            is SearchResultSuccess -> {
                Toast.makeText(activity, "Complete!", Toast.LENGTH_SHORT).show()
                with(adapter) {
                    countries.clear()
                    countries.addAll(response.data.map { it.name })
                    notifyDataSetChanged()
                }
            }
        }
    }
}
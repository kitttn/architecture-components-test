package betrip.kitttn.architecturecomponentstest.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.activity.BaseActivity
import betrip.kitttn.architecturecomponentstest.di.LifecycleAware
import betrip.kitttn.architecturecomponentstest.di.modules.Factory
import betrip.kitttn.architecturecomponentstest.model.ViewCountryName
import betrip.kitttn.architecturecomponentstest.plusAssign
import betrip.kitttn.architecturecomponentstest.view.adapters.CountryNameFlagAdapter
import betrip.kitttn.architecturecomponentstest.vm.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author kitttn
 */

@LifecycleAware
class SearchFragment : Fragment(), BackPressHandable {
    companion object {
        private val TAG = "SearchFragment"
    }

    @Inject lateinit var factory: Factory<SearchResultsViewModel>
    private var enteredTextVM: EnteredTextViewModel? = null
    private val composite = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(activity as BaseActivity) {
            lifecycleComponent.inject(this@SearchFragment)
            setSupportActionBar(toolbar)
        }

        enteredTextVM = ViewModelProviders.of(activity).get(EnteredTextViewModel::class.java)
        val searchResultsVM = ViewModelProviders.of(activity, factory).get(SearchResultsViewModel::class.java)
        Log.i(TAG, "onViewCreated: Factory: $factory")
        Log.i(TAG, "onViewCreated: VM instance: $searchResultsVM")
        Log.i(TAG, "onViewCreated: Fragment $this is created!")

        searchResultsRV.layoutManager = LinearLayoutManager(activity)
        val adapter = CountryNameFlagAdapter(mutableListOf())
        searchResultsRV.adapter = adapter

        composite += searchResultsVM.getSearchResults()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ parseSearchResultResponse(it, adapter) }, Throwable::printStackTrace)

        val textVm = enteredTextVM ?: return

        composite += textVm.getEnteredText()
                .observeOn(Schedulers.io())
                .debounce(200, TimeUnit.MILLISECONDS, Schedulers.io())
                .subscribe(searchResultsVM::startSearch, Throwable::printStackTrace)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.search_menu, menu)

        val menuItem = menu?.findItem(R.id.search) ?: return
        val searchView = menuItem.actionView as? SearchView ?: return

        searchView.maxWidth = Int.MAX_VALUE

        val textVm = enteredTextVM ?: return
        composite += textVm.getLastQuery()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.i(TAG, "onCreateOptionsMenu: Last text: $it")
                    if (it.isNotEmpty()) {
                        menuItem.expandActionView()
                        searchView.setQuery(it, false)
                    }

                }, Throwable::printStackTrace)

        menuItem.setOnActionExpandListener(CollapseExpandListener({ toolbar.setBackgroundResource(R.color.colorPrimary) },
                { toolbar.setBackgroundResource(R.color.white) }))

        searchView.setOnQueryTextListener(QueryListener(textVm::textChanged))
    }

    override fun onBackPressProcessed(): Boolean {
        return false
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
            is SearchResultError -> {
                /*Toast.makeText(activity, "${response.errorCode}; Reason: ${response.errorReason}", Toast.LENGTH_LONG)
                        .show()*/
                adapter.countries.clear()
                adapter.notifyDataSetChanged()
            }
            is SearchResultSuccess -> {
                /*Toast.makeText(activity, "Complete!", Toast.LENGTH_SHORT).show()*/
                with(adapter) {
                    countries.clear()
                    countries.addAll(response.data.map {
                        val mergedName = getString(R.string.country_name_template, it.name, it.nativeName)
                        ViewCountryName(mergedName, getResourceId(it.isoCode.toLowerCase()))
                    })
                    notifyDataSetChanged()
                }
            }
        }
    }

    private @DrawableRes fun getResourceId(isoCode: String): Int {
        val id = resources.getIdentifier(isoCode, "drawable", activity.packageName)
        if (id == 0) {
            Log.w(TAG, "getResourceId: Resource for $isoCode not found!")
        }
        return id
    }
}

class CollapseExpandListener(private val collapse: () -> Unit, private val expand: () -> Unit) : MenuItem.OnActionExpandListener {
    override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
        expand()
        return true
    }

    override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
        collapse()
        return true
    }
}

class QueryListener(private val queryChanged: (String) -> Unit) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?) = false

    override fun onQueryTextChange(newText: String?): Boolean {
        queryChanged(newText ?: "")
        return true
    }
}
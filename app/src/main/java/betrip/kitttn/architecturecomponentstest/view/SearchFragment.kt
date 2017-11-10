package betrip.kitttn.architecturecomponentstest.view

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.activity.BaseActivity
import betrip.kitttn.architecturecomponentstest.di.LifecycleAware
import betrip.kitttn.architecturecomponentstest.di.modules.Factory
import betrip.kitttn.architecturecomponentstest.getFlagResIdByCode
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
class SearchFragment : Fragment() {
    companion object {
        private val TAG = "SearchFragment"
    }

    @Inject lateinit var factory: Factory<SearchResultsViewModel>
    private val composite = CompositeDisposable()
    private val adapter by lazy {
        CountryNameFlagAdapter(mutableListOf(), this::openDetailsFragment)
    }

    private var searchView: SearchView? = null
    private var menuItem: MenuItem? = null

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

        Log.i(TAG, "onViewCreated: Fragment $this is created!")

        searchResultsRV.layoutManager = LinearLayoutManager(activity)
        searchResultsRV.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.search_menu, menu)

        menuItem = menu?.findItem(R.id.search) ?: return
        searchView = menuItem?.actionView as? SearchView ?: return

        searchView?.maxWidth = Int.MAX_VALUE
        bindViewModel()
    }

    private fun openDetailsFragment(name: String) {
        activity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, CountryDetailsFragment.newInstance(name))
                .addToBackStack("details")
                .commit()
    }

    private fun bindViewModel() {
        val textVm = ViewModelProviders.of(activity).get(EnteredTextViewModel::class.java)
        val searchResultsVM = ViewModelProviders.of(activity, factory).get(SearchResultsViewModel::class.java)

        refreshLayout.setOnRefreshListener {
            searchView?.setQuery("", false)
        }

        menuItem?.setOnActionExpandListener(CollapseExpandListener({ toolbar.setBackgroundResource(R.color.colorPrimary) },
                { toolbar.setBackgroundResource(R.color.white) }))

        composite += searchResultsVM.getSearchResults()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ parseSearchResultResponse(it, adapter, searchResultsVM) }, Throwable::printStackTrace)

        composite += textVm.getEnteredText()
                .observeOn(Schedulers.io())
                .debounce(400, TimeUnit.MILLISECONDS, Schedulers.io())
                .subscribe(searchResultsVM::startSearch, Throwable::printStackTrace)

        composite += textVm.getLastQuery()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.i(TAG, "onCreateOptionsMenu: Last text: $it")
                    if (it.isNotEmpty()) {
                        menuItem?.expandActionView()
                        searchView?.setQuery(it, false)
                    }

                    searchView?.setOnQueryTextListener(QueryListener(textVm::textChanged))

                }, Throwable::printStackTrace)
    }

    private fun unbindViewModel() {
        menuItem?.setOnActionExpandListener(null)
        searchView?.setOnQueryTextListener(null)

        menuItem = null
        searchView = null

        composite.clear()
    }

    override fun onDestroy() {
        unbindViewModel()
        super.onDestroy()
        Log.i(TAG, "onDestroy: Fragment $this destroyed!")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: Fragment $this is stopped!")
    }

    private fun parseSearchResultResponse(response: SearchResultState,
                                          adapter: CountryNameFlagAdapter,
                                          searchVm: SearchResultsViewModel) {
        Log.i(TAG, "parseSearchResultResponse: Frag id: $this; Got new VM state: $response")

        refreshLayout.isRefreshing = false
        when (response) {
            is InitialSearchResultState -> searchVm.startSearch("")
            is SearchResultLoading -> refreshLayout.isRefreshing = true
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
                        ViewCountryName(mergedName, it.name, activity.getFlagResIdByCode(it.isoCode))
                    })
                    notifyDataSetChanged()
                }
            }
        }
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
    companion object {
        private val TAG = QueryListener::class.java.simpleName
    }

    override fun onQueryTextSubmit(query: String?) = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText == null)
            return true

        Log.i(TAG, "onQueryTextChange: Querying $newText...")
        queryChanged(newText)
        return true
    }
}
package ru.okcode.currencyconverter.ui.editcurrencylist

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ru.okcode.currencyconverter.R
import ru.okcode.currencyconverter.data.model.ConfiguredCurrency
import ru.okcode.currencyconverter.mvibase.MviView
import ru.okcode.currencyconverter.ui.RatesListActivity
import ru.okcode.currencyconverter.ui.editcurrencylist.EditCurrenciesListIntent.*

@AndroidEntryPoint
class EditCurrenciesListActivity : AppCompatActivity(),
    MviView<EditCurrenciesListIntent, EditCurrenciesListViewState> {

    private val viewModel: EditCurrenciesListViewModel by viewModels()
    private val disposables = CompositeDisposable()

    // Intents subjects
    private val addPublisher =
        PublishSubject.create<AddCurrencyIntent>()

    private val movePublisher =
        PublishSubject.create<MoveCurrencyIntent>()

    private val removePublisher =
        PublishSubject.create<RemoveCurrencyIntent>()

    // View elements
    private lateinit var coordinatorLayout: CoordinatorLayout

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_currencies_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        coordinatorLayout = findViewById(R.id.edit_list_coordinator)

        // RecyclerView Edit currencies list
        val recyclerView: RecyclerView = findViewById(R.id.currencies_recyclerview)
        val adapter = EditCurrenciesListAdapter()
        val layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // FAB
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            TODO()
            //start activity or dialog fragment
        }
    }

    /**
     * onStart
     */
    override fun onStart() {
        super.onStart()
        disposables.add(viewModel.states().subscribe(this::render))
        viewModel.processIntents(intents())
    }

    /**
     * onStop
     */
    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    /**
     * onOptionsItemSelected
     */
    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo((Intent(this, RatesListActivity::class.java)))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    /**
     * intents
     */
    override fun intents(): Observable<EditCurrenciesListIntent> {
        return Observable.merge(
            addIntent(),
            moveIntent(),
            removeIntent()
        )
    }

    private fun addIntent(): Observable<AddCurrencyIntent> {
        return addPublisher
    }

    private fun moveIntent(): Observable<MoveCurrencyIntent> {
        return movePublisher
    }

    private fun removeIntent(): Observable<RemoveCurrencyIntent> {
        return removePublisher
    }

    /**
     * render
     */
    override fun render(state: EditCurrenciesListViewState) {
        if (state.error != null) {
            renderError(state.error)
        } else {
            renderData(state.currencies)
        }
    }

    private fun renderError(error: Throwable) {
        Snackbar.make(
            coordinatorLayout,
            error.localizedMessage ?: "Unknown error",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun renderData(currencies: List<ConfiguredCurrency>) {

    }
}

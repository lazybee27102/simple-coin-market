package market.coin.simple.simplecoinmarket.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.jakewharton.rxbinding2.view.RxView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import market.coin.simple.simplecoinmarket.custom.EndlessRecyclerViewScrollListener;
import market.coin.simple.simplecoinmarket.custom.LinearItemDecoration;
import market.coin.simple.simplecoinmarket.R;
import market.coin.simple.simplecoinmarket.api.RetrofitHelper;
import market.coin.simple.simplecoinmarket.custom.ScrollAwareFabBehavior;
import market.coin.simple.simplecoinmarket.api.SimpleCoinMarketApi;
import market.coin.simple.simplecoinmarket.adapter.CoinAdapter;
import market.coin.simple.simplecoinmarket.database.CoinManager;
import market.coin.simple.simplecoinmarket.database.DatabaseHelper;
import market.coin.simple.simplecoinmarket.databinding.ActivityMainBinding;
import market.coin.simple.simplecoinmarket.model.Coin;
import market.coin.simple.simplecoinmarket.model.CoinList;
import market.coin.simple.simplecoinmarket.model.Coin_Table;
import market.coin.simple.simplecoinmarket.util.ErrorUtil;
import market.coin.simple.simplecoinmarket.util.ValidateUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final int MAX_COIN_PER_PAGE = 500;

    private ActivityMainBinding mBinding;

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private final SimpleCoinMarketApi mApi = RetrofitHelper.getInstance();

    private CoinAdapter mAdapter;

    private EndlessRecyclerViewScrollListener mScrollListener;

    private List<Coin> mCoinList;

    private int mNextPage = 0;

    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupViews();
        refreshCoinList();
        handleEvents();
    }

    private void refreshCoinList() {
        mCompositeDisposable.add(ReactiveNetwork.observeInternetConnectivity()
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mCompositeDisposable.add(DatabaseHelper.clearTable(Coin.class)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .concatMap(transaction -> callGetCoinListApi(0))
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe(disposable -> setSwipeRefreshing(true))
                                .doOnTerminate(() -> setSwipeRefreshing(false))
                                .subscribe(this::setupNewAdapter, this::showErrorLayout));
                    } else {
                        showNoInternetLayout();
                    }
                }, Throwable::printStackTrace));
    }

    private void setSwipeRefreshing(boolean b) {
        mBinding.layoutSwipe.setRefreshing(b);
    }

    private void setupNewAdapter(CoinList coinList) {
        mNextPage = 1;

        resetScrollState();

        if (mCoinList == null) {
            mCoinList = coinList.getCoinList();
        } else {
            mCoinList.clear();
            mCoinList.addAll(coinList.getCoinList());
        }

        if (mAdapter == null) {
            mAdapter = new CoinAdapter(this, mCoinList);
            mBinding.rvCoins.setAdapter(mAdapter);
            coinClicks();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void coinClicks() {

    }

    private void resetScrollState() {
        if (mScrollListener != null) {
            mScrollListener.resetState();
        }
    }

    private Observable<CoinList> callGetCoinListApi(int page) {
        return mApi.getCoinList(page, MAX_COIN_PER_PAGE)
                .map(CoinList::valueOf);
        //.doOnNext(coinList -> CoinManager.addAddCoins(coinList.getCoinList()));
    }

    private void setupViews() {
        setupToolbar();
        setupRecyclerView();
        setupFab();
    }

    private void setupFab() {
        CoordinatorLayout.LayoutParams layoutParams
                = (CoordinatorLayout.LayoutParams) mBinding.fabScrollUp.getLayoutParams();
        layoutParams.setBehavior(new ScrollAwareFabBehavior());
        mBinding.fabScrollUp.setLayoutParams(layoutParams);
    }

    private void setupRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mBinding.rvCoins.setLayoutManager(mLayoutManager);
        mBinding.rvCoins.setHasFixedSize(true);
        //mBinding.rvCoins.addItemDecoration(new LinearItemDecoration(getResources().getDimensionPixelSize(R.dimen.ico_size_normal)));
    }

    private void setupToolbar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setTitle("");
    }

    private void handleEvents() {
        mCompositeDisposable.add(searchTextChanges()
                .debounce(250, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.d(TAG, "onQueryTextChange: " + s);
                    mAdapter.getFilter().filter(s);
                }, Throwable::printStackTrace));

/*        mCompositeDisposable.add(searchTextSubmit().subscribe(s -> {
            Log.d(TAG, "handleEvents: " + s);
        }, Throwable::printStackTrace));*/

        mCompositeDisposable.add(searchTextToggle().subscribe(aBoolean -> {
            Log.d(TAG, "handleEvents: " + aBoolean);
        }, Throwable::printStackTrace));


        mCompositeDisposable.add(refreshes()
                .filter(isRefreshing -> isRefreshing)
                .subscribe(aBoolean -> {
                    mBinding.layoutSwipe.setRefreshing(false);
                    callGetCoinListApi(0);
                }, Throwable::printStackTrace));

        mCompositeDisposable.add(recyclerViewScrolls()
                .subscribe(integer -> loadMore(), Throwable::printStackTrace));

        /*mCompositeDisposable.add(pointHistoryClicks().subscribe(o ->
                startPointHistoryActivity(), Throwable::printStackTrace));*/

        mCompositeDisposable.add(fabClicks().subscribe(o -> smoothScrollToFirstPosition(),
                Throwable::printStackTrace));
    }

    private void loadMore() {
       /* mCompositeDisposable.add(ReactiveNetwork.observeInternetConnectivity()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isInternetAvailable -> {
                    if (isInternetAvailable) {
                        mCompositeDisposable.add(callGetCoinListApi(mNextPage * MAX_COIN_PER_PAGE)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe(disposable -> setSwipeRefreshing(true))
                                .doOnTerminate(() -> setSwipeRefreshing(false))
                                .subscribe(this::insertCoinList, this::showErrorLayout));
                    } else {
                        showNoInternetLayout();
                    }
                }, Throwable::printStackTrace));*/
    }

    private void showErrorLayout(Throwable throwable) {
        Snackbar.make(mBinding.getRoot(), ErrorUtil.getErrorMessage(this, throwable), Snackbar.LENGTH_INDEFINITE).show();
    }

    private void showNoInternetLayout() {
        Snackbar.make(mBinding.getRoot(), R.string.error_no_internet, Snackbar.LENGTH_INDEFINITE).show();
    }

    private void insertCoinList(CoinList coinList) {
        mNextPage++;
        int currentSize = mCoinList.size();
        mCoinList.addAll(coinList.getCoinList());
        mAdapter.notifyItemInserted(currentSize);
        mAdapter.notifyItemRangeChanged(currentSize, mCoinList.size());
    }

    private Observable<Integer> recyclerViewScrolls() {
        return Observable.create(e -> {
            if (mScrollListener == null) {
                mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        e.onNext(page);
                    }
                };
            }

            mBinding.rvCoins.addOnScrollListener(mScrollListener);
        });
    }

    private Observable<Object> fabClicks() {
        return RxView.clicks(mBinding.fabScrollUp);
    }

    private void smoothScrollToFirstPosition() {
        mBinding.rvCoins.scrollToPosition(0);
        mBinding.fabScrollUp.setVisibility(View.INVISIBLE);
    }

    private Observable<Boolean> refreshes() {
        return Observable.create(e -> mBinding.layoutSwipe.setOnRefreshListener(() ->
                e.onNext(mBinding.layoutSwipe.isRefreshing())));
    }

    private Observable<String> searchTextChanges() {
        return Observable.create(e ->
                mBinding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        e.onNext(newText);
                        return true;
                    }
                }));
    }

    private Observable<String> searchTextSubmit() {
        return Observable.create(e ->
                mBinding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        e.onNext(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                }));
    }

    private Observable<Boolean> searchTextToggle() {
        return Observable.create(e -> mBinding.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                e.onNext(true);
            }

            @Override
            public void onSearchViewClosed() {
                e.onNext(false);
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mBinding.searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (mBinding.searchView.isSearchOpen()) {
            mBinding.searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}

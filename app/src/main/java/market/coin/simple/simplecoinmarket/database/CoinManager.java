package market.coin.simple.simplecoinmarket.database;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import market.coin.simple.simplecoinmarket.model.Coin;
import market.coin.simple.simplecoinmarket.model.Coin_Table;

public class CoinManager {
    private static final String TAG = CoinManager.class.getSimpleName();

    private CoinManager() {
    }

    public static Observable<List<Coin>> getCoinList() {
        return RXSQLite.rx(
                SQLite.select().from(Coin.class).orderBy(Coin_Table.rank, true))
                .queryList().toObservable()
                .subscribeOn(Schedulers.io());
    }

    public static void addAddCoins(List<Coin> vouchers) {
        FlowManager.getModelAdapter(Coin.class).saveAll(vouchers);
    }
}

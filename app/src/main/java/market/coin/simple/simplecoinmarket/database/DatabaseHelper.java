package market.coin.simple.simplecoinmarket.database;

import android.content.Context;

import com.raizlabs.android.dbflow.BuildConfig;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.DirectModelNotifier;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import io.reactivex.Observable;

public class DatabaseHelper {
    private static final String TAG = DatabaseHelper.class.getCanonicalName();

    private DatabaseHelper() {
    }

    public static void initDatabase(Context context) {
        /*DatabaseConfig userChanged = new DatabaseConfig.Builder(CoinMarketDatabase.class)
                .modelNotifier(DirectModelNotifier.get())
                .build();*/

        FlowManager.init(new FlowConfig.Builder(context).build());

        if (BuildConfig.DEBUG) {
            FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
        }
    }

    public static Observable<Transaction> clearDatabase(Context context) {
        return Observable.create(e -> {
            DatabaseDefinition database = FlowManager.getDatabase(CoinMarketDatabase.class);

            Transaction transactionBuilder = new Transaction.Builder(
                    databaseWrapper -> {
                        //Delete.tables(User.class, Voucher.class, Quest.class, FeaturedQuest.class, Message.class, Popup.class);
                    }, database)
                    .success(transaction -> {
                        //LogUtil.logD(TAG, "Complete clear database" + context.getDatabasePath(SevenRewardsDatabase.DB_NAME));
                        e.onNext(transaction);
                        e.onComplete();
                    })
                    .error((transaction, throwable) -> e.onError(throwable)).build();
            transactionBuilder.execute();
        });
    }

    public static <T> Observable<Transaction> clearTable(Class<T> className) {
        return Observable.create(e -> {
            DatabaseDefinition database = FlowManager.getDatabase(CoinMarketDatabase.class);

            Transaction transactionBuilder = new Transaction.Builder(
                    databaseWrapper -> {
                        // Clear DBFlow
                        Delete.table(className);
                    }, database)
                    .success(transaction -> {
                        //LogUtil.logD(TAG, "Clear table " + className + " successfully");
                        e.onNext(transaction);
                        e.onComplete();
                    })
                    .error((transaction, throwable) -> e.onError(throwable)).build();
            transactionBuilder.execute();
        });
    }
}

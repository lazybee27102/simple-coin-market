package market.coin.simple.simplecoinmarket;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import market.coin.simple.simplecoinmarket.database.DatabaseHelper;

public class SimpleCoinMarketApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper.initDatabase(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

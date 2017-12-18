package market.coin.simple.simplecoinmarket.database;

import com.raizlabs.android.dbflow.annotation.Database;

import static market.coin.simple.simplecoinmarket.database.CoinMarketDatabase.DB_NAME;
import static market.coin.simple.simplecoinmarket.database.CoinMarketDatabase.DB_VERSION;

@Database(name = DB_NAME, version = DB_VERSION)
public class CoinMarketDatabase {
    public static final String DB_NAME = "CoinMarketDatabase";
    public static final int DB_VERSION = 1;
}

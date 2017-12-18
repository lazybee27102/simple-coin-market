package market.coin.simple.simplecoinmarket.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.rx2.structure.BaseRXModel;

import market.coin.simple.simplecoinmarket.database.CoinMarketDatabase;
import market.coin.simple.simplecoinmarket.util.ValidateUtil;

@Table(database = CoinMarketDatabase.class)
public class Coin extends BaseRXModel {
    @Column
    @PrimaryKey
    private String id;

    @Column
    private String name;

    @Column
    private String symbol;

    @Column
    private String rank;

    @Column
    private String priceUsd;

    @Column
    private String priceBtc;

    @Column
    private String lastUpdated;

    @Column
    private String percentChange24h;

    public Coin() {

    }

    public static Coin valueOf(CoinResponse coinResponse) {
        Coin coin = new Coin();
        coin.setId(coinResponse.id);
        coin.setName(coinResponse.name);
        coin.setSymbol(coinResponse.symbol);
        coin.setRank(coinResponse.rank);
        coin.setPriceUsd(coinResponse.priceUsd);
        coin.setPriceBtc(coinResponse.priceBtc);
        coin.setLastUpdated(coinResponse.lastUpdated);
        coin.setPercentChange24h(coinResponse.percentChange24h);
        return coin;
    }

    public boolean isAscending() {
        if (!ValidateUtil.checkString(getPercentChange24h())) {
            return false;
        }

        double percentChange24h = Double.valueOf(getPercentChange24h());
        return percentChange24h > 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(String priceUsd) {
        this.priceUsd = priceUsd;
    }

    public String getPriceBtc() {
        return priceBtc;
    }

    public void setPriceBtc(String priceBtc) {
        this.priceBtc = priceBtc;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getPercentChange24h() {
        return percentChange24h;
    }

    public void setPercentChange24h(String percentChange24h) {
        this.percentChange24h = percentChange24h;
    }
}

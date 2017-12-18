package market.coin.simple.simplecoinmarket.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoinResponse {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("symbol")
    @Expose
    public String symbol;

    @SerializedName("rank")
    @Expose
    public String rank;

    @SerializedName("price_usd")
    @Expose
    public String priceUsd;

    @SerializedName("price_btc")
    @Expose
    public String priceBtc;

    @SerializedName("percent_change_24h")
    @Expose
    public String percentChange24h;

    @SerializedName("last_updated")
    @Expose
    public String lastUpdated;
}

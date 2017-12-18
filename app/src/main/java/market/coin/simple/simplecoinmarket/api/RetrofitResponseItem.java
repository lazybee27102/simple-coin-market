package market.coin.simple.simplecoinmarket.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetrofitResponseItem<T> {
    @SerializedName("data")
    @Expose
    public T item;

    @SerializedName("first_login")
    @Expose
    public boolean firstLogin;
}

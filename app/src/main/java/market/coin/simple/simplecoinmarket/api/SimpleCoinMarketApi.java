package market.coin.simple.simplecoinmarket.api;

import java.util.List;

import io.reactivex.Observable;
import market.coin.simple.simplecoinmarket.config.ApiConstant;
import market.coin.simple.simplecoinmarket.model.CoinResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SimpleCoinMarketApi {
    @GET(ApiConstant.ENDPOINT_GET_COIN_LIST)
    Observable<List<CoinResponse>> getCoinList(@Query("start") int start,
                                               @Query("limit") int limit);
}

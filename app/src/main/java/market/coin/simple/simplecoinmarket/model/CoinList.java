package market.coin.simple.simplecoinmarket.model;

import java.util.ArrayList;
import java.util.List;

import market.coin.simple.simplecoinmarket.api.RetrofitResponseListItem;

public class CoinList {
    private List<Coin> coinList;

    public CoinList() {
    }

    public static CoinList valueOf(RetrofitResponseListItem<CoinResponse> response) {
        CoinList coinList = new CoinList();
        List<Coin> coins = new ArrayList<>(response.items.size());
        for (CoinResponse coinResponse : response.items) {
            Coin coin = Coin.valueOf(coinResponse);
            coins.add(coin);
        }

        coinList.setCoinList(coins);

        return coinList;
    }

    public static CoinList valueOf(List<CoinResponse> response) {
        CoinList coinList = new CoinList();
        List<Coin> coins = new ArrayList<>(response.size());
        for (CoinResponse coinResponse : response) {
            Coin coin = Coin.valueOf(coinResponse);
            coins.add(coin);
        }

        coinList.setCoinList(coins);

        return coinList;
    }

    public List<Coin> getCoinList() {
        return coinList;
    }

    public void setCoinList(List<Coin> coinList) {
        this.coinList = coinList;
    }
}

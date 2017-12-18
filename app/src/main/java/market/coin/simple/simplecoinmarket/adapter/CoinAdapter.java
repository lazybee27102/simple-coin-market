package market.coin.simple.simplecoinmarket.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import market.coin.simple.simplecoinmarket.R;
import market.coin.simple.simplecoinmarket.util.ViewUtil;
import market.coin.simple.simplecoinmarket.databinding.ItemCoinBinding;
import market.coin.simple.simplecoinmarket.model.Coin;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.CoinHolder> implements Filterable {
    private Context context;
    private List<Coin> coinList;
    private List<Coin> originalList;

    public CoinAdapter(Context context, List<Coin> coinList) {
        this.context = context;
        this.coinList = coinList;
        this.originalList = coinList;
    }

    @Override
    public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coin, parent, false);
        return new CoinHolder(view);
    }

    @Override
    public void onBindViewHolder(CoinHolder holder, int position) {
        Coin coin = coinList.get(position);
        ItemCoinBinding itemCoinBinding = holder.mBinding;

        String coinName = coin.getName();
        String coinPrice = coin.getPriceUsd();
        String coinRank = coin.getRank();
        String symbol = coin.getSymbol();
        String percentChange24 = coin.getPercentChange24h();

        ViewUtil.setText(itemCoinBinding.tvName, coinName + " (" + symbol + ")");
        ViewUtil.setText(itemCoinBinding.tvPrice, "$" + coinPrice);
        ViewUtil.setText(itemCoinBinding.tvPercentChange, percentChange24 + "%");
        ViewUtil.setText(itemCoinBinding.tvRank, coinRank);


        if (coin.isAscending()) {
            itemCoinBinding.layoutPercent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen));
        } else {
            itemCoinBinding.layoutPercent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
        }
    }

    @Override
    public int getItemCount() {
        return coinList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Coin> filteredResults;
                if (charSequence.length() == 0) {
                    filteredResults = originalList;
                } else {
                    filteredResults = getFilteredResults(charSequence.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                coinList = (List<Coin>) filterResults.values;
                CoinAdapter.this.notifyDataSetChanged();
            }

            protected List<Coin> getFilteredResults(String constraint) {
                List<Coin> results = new ArrayList<>();

                for (Coin item : originalList) {
                    String coinName = item.getName().toLowerCase();
                    String coinSymbol = item.getSymbol().toLowerCase();

                    if (coinName.contains(constraint) || coinSymbol.contains(constraint)) {
                        results.add(item);
                    }
                }
                return results;
            }
        };
    }

    public class CoinHolder extends RecyclerView.ViewHolder {
        private ItemCoinBinding mBinding;

        public CoinHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}

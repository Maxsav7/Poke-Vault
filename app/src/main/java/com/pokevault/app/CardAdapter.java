/*
 * UI HELPER: Converts Card objects into the rows displayed by a ListView.
 * Both the catalog and vault reuse this class.
 */
package com.pokevault.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class CardAdapter extends BaseAdapter {
    public interface CardAction {
        void run(Card card);
    }

    private final LayoutInflater inflater;
    private final List<Card> cards;
    private final PokeVaultData data;
    private final String actionLabel;
    private final CardAction action;

    public CardAdapter(Context context, List<Card> cards, PokeVaultData data,
                       String actionLabel, CardAction action) {
        inflater = LayoutInflater.from(context);
        this.cards = cards;
        this.data = data;
        this.actionLabel = actionLabel;
        this.action = action;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Card getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View oldView, ViewGroup parent) {
        View row = oldView;
        if (row == null) {
            row = inflater.inflate(R.layout.row_card, parent, false);
        }

        final Card card = getItem(position);
        TextView name = row.findViewById(R.id.cardNameText);
        TextView information = row.findViewById(R.id.cardInfoText);
        Button button = row.findViewById(R.id.cardActionButton);

        name.setText(card.getName());
        String details = card.getSetName() + " • " + card.getRarityName()
            + " • " + String.format(Locale.US, "$%.2f", card.getMarketValue());
        if (data.getQuantity(card) > 0) {
            details += " • Owned: " + data.getQuantity(card);
        }
        information.setText(details);
        button.setText(actionLabel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action.run(card);
            }
        });
        return row;
    }
}

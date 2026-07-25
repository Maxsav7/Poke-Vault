/*
 * SCREEN CONTROLLER: Displays the card catalog, performs searches, and adds cards.
 * Its matching screen design is res/layout/activity_catalog.xml.
 */
package com.pokevault.app;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

public class CatalogActivity extends Activity {
    private PokeVaultData data;
    private ListView cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        data = new PokeVaultData(this);
        cardList = findViewById(R.id.cardList);
        showCards(data.loadCards());

        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                showCards(data.searchCards(text.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        findViewById(R.id.catalogBackButton).setOnClickListener(view -> finish());
    }

    private void showCards(List<Card> cards) {
        cardList.setAdapter(new CardAdapter(this, cards, data, false, null));
    }
}

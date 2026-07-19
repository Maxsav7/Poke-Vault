/*
 * SCREEN CONTROLLER: Displays the current username and collection totals.
 * Its matching screen design is res/layout/activity_profile.xml.
 */
package com.pokevault.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class ProfileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        PokeVaultData data = new PokeVaultData(this);
        UserProfile user = data.getCurrentUser();
        TextView details = findViewById(R.id.profileDetailsText);
        details.setText("Username: " + user.getUsername()
            + "\n\nCards owned: " + data.getTotalCardCount()
            + "\n\nVault value: " + String.format(Locale.US, "$%.2f", data.getVaultValue()));

        findViewById(R.id.profileBackButton).setOnClickListener(view -> finish());
    }
}

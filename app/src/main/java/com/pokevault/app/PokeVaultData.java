/*
 * DATA CONTROLLER: Reads the card CSV file and saves app data on the device.
 * Activities call this class instead of handling storage themselves.
 */
package com.pokevault.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PokeVaultData {
    private static final String FILE_NAME = "pokevault_data";
    private static final String USERS_KEY = "users";
    private static final String CURRENT_USER_KEY = "current_user";

    private final Context context;
    private final SharedPreferences preferences;

    public PokeVaultData(Context context) {
        this.context = context.getApplicationContext();
        preferences = this.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public boolean createUser(String username, String password) {
        Set<String> users = new HashSet<>(preferences.getStringSet(USERS_KEY, new HashSet<String>()));
        if (users.contains(username)) {
            return false;
        }
        users.add(username);
        preferences.edit()
            .putStringSet(USERS_KEY, users)
            .putString(passwordKey(username), password)
            .apply();
        return true;
    }

    public boolean login(String username, String password) {
        String savedPassword = preferences.getString(passwordKey(username), null);
        if (savedPassword == null || !savedPassword.equals(password)) {
            return false;
        }
        preferences.edit().putString(CURRENT_USER_KEY, username).apply();
        return true;
    }

    public void logout() {
        preferences.edit().remove(CURRENT_USER_KEY).apply();
    }

    public UserProfile getCurrentUser() {
        String username = preferences.getString(CURRENT_USER_KEY, "");
        return new UserProfile(username);
    }

    public List<Card> loadCards() {
        List<Card> cards = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getResources().openRawResource(R.raw.cards))
            );
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 4) {
                    cards.add(new Card(
                        values[0].trim(),
                        values[1].trim(),
                        Double.parseDouble(values[2].trim()),
                        Integer.parseInt(values[3].trim())
                    ));
                }
            }
            reader.close();
        } catch (Exception ignored) {
            // An empty list is shown if the classroom data file cannot be read.
        }
        return cards;
    }

    public List<Card> searchCards(String searchText) {
        List<Card> matches = new ArrayList<>();
        String wanted = searchText.toLowerCase(Locale.US).trim();
        for (Card card : loadCards()) {
            if (card.getName().toLowerCase(Locale.US).contains(wanted)) {
                matches.add(card);
            }
        }
        return matches;
    }

    public void addCard(Card card) {
        String key = quantityKey(getCurrentUser().getUsername(), card.getName());
        int currentQuantity = preferences.getInt(key, 0);
        preferences.edit().putInt(key, currentQuantity + 1).apply();
    }

    public void removeCard(Card card) {
        String key = quantityKey(getCurrentUser().getUsername(), card.getName());
        int currentQuantity = preferences.getInt(key, 0);
        if (currentQuantity <= 1) {
            preferences.edit().remove(key).apply();
        } else {
            preferences.edit().putInt(key, currentQuantity - 1).apply();
        }
    }

    public int getQuantity(Card card) {
        return preferences.getInt(
            quantityKey(getCurrentUser().getUsername(), card.getName()), 0
        );
    }

    public List<Card> getVaultCards() {
        List<Card> ownedCards = new ArrayList<>();
        for (Card card : loadCards()) {
            if (getQuantity(card) > 0) {
                ownedCards.add(card);
            }
        }
        return ownedCards;
    }

    public int getTotalCardCount() {
        int total = 0;
        for (Card card : getVaultCards()) {
            total += getQuantity(card);
        }
        return total;
    }

    public double getVaultValue() {
        double total = 0;
        for (Card card : getVaultCards()) {
            total += card.getMarketValue() * getQuantity(card);
        }
        return total;
    }

    private String passwordKey(String username) {
        return "password_" + username;
    }

    private String quantityKey(String username, String cardName) {
        return "quantity_" + username + "_" + cardName;
    }
}

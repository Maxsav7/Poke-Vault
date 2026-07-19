/*
 * MODEL: Stores the information for one Pokemon card.
 * This class does not draw anything on the screen.
 */
package com.pokevault.app;

public class Card {
    private final String name;
    private final String setName;
    private final double marketValue;
    private final int rarity;

    public Card(String name, String setName, double marketValue, int rarity) {
        this.name = name;
        this.setName = setName;
        this.marketValue = marketValue;
        this.rarity = rarity;
    }

    public String getName() {
        return name;
    }

    public String getSetName() {
        return setName;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public String getRarityName() {
        String[] names = {
            "Common", "Uncommon", "Rare", "Holofoil Rare",
            "Double Rare", "Ultra Rare", "Secret Rare"
        };
        if (rarity < 0 || rarity >= names.length) {
            return "Unknown";
        }
        return names[rarity];
    }
}

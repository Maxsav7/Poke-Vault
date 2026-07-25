/*
 * MODEL: Stores the information for one Pokemon card.
 * This class does not draw anything on the screen.
 */
package com.pokevault.app;

public class Card {
    private final String number;
    private final String name;
    private final String setName;
    private final double marketValue;
    private final String rarityName;
    private final String imageUrl;

    public Card(String number, String name, String setName, double marketValue,
                String rarityName, String imageUrl) {
        this.number = number;
        this.name = name;
        this.setName = setName;
        this.marketValue = marketValue;
        this.rarityName = rarityName;
        this.imageUrl = imageUrl;
    }

    public String getNumber() {
        return number;
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
        return rarityName.isEmpty() ? "Energy" : rarityName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

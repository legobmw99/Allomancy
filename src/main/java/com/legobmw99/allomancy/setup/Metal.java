package com.legobmw99.allomancy.setup;

public enum Metal {
    IRON(0, "iron"),
    STEEL(1, "steel"),
    TIN(2, "tin"),
    PEWTER(3, "pewter"),
    ZINC(4, "zinc"),
    BRASS(5, "brass"),
    COPPER(6, "copper"),
    BRONZE(7, "bronze"); /*, TODO
    ALUMINUM(8, "aluminum"),
    DURALUMIN(9, "duralumin"),
    CHROMIUM(10, "chromium"),
    NICROSIL(11, "nicrosil"),
    GOLD(12, "gold"),
    ELECTRUM(13, "electrum"),
    CADMIUM(14, "cadmium"),
    BENDALLOY(15, "bendalloy");*/

    private final int index;
    private final String name;

    Metal(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static Metal getMetal(int index) {
        for (Metal metal : values()) {
            if (metal.getIndex() == index)
                return metal;
        }
        return null;
    }
}
package com.legobmw99.allomancy.setup;

public enum Metal {
    IRON,
    STEEL,
    TIN,
    PEWTER,
    ZINC,
    BRASS,
    COPPER,
    BRONZE,
    ALUMINUM,
    DURALUMIN,
    CHROMIUM,
    NICROSIL,
    GOLD,
    ELECTRUM,
    CADMIUM,
    BENDALLOY;

    public String getName() {
        return super.name().toLowerCase();
    }

    public int getIndex() {
        return ordinal();
    }

    public static Metal getMetal(int index) {
        for (Metal metal : values()) {
            if (metal.getIndex() == index)
                return metal;
        }
        return null;
    }

    public String getDisplayName(){
        return super.name().substring(0,1).toUpperCase() + getName().substring(1);
    }
}
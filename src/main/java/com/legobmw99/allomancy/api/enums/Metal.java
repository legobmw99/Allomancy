package com.legobmw99.allomancy.api.enums;

import java.util.Locale;

public enum Metal {
    IRON(true),
    STEEL,
    TIN,
    PEWTER,
    ZINC,
    BRASS,
    COPPER(true),
    BRONZE,
    ALUMINUM,
    DURALUMIN,
    CHROMIUM,
    NICROSIL,
    GOLD(true),
    ELECTRUM,
    CADMIUM,
    BENDALLOY;


    private final boolean vanilla;

    Metal() {
        this(false);
    }

    Metal(boolean isVanilla) {
        this.vanilla = isVanilla;
    }

    public static Metal getMetal(int index) {
        for (Metal metal : values()) {
            if (metal.getIndex() == index) {
                return metal;
            }
        }
        throw new IllegalArgumentException("Allomancy: Bad Metal Index");
    }

    public boolean isVanilla() {
        return this.vanilla;
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public int getIndex() {
        return ordinal();
    }


}
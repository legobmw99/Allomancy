package com.legobmw99.allomancy.api.enums;

import java.util.Locale;

public enum Metal {
    IRON(true),
    STEEL(IRON),
    TIN,
    PEWTER(TIN),
    ZINC,
    BRASS(ZINC),
    COPPER(true),
    BRONZE(COPPER),
    ALUMINUM,
    DURALUMIN(ALUMINUM),
    CHROMIUM,
    NICROSIL(CHROMIUM),
    GOLD(true),
    ELECTRUM(GOLD),
    CADMIUM,
    BENDALLOY(CADMIUM);


    private final boolean vanilla;
    private final Metal base;

    Metal() {
        this(false);
    }

    Metal(Metal base) {
        this.base = base;
        this.vanilla = false;
    }

    Metal(boolean isVanilla) {
        this.vanilla = isVanilla;
        this.base = null;
    }

    public static Metal getMetal(int index) {
        for (Metal metal : values()) {
            if (metal.getIndex() == index) {
                return metal;
            }
        }
        throw new IllegalArgumentException("Allomancy: Bad Metal Index");
    }

    public boolean isAlloy() {return this.base != null;}

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
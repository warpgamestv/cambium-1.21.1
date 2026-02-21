package com.warpgames.cambium.block;

import net.minecraft.util.StringRepresentable;

public enum StrandConnection implements StringRepresentable {
    NONE("none"),
    CONNECTED("connected"),
    LOCKED("locked");

    private final String name;

    StrandConnection(String name) {
        this.name = name;
    }

    @Override
    public @org.jetbrains.annotations.NotNull String getSerializedName() {
        return name;
    }
}

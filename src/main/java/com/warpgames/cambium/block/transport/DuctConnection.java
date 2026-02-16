package com.warpgames.cambium.block.transport;

import net.minecraft.util.StringRepresentable;

public enum DuctConnection implements StringRepresentable {
    NONE("none"),
    NORMAL("normal"),
    EXTRACT("extract");

    private final String name;

    DuctConnection(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
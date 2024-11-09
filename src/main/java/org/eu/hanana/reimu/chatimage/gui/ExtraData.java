package org.eu.hanana.reimu.chatimage.gui;

import com.google.gson.Gson;

public record ExtraData(String action, String value, String extra) {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

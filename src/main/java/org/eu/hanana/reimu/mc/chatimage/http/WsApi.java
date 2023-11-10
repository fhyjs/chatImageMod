package org.eu.hanana.reimu.mc.chatimage.http;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WsApi {
    String namespace();
    String name();
}

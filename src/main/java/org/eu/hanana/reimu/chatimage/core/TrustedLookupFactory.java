package org.eu.hanana.reimu.chatimage.core;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;

public class TrustedLookupFactory {
    private static final VarHandle OVERRIDE_HANDLE;

    static {
        try {
            // 获取 AccessibleObject.override 的 VarHandle
            Lookup lookup = MethodHandles.privateLookupIn(AccessibleObject.class, MethodHandles.lookup());
            OVERRIDE_HANDLE = lookup.findVarHandle(AccessibleObject.class, "override", boolean.class);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to init VarHandle for override", e);
        }
    }

    public static MethodHandles.Lookup createTrustedLookup(Class<?> target) {
        try {
            Constructor<MethodHandles.Lookup> c =
                MethodHandles.Lookup.class.getDeclaredConstructor(Class.class,Class.class, int.class);

            // 直接改 AccessibleObject.override = true
            OVERRIDE_HANDLE.set(c, true);

            return c.newInstance(target,null,
                    MethodHandles.Lookup.PRIVATE |
                    MethodHandles.Lookup.PROTECTED |
                    MethodHandles.Lookup.PACKAGE |
                    MethodHandles.Lookup.PUBLIC);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create trusted lookup", e);
        }
    }
}

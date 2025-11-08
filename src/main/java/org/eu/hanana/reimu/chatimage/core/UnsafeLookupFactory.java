package org.eu.hanana.reimu.chatimage.core;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public final class UnsafeLookupFactory {

    private static final Unsafe UNSAFE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UnsafeLookupFactory", e);
        }
    }

    private UnsafeLookupFactory() {}

    /**
     * 创建一个高权限 Lookup，可访问目标类的私有成员。
     */
    public static MethodHandles.Lookup createTrustedLookup(Class<?> target) {
        try {
            // 使用 allocateInstance 绕过构造器访问限制
            MethodHandles.Lookup lookup =
                    (MethodHandles.Lookup) UNSAFE.allocateInstance(MethodHandles.Lookup.class);

            // 利用 VarHandle 或反射写入字段 "lookupClass" 和 "allowedModes"
            Field lookupClass = MethodHandles.Lookup.class.getDeclaredField("lookupClass");
            Field allowedModes = MethodHandles.Lookup.class.getDeclaredField("allowedModes");
            long lookupClassOffset = UNSAFE.objectFieldOffset(lookupClass);
            long allowedModesOffset = UNSAFE.objectFieldOffset(allowedModes);

            UNSAFE.putObject(lookup, lookupClassOffset, target);
            UNSAFE.putInt(lookup, allowedModesOffset,
                    MethodHandles.Lookup.PRIVATE |
                            MethodHandles.Lookup.PROTECTED |
                            MethodHandles.Lookup.PACKAGE |
                            MethodHandles.Lookup.PUBLIC);

            return lookup;
        } catch (Throwable t) {
            throw new RuntimeException("Failed to create trusted Lookup via Unsafe", t);
        }
    }

    public static Unsafe getUnsafe() {
        return UNSAFE;
    }
}

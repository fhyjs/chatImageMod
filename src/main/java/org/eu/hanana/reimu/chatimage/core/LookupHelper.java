package org.eu.hanana.reimu.chatimage.core;

import java.lang.invoke.MethodHandles;

public class LookupHelper {

    /**
     * 创建一个具有最高权限的 Lookup，可以访问任意类的私有成员。
     *
     * @param target 目标类（可跨模块）
     * @return 可完全访问 targetClass 的 Lookup
     */
    public static MethodHandles.Lookup trustedLookup(Class<?> target) {
        try {
            // 先尝试 VarHandle 方式
            return TrustedLookupFactory.createTrustedLookup(target);
        } catch (Throwable t) {
            // 失败时回退 Unsafe
            try {
                return UnsafeLookupFactory.createTrustedLookup(target);
            } catch (Throwable t2) {
                throw new RuntimeException("Both VarHandle & Unsafe lookup creation failed", t2);
            }
        }
    }

    /**
     * 快速调用目标类的私有静态方法示例
     */
    public static Object callPrivateStatic(Class<?> cls, String name, Class<?> retType, Class<?>... params) throws Throwable {
        MethodHandles.Lookup lookup = trustedLookup(cls);
        var mh = lookup.findStatic(cls, name, java.lang.invoke.MethodType.methodType(retType, params));
        return mh.invoke();
    }
}

package org.eu.hanana.reimu.chatimage.core;

import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class ModuleOpener {

    // 用于 agent 内部调用
    public static void agentmain(String args, Instrumentation inst) {
        try {
            System.out.println("[Agent] Injected in-process!");

            // 打开 java.base/sun.misc 给当前模块
            Module javaBase = Object.class.getModule();
            Module myMod = ModuleOpener.class.getModule();

            inst.redefineModule(
                javaBase,
                Set.of(), // reads
                Map.of(), // exports
                Map.of("sun.misc", Set.of(myMod)), // opens
                Set.of(),
                Map.of()
            );

            System.out.println("[Agent] Successfully opened java.base/sun.misc");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void openSunMisc() {
        try {
            var dir = new File("chatimage");
            dir.mkdirs();
            // 1. 写出临时 agent jar（只包含 MANIFEST）
            Path tmpJar = Files.createTempFile(dir.toPath(),"dynagent-", ".jar");
            try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(tmpJar))) {
                JarEntry entry = new JarEntry("META-INF/MANIFEST.MF");
                jos.putNextEntry(entry);
                String manifest = "Manifest-Version: 1.0\n"
                        + "Agent-Class: " + ModuleOpener.class.getName() + "\n"
                        + "Can-Redefine-Classes: true\n"
                        + "Can-Retransform-Classes: true\n"
                        + "Can-Set-Native-Method-Prefix: true\n";
                jos.write(manifest.getBytes());
                jos.closeEntry();
            }

            // 2. 自己 attach 自己
            String pid = ProcessHandle.current().pid() + "";
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(tmpJar.toAbsolutePath().toString(), "");
            vm.detach();

            // 3. 删除临时 jar
            Files.deleteIfExists(tmpJar);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

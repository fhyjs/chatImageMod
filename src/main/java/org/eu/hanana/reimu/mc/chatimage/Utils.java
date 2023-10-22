package org.eu.hanana.reimu.mc.chatimage;

import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static String FileChooser() {
        JFileChooser fileChooser = new JFileChooser();

        int returnValue = fileChooser.showOpenDialog(Display.getParent()); // 打开文件选择对话框

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // 用户选择了一个文件
            java.io.File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            return null;
        }
    }
    public static boolean OK;
    public static byte[] ReadFile(File file)
     {


        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[(int) file.length()]; // 创建与文件大小相同的字节数组

            fis.read(byteArray); // 从文件读取字节并存储到字节数组中
            fis.close();

            return byteArray;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void WriteFile(String filePath,byte[] contentBytes) {

        try {
            // 创建FileOutputStream对象
            FileOutputStream fos = new FileOutputStream(filePath);

            // 使用FileOutputStream将字节数组写入文件
            fos.write(contentBytes);

            // 关闭文件输出流
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void SendLByte(byte[] bytes) throws InterruptedException {
        int maxPacketSize = 32700; // 限制单个数据包大小

        List<byte[]> data=new ArrayList<>();
        // 分割数据并发送
        for (int i = 0; i < bytes.length; i += maxPacketSize) {
            int length = Math.min(bytes.length - i, maxPacketSize);
            byte[] chunk = Arrays.copyOfRange(bytes, i, i + length);
            data.add(chunk);
        }
        OK=false;
        ChatImageMod.INSTANCE.sendToServer(new UploadMMessage(data.size(),"start"));
        while (!OK){
            Thread.sleep(1);
        }
        for (byte[] datum : data) {
            OK=false;
            ChatImageMod.INSTANCE.sendToServer(new UploadMessage(datum, data.indexOf(datum)));
            while (!OK){
                Thread.sleep(1);
            }
        }
        ChatImageMod.INSTANCE.sendToServer(new UploadMMessage(0,"finish"));
    }
}

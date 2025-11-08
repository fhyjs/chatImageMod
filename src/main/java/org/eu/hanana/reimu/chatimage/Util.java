package org.eu.hanana.reimu.chatimage;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class Util {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(SECURE_RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
    public static String getFileExtension(File file) {
        if (file == null || file.isDirectory()) {
            return "/"; // 目录返回空字符串
        }
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < name.length() - 1) {
            return name.substring(dotIndex + 1).toLowerCase(); // 后缀转小写
        }
        return ""; // 无扩展名
    }

    public static BufferedImage iconToImage(Icon icon) {
        if (icon == null) return null;

        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        // 创建 ARGB 图像
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        // 用 Graphics 方式绘制，无论底层是 MultiResolutionImage 还是普通 Image 都安全
        Graphics2D g = image.createGraphics();
        try {
            icon.paintIcon(null, g, 0, 0);
        } finally {
            g.dispose();
        }
        return image;
    }
    public static List<String> splitByCiCodes(String text) {
        List<String> parts = new ArrayList<>();
        int index = 0;

        while (index < text.length()) {
            int start = text.indexOf("CI{", index);
            if (start == -1) {
                // 剩下的都是普通文本
                parts.add(text.substring(index));
                break;
            }

            // 普通文本部分
            if (start > index) {
                parts.add(text.substring(index, start));
            }

            // 找到CI{}的匹配结尾
            int braceLevel = 0;
            int end = -1;
            for (int i = start + 3; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '{') braceLevel++;
                else if (c == '}') {
                    if (braceLevel == 0) {
                        end = i;
                        break;
                    } else braceLevel--;
                }
            }

            if (end == -1) {
                // 没闭合，直接把剩下的全加上
                parts.add(text.substring(start));
                break;
            }

            // 加入完整的 CI 代码块
            parts.add(text.substring(start, end + 1));
            index = end + 1;
        }

        return parts;
    }

    /**
     * 合并二维字节数组为一维字节数组
     *
     * @param byteArrays 要合并的二维字节数组
     * @return 合并后的字节数组
     */
    public static byte[] mergeByteArrays(byte[][] byteArrays) {
        // 计算合并后的字节数组长度
        int totalLength = 0;
        for (byte[] array : byteArrays) {
            totalLength += array.length;
        }

        // 创建合并后的字节数组
        byte[] mergedArray = new byte[totalLength];

        // 将每个子数组的内容复制到合并后的字节数组中
        int currentIndex = 0;
        for (byte[] array : byteArrays) {
            System.arraycopy(array, 0, mergedArray, currentIndex, array.length);
            currentIndex += array.length;
        }

        return mergedArray;
    }
    /**
     * 递归删除目录及其内容
     *
     * @param directory 要删除的目录
     * @return 如果目录及其内容删除成功，则返回 true；否则返回 false
     */
    public static boolean deleteDirectory(File directory) {
        if (!directory.exists()) {
            return false;
        }

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 递归删除子目录
                        deleteDirectory(file);
                    } else {
                        // 删除文件
                        file.delete();
                    }
                }
            }
        }

        // 删除空目录
        return directory.delete();
    }
    public static List<byte[]> splitByteArray(byte[] data, int chunkSize) {
        List<byte[]> chunks = new ArrayList<>();
        for (int i = 0; i < data.length; i += chunkSize) {
            int end = Math.min(i + chunkSize, data.length);
            byte[] chunk = new byte[end - i];
            System.arraycopy(data, i, chunk, 0, chunk.length);
            chunks.add(chunk);
        }
        return chunks;
    }
    public static byte[] readFileToByteArray(File file) throws IOException {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            return bos.toByteArray();
        } finally {
            if (fis != null) {
                fis.close();
            }
            bos.close();
        }
    }
    public static BufferedImage convertToBufferedImage(NativeImage nativeImage) {
        // 1. 验证格式必须是 RGBA
        if (nativeImage.format() != NativeImage.Format.RGBA) {
            throw new IllegalArgumentException("只支持 RGBA 格式的 NativeImage");
        }

        // 2. 获取图像尺寸
        int width = nativeImage.getWidth();
        int height = nativeImage.getHeight();

        // 3. 创建目标 BufferedImage
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 4. 批量获取像素数据（避免逐像素操作）
        IntBuffer pixelBuffer = MemoryUtil.memIntBuffer(nativeImage.getPointer(), width * height);
        int[] pixels = new int[width * height];
        pixelBuffer.get(pixels);

        // 5. 转换 ABGR 到 ARGB（利用游戏工具类）
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = ARGB.fromABGR(pixels[i]);
        }

        // 6. 一次性设置到 BufferedImage
        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

        return bufferedImage;
    }
}

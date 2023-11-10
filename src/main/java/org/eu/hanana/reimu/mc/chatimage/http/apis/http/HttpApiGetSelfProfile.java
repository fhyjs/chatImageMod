package org.eu.hanana.reimu.mc.chatimage.http.apis.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HttpApiGetSelfProfile extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应内容类型
        response.setContentType("application/json");

        // 获取输出流
        PrintWriter out = response.getWriter();

        // 生成动态内容
        out.println("{\n" +
                "    \"avatarUrl\": \"/icon\",\n" +
                "    \"birthday_day\": 0,\n" +
                "    \"birthday_month\": 0,\n" +
                "    \"birthday_year\": 0,\n" +
                "    \"categoryId\": 0,\n" +
                "    \"isBlock\": false,\n" +
                "    \"isMsgDisturb\": false,\n" +
                "    \"isSpecialCareOpen\": false,\n" +
                "    \"isSpecialCareZone\": false,\n" +
                "    \"longNick\": \"\",\n" +
                "    \"nick\": \"ChatImgMod\",\n" +
                "    \"onlyChat\": false,\n" +
                "    \"qid\": \"\",\n" +
                "    \"qzoneNotWatch\": false,\n" +
                "    \"qzoneNotWatched\": false,\n" +
                "    \"remark\": \"\",\n" +
                "    \"ringId\": \"\",\n" +
                "    \"sex\": 0,\n" +
                "    \"status\": 10,\n" +
                "    \"topTime\": \"0\",\n" +
                "    \"uid\": \"u__htjqoR98ZR250Vk47nH3g\",\n" +
                "    \"uin\": \"1432582213\"\n" +
                "}");
    }
}

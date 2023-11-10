package org.eu.hanana.reimu.mc.chatimage.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.client.NoOpEndpoint;
import org.eclipse.jetty.websocket.common.events.JettyListenerEventDriver;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.eu.hanana.reimu.mc.chatimage.ChatImageMod;
import org.eu.hanana.reimu.mc.chatimage.http.apis.http.HttpApiGetSelfProfile;

public class HttpServer extends Thread{
    private final int port;
    public static HttpServer newHttpServer(int port){
        return new HttpServer(port);
    }
    private HttpServer(int port){
        this.port=port;
        setName(this.toString());
    }

    public void server(int p) throws Exception {
        // 创建 Jetty 服务器
        Server server = new Server(p);
        HandlerCollection handlers = new HandlerCollection();
        // 创建 ServletContextHandler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS){};
        context.setContextPath("/");
        context.setDisplayName("ChatImgMod");

        // 创建WebSocketUpgradeFilter
        WebSocketServerFactory webSocketServerFactory;
        WebSocketUpgradeFilter webSocketUpgradeFilter = WebSocketUpgradeFilter.configure(context);
        // 注册WebSocket Endpoint
        webSocketUpgradeFilter.addMapping("/", (webSocketServerFactory=new WebSocketServerFactory()));
        // 将ServletContextHandler添加到服务器
        handlers.addHandler(context);

        //注册WS适配器
        webSocketServerFactory.register(WsHandler.class);

        // 注册DynamicPageServlet到指定路径
        context.addServlet(new ServletHolder(new Api()), "/api");
        context.addServlet(new ServletHolder(new HttpApiGetSelfProfile()), "/api/getSelfProfile");


        // 启动服务器
        server.setHandler(handlers);
        server.start();
        server.join();
    }

    @Override
    public void run() {
        try {
            this.server(port);
        } catch (Exception e) {
            ChatImageMod.logger.warn(e);
        }
    }
}


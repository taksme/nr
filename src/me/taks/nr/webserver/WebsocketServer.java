package me.taks.nr.webserver;

import java.util.Properties;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import me.taks.nr.Report;
import me.taks.nr.Reports;
import me.taks.nr.location.Locations;
import me.taks.nr.subs.Subscriptions;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.ImmediateEventExecutor;

import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class WebsocketServer {
	public class WebsocketServerHandler extends ChannelInboundHandlerAdapter {
	    private WebSocketServerHandshaker handshaker;

	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	        if (msg instanceof FullHttpRequest) {
	            handleHttpRequest(ctx, (FullHttpRequest) msg);
	        } else if (msg instanceof WebSocketFrame) {
	            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
	        } else {
	            ReferenceCountUtil.release(msg);
	        }
	    }

	    @Override
	    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	        ctx.flush();
	    }

		private void handleHttpRequest(final ChannelHandlerContext ctx, FullHttpRequest req)
	            throws Exception {
	    	
	    	HttpResponse res = null;
	        final String uri = req.getUri();
	    	
	    	// Handle a bad request.
	        if (!req.getDecoderResult().isSuccess()) {
	            res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
	        } else if (req.getMethod() == GET && req.headers().contains("Upgrade")) {
				System.out.println("Handling upgrade");
		        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
		                getWebSocketLocation(req), null, false, Integer.MAX_VALUE);
		        handshaker = wsFactory.newHandshaker(req);
		        if (handshaker == null) {
					System.out.println("Hit unsupported");
		            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
		        } else {
					System.out.println("Handshaking");
		            handshaker.handshake(ctx.channel(), req);
/*		            .addListener(new ChannelFutureListener() {
						@Override
						public void operationComplete(ChannelFuture arg0) throws Exception {
							ctx.pipeline().removeFirst();
							ctx.pipeline().removeFirst();
							System.out.println("removed");
						}
					});
*/		        }
		        return;
	        } else if (req.getMethod() == GET && "/map".equals(uri)) {
			    res = new StaticMapRenderer(locations).handle(req);
	        } else if (req.getMethod() == GET && uri.startsWith("/locations")) {
			    res = new LocationAPIServer(locations).handle(req);
	        } else if ((req.getMethod() == POST || req.getMethod() == PUT) && uri.startsWith("/sub")) {
			    res = new SubscribeAPIServer(subs).handle(req);
		    } else if (req.getMethod() == GET ) {
		    	res = StaticFileServer.handle(req);
		    } else {
	            res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN);
	        }
	        
	        if (isKeepAlive(req)) {
	            res.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
	        }
	        ChannelFuture f = ctx.writeAndFlush(res);
	        if (!isKeepAlive(req) || res.getStatus().code() != 200) {
	            f.addListener(ChannelFutureListener.CLOSE);
	        }
	        req.release();
	    }

	    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
	        System.out.println("Websocket received");
	        if (frame instanceof CloseWebSocketFrame) {
	            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
	        } else if (frame instanceof PingWebSocketFrame) {
	            ctx.write(new PongWebSocketFrame(frame.isFinalFragment(), frame.rsv(), frame.content()), ctx.voidPromise());
	        } else if (frame instanceof TextWebSocketFrame) {
	            handleTextWebSocketFrame(ctx, (TextWebSocketFrame)frame);
/*	        } else if (frame instanceof BinaryWebSocketFrame) {
	            ctx.write(frame, ctx.voidPromise());
	        } else if (frame instanceof ContinuationWebSocketFrame) {
	            ctx.write(frame, ctx.voidPromise());
*/	        } else if (frame instanceof PongWebSocketFrame) {
	            frame.release();
	            // Ignore
	        } else {
	            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
	                    .getName()));
	        }
	    }
	    
	    private void handleTextWebSocketFrame(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
	        String request = frame.text();
	        System.out.println("Websocket received: "+request);
	        if ("SUBSCRIBE REPORTS".equals(request)) {
	        	reportSubscribers.add(ctx.channel());
	        }
	        //ctx.channel().write(new TextWebSocketFrame(request.toUpperCase()));
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    	cause.printStackTrace();
	    	ctx.close();
	    }

	    private String getWebSocketLocation(FullHttpRequest req) {
	        return "ws://" + req.headers().get(HttpHeaders.Names.HOST);
	    }
	}
	public final Reports reports;
	public final Locations locations;
	public final Subscriptions subs;
	private int port = 8080;
	private ChannelGroup reportSubscribers;
	
	public WebsocketServer(final Reports reports, final Locations locations, 
							Properties props, final Subscriptions subs) {
		this.reports = reports;
		this.locations = locations;
		this.subs = subs;
		
		reportSubscribers = new DefaultChannelGroup( "websocket-server", ImmediateEventExecutor.INSTANCE);
		reports.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent arg0) {}
			public void intervalAdded(ListDataEvent event) {
				System.out.println("sending to "+reportSubscribers.size()+" subscribers. reports size:"+reports.size()+", "+event.getIndex0()+", "+event.getIndex1());
				String out = "";
				for (int i=event.getIndex0(); i<=event.getIndex1(); i++) {
					Report r = reports.get(i);
					out+=","+r.toJSONString();
				}
				reportSubscribers.writeAndFlush(new TextWebSocketFrame("["+out.substring(1)+"]"));
			}
			public void contentsChanged(ListDataEvent arg0) {}
		});
	}

	public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
             .channel(NioServerSocketChannel.class)
             .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
             .option(ChannelOption.SO_BACKLOG, 1024)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast("decoder", new HttpRequestDecoder());
					pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
					pipeline.addLast("encoder", new HttpResponseEncoder());
				 	pipeline.addLast("handler", new WebsocketServerHandler());
                 }
             });
            ChannelFuture f = b.bind(port).sync();
            System.out.println("Web Socket Server started at port " + port);
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}

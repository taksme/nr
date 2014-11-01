package me.taks.nr.webserver;

import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.Collection;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import me.taks.nr.location.Location;
import me.taks.nr.location.Locations;
import me.taks.nr.schedule.Train;
import me.taks.nr.schedule.Trains;

public class TrainAPIServer {
	private StringBuffer out = new StringBuffer();
	private Trains trains;
	
	public TrainAPIServer(Trains trains) {
		this.trains = trains;
	}
	
	public void append(String s) {
		out.append(s);
	}
	
	public void overFirst(char c) {
		out.insert(0, c);
	}
	
	public void append(Collection<Train> trains) {
		for (Train t : trains) {
//			append("," + t.toJSONString());
		}
		overFirst('[');
		append("]");
	}
	
	public HttpResponse handle(HttpRequest req) {
		final String uri = req.getUri();
   // 	append(locations, uri.equals("/locations/mappable"));
		ByteBuf buffer = Unpooled.copiedBuffer(out, CharsetUtil.UTF_8);
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK);
        res.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        setContentLength(res, buffer.readableBytes());
        res.content().writeBytes(buffer);
        buffer.release();
        return res;
	}
}
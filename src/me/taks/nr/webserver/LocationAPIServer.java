package me.taks.nr.webserver;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

import me.taks.json.JSONObject;
import me.taks.nr.Box;
import me.taks.nr.Locations;
import me.taks.nr.Point;
import me.taks.nr.Report;
import me.taks.nr.ReportViewer;
import me.taks.nr.Locations.Location;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;

public class LocationAPIServer {
	private StringBuffer out = new StringBuffer();
	private Locations locations;
	
	public LocationAPIServer(Locations locations) {
		this.locations = locations;
	}
	
	public void append(String s) {
		out.append(s);
	}
	
	public void append(Locations locations, boolean mappable) {
		append("[");
		for (Locations.Location l : locations) {
			if (!mappable || (l.getLocation()!=null && l.getLocation().northing>0))
				append(l.toJSONString()+",");
		}
		append("]");
	}
	
	public HttpResponse handle(HttpRequest req) {
		final String uri = req.getUri();
    	append(locations, uri.equals("/locations/mappable"));
		ByteBuf buffer = Unpooled.copiedBuffer(out, CharsetUtil.UTF_8);
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK);
        res.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        setContentLength(res, buffer.readableBytes());
        res.content().writeBytes(buffer);
        buffer.release();
        return res;
	}
}
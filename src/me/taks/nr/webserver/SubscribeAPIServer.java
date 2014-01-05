package me.taks.nr.webserver;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import me.taks.json.JSONObject;
import me.taks.nr.subs.Subscription;
import me.taks.nr.subs.Subscriptions;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

public class SubscribeAPIServer {
	private Subscriptions subs;
	public SubscribeAPIServer(Subscriptions subs) {
		this.subs = subs;
	}
	public HttpResponse handle(FullHttpRequest req) {
		if (req.getMethod()==HttpMethod.POST) {
			//TODO: this is all kinds of evil
			JSONObject o = new JSONObject(req.content().toString(CharsetUtil.UTF_8));
			String[] filter = o.getString("subscription").split("[.]");
			subs.add(new Subscription(Subscription.Type.valueOf(o.getString("method")),
						o.getString("clientId"),
						filter[1]));
			return new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
		} else if (req.getMethod()==HttpMethod.PUT && req.getUri().equals("/sub/delete")) {
			JSONObject o = new JSONObject(req.content().toString(CharsetUtil.UTF_8));
			String[] filter = o.getString("subscription").split("[.]");
			Subscription delete = null;
			for (Subscription s : subs) {
				if (filter[1].equals(s.getFilter()) && s.getClientId().equals(o.getString("clientId"))) {
					delete = s;
					break;
				}
			}
			if (delete!=null) {
				subs.remove(delete);
				return new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
			} else {
				return new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND);
			}
		} else {
			return new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
		}
	}
}

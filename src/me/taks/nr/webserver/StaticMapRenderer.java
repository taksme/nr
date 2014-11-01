package me.taks.nr.webserver;


import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import me.taks.nr.Box;
import me.taks.nr.Point;
import me.taks.nr.Report;
import me.taks.nr.ReportViewer;
import me.taks.nr.location.Location;
import me.taks.nr.location.Locations;

public class StaticMapRenderer {
	private StringBuffer out = new StringBuffer();
	
	public StaticMapRenderer(Locations locations) {
		append(locations);
	}
	
	@SuppressWarnings("serial")
	private class Grouping extends Locations {
		public String name;
        public Box box;
        public Grouping (String name, Box box) {
        	this.name = name;
        	this.box = box;
        }
	}
/*	
	Grouping[] groupings = {
			new Grouping("Edinburgh/Glasgow", 
					new Box(new Point(240000, 640000), new Point(340000, 700000))),
			new Grouping("Liverpool/Manchester/Leeds", 
					new Box(new Point(320000, 365000), new Point(450000, 442000))),
			new Grouping("London", new Box(new Point(460000, 142000), new Point(600000, 220000))),
			new Grouping("National", new Box(new Point(146000, 30000), new Point(656000, 970000)))
	};
*/	
	Grouping[] groupings = {
			new Grouping("Scotland", 
					new Box(new Point(160000, 565000), new Point(426000, 970000))),
			new Grouping("England and Wales", new Box(new Point(146000, 30000), new Point(656000, 566000)))
	};
	
	private String cleanForAttr(String s) {
    	return s.replaceAll("&","&amp;").replaceAll("[^A-Za-z0-9-()&;\t¼½¾ ]", "");
	}
	
	public void append(String s) {
		out.append(s);
	}
	
	private void append(Location l) {
		Report report = l.getLastReport();
    	long late = report!=null ? report.getTimes().getMinutesDiff() : Long.MAX_VALUE;
    	Point p = l.getLocation();
    	append(String.format(
			"<circle id='%s' cx='%s' cy='%s' r='%s' class='%s'>" +
				"<title>%s\n%s : %s\n%s</title>" +
			"</circle>", 
			l.getStanox(), 
			p.easting, 
			-p.northing, 
			2000, 
			late >60*24 ? "noreport" : late>5 ? "late" : late>0 ? "nearly" : "ontime",
			cleanForAttr(l.getDescription()),
			p.easting, 
			-p.northing, 
	    	report!=null ? cleanForAttr(ReportViewer.get(report).getSummary()) : "No Report"
    	));
	}
	
	private void append(Grouping g) {
		Point size = g.box.getSize();
		append(String.format(
	        "<section class='map %s'>" +
        		"<header>%s</header>" +
        		"<svg viewBox='%s,%s,%s,%s'><g>",
			cleanForAttr(g.name),
			g.name,
			g.box.low.easting,
			-g.box.high.northing,
			size.easting,
			size.northing
		));
		for (Location l : g) {
			Report report = l.getLastReport();
	    	long late = report!=null ? report.getTimes().getMinutesDiff() : Long.MAX_VALUE;
	    	if (late>60*24)
			append(l);
		}
		for (Location l : g) {
			Report report = l.getLastReport();
	    	long late = report!=null ? report.getTimes().getMinutesDiff() : Long.MAX_VALUE;
	    	if (late<=60*24)
			append(l);
		}
		append("</g></svg></section>");
	}

	public void append(Locations locations) {
		for (Location l : locations) {
			Point p = l.getLocation();
			if (null==p) continue;
			for (Grouping grouping : groupings) {
				if (grouping.box.contains(l.getLocation())) {
					grouping.add(l);
					break;
				}
			}
		}
		append("<html>" +
				"<head>" +
					"<link rel='stylesheet/less' type='text/css' href='styles.less'>" +
		    		"<script src='less.js' type='text/javascript'></script>" +
		    		"<script src='jquery.js' type='text/javascript'></script>" +
		    		"<script src='map.js' type='text/javascript'></script>" +
		    	"</head>" +
				"<body>");
		for (Grouping g : groupings) append(g);
		append("</body></html>");
	}
	
	public HttpResponse handle(HttpRequest req) {
		ByteBuf buffer = Unpooled.copiedBuffer(out, CharsetUtil.UTF_8);
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK);
        res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        setContentLength(res, buffer.readableBytes());
        res.content().writeBytes(buffer);
        buffer.release();
        return res;
	}
}
package me.taks.nr.webserver;

import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpHeaders.Names.IF_MODIFIED_SINCE;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

public class StaticFileServer {
    private static Pattern urlPattern = Pattern.compile("^/[A-Za-z0-9-.]+$");
    private static SimpleDateFormat dateFormatter = 
    		new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
    private static TimeZone GMT = TimeZone.getTimeZone("GMT");
    private static MimetypesFileTypeMap mtftMap = new MimetypesFileTypeMap();
    
    static {
    	mtftMap.addMimeTypes("image png tif jpg jpeg gif bmp");
    	mtftMap.addMimeTypes("text txt htm html");
    }
    
    public static HttpResponse handle(HttpRequest req) {
        String uri = req.getUri();
System.out.println(uri);
        if (req.getMethod()!=HttpMethod.GET || !urlPattern.matcher(uri).matches()) {
    		return new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN);
        }

        File file = new File("static"+uri);
        if (file.isHidden() || !file.exists()) {
            return new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
        } else if (!file.isFile()) {
    		return new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN);
        }

        try {
	        String ifModifiedSince = req.headers().get(IF_MODIFIED_SINCE);
	        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
	            long modSecs = dateFormatter.parse(ifModifiedSince).getTime() / 1000;
	            if (modSecs >= file.lastModified() / 1000) {
	        		return new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED);
	            }
	        }
        } catch (ParseException pe) { /* Meh. Just send the file anyway */ }

        try {
            int fileLength = (int)file.length();
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK);
            setContentLength(res, fileLength);
            setHeaders(res, file);
            //shit solution removing the chunking but good enough to experiment
            byte[] content = new byte[fileLength];
            FileInputStream fis = new FileInputStream(file);
            fis.read(content);
            fis.close();
            res.content().writeBytes(content);
            return res;
        } catch (IOException fnfe) {
            return new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
        }

    }
    
    private static void setHeaders(HttpResponse res, File file) {
        res.headers().set(CONTENT_TYPE, mtftMap.getContentType(file));
        
        // Date header
        dateFormatter.setTimeZone(GMT);
        String date = dateFormatter.format(new GregorianCalendar().getTime());
        res.headers().set(DATE, date);

        // Add cache headers
/* DON'T CACHE
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        res.headers().set(EXPIRES, dateFormatter.format(time.getTime()));
        res.headers().set(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        res.headers().set(
                LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
*/
    }
}

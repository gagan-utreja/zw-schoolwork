package edu.gatech.oad.rocket.findmythings.server.util;

import java.io.IOException;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.util.WebUtils;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

public final class HTTP {
	
	private HTTP() {}
	
	public static enum Status {
		OK(200),
		UNAUTHORIZED(401),
	    NOT_FOUND(404),
	    FORBIDDEN(403),
	    INTERNAL_ERROR(500);
		
		private int code;
		
		private Status(int code) {
			this.code = code;
		}
		
		int toInt() {
			return code;
		}
	}

	public static void write(ServletResponse response, String mimeType, Status returnCode, String output) {
		write(WebUtils.toHttp(response), mimeType, returnCode, output);
	}

	public static void write(HttpServletResponse response, String mimeType, Status returnCode, String output) {
		try {
			response.setContentType(mimeType);
			response.setStatus(returnCode.toInt());
			response.getWriter().println(output);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeJSON(HttpServletResponse response, Status returnCode, JSONObject obj) {
		write(response, MimeTypes.JSON, returnCode, obj.toString());
	}

	public static void writeJSON(ServletResponse response, Status returnCode, JSONObject obj) {
		write(response, MimeTypes.JSON, returnCode, obj.toString());
	}

	public static void writeAsJSON(HttpServletResponse response, Status returnCode, Object... args) {
		writeJSON(response, returnCode, JSON.fromArgs(args));
	}

	public static void writeAsJSON(ServletResponse response, Status returnCode, Object... args) {
		writeJSON(response, returnCode, JSON.fromArgs(args));
	}

}

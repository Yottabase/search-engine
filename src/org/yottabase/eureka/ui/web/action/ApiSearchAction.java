package org.yottabase.eureka.ui.web.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.yottabase.eureka.ui.web.core.Action;

public class ApiSearchAction implements Action{

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		JSONObject json = new JSONObject();
		
		json.put("Hello", "World");
		
		response.setContentType("application/json");
		response.getWriter().write(json.toString());
	}

}

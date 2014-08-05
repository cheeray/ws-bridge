package com.cheeray.ws.bridge;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>
 * A servlet bridges REST and SOAP messages.
 * </p>
 * 
 * <p>
 * Routes are mapped inside the router.
 * </p>
 * 
 * @author cheeray
 * 
 */
@SuppressWarnings("serial")
@WebServlet("/bridge")
public class BridgeServlet extends HttpServlet {

	@Inject
	Jsoner jsoner;

	@Inject
	Soaper soaper;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, List<String>> params = IO.parse(req.getQueryString());
		List<String> actions = params.get(Route.ACTION);
		PrintWriter writer = resp.getWriter();
		try {
			if (actions == null) {
				// Consider it's a soap request ...
				// TODO: Route to REST ...
				IO.markSoap(resp);
				JSONObject json = jsoner.json(req);
				IO.writeSoap(writer, json);
			} else {
				if (actions.isEmpty()) {
					throw new ServletException("Missing SOAP action in query parameters.");
				} else {
					// Consider it's a JSON request ...
					String action = actions.iterator().next();
					req.setAttribute(Route.ACTION, action);
					IO.markJson(resp);
					Vector<?> soap = soaper.soap(req);
					IO.writeJson(writer, soap);
				}
			}
		} catch (JSONException e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
	}
}

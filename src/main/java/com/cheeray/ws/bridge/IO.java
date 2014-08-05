package com.cheeray.ws.bridge;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class IO {

	public static String read(HttpServletRequest req) throws IOException {
		StringBuilder sb = new StringBuilder();
		try {
			InputStreamReader inR = new InputStreamReader(req.getInputStream());
			BufferedReader buf = new BufferedReader(inR);
			String line;
			while ((line = buf.readLine()) != null) {
				sb.append(line);
			}
		} finally {

		}
		final String content = sb.toString();
		Logger.getLogger(IO.class.getName()).info(content);
		return content;
	}

	public static void markJson(HttpServletResponse resp) {
		resp.setHeader("Accept", "application/json");
		resp.setHeader("Content-type", "application/json");
	}

	public static void markSoap(HttpServletResponse resp) {
		resp.setHeader("Accept", "application/xml");
		resp.setHeader("Content-type", "application/xml");
	}

	public static void writeJson(PrintWriter writer, Vector<?> ro) {
		// TODO Auto-generated method stub
		if (ro.size() > 0)
			if (ro.size() > 1) {
				JSONArray array = new JSONArray();
				Iterator<?> iterator = ro.iterator();
				while (iterator.hasNext()) {
					SoapObject o = (SoapObject) iterator.next();
					array.put(toJson(o));
				}
				writer.write(array.toString());
			} else {
				SoapObject o = (SoapObject) ro.get(0);
				JSONObject jo = toJson(o);
				writer.write(jo.toString());
			}
	}

	public static JSONObject toJson(SoapObject o) {
		// TODO: convert to json ...
		JSONObject jo;
		try {
			String str = o.toString();
			String jsonStr = str.substring(str.indexOf('{')).replaceAll("=", "=\"").replaceAll(";", "\",").replaceAll("/", "\\\\/");
			jo = new JSONObject(jsonStr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jo = new JSONObject();
		}
		return jo;
	}

	public static void writeSoap(PrintWriter writer, JSONObject json) {
		// TODO Auto-generated method stub

	}

	public static Map<String, List<String>> parse(String queryString)
			throws UnsupportedEncodingException {
		final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		final String[] pairs = queryString.split("&");
		for (String pair : pairs) {
			final int idx = pair.indexOf("=");
			final String key = idx > 0 ? URLDecoder.decode(
					pair.substring(0, idx), "UTF-8") : pair;
			if (!query_pairs.containsKey(key)) {
				query_pairs.put(key, new LinkedList<String>());
			}
			final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder
					.decode(pair.substring(idx + 1), "UTF-8") : null;
			query_pairs.get(key).add(value);
		}
		return query_pairs;
	}

	public static Element toElement(JSONObject json, String namespace, String action) throws JSONException {

		final InputStream bis = new ByteArrayInputStream(XML.toString(json)
				.getBytes(StandardCharsets.UTF_8));
		final XmlPullParser parser = new KXmlParser();
		try {
			parser.setInput(bis, "UTF-8");
			//parser.setFeature(XmlPullParser.NO_NAMESPACE, true);
			Node node = new Node();
			node.parse(parser);
			Element ele = node.createElement(namespace, action);
			for (int i = 0; i <node.getChildCount(); i++) {
				ele.addChild(node.getType(i), node.getChild(i));
			}
			return ele;
		} catch (XmlPullParserException e) {
			throw new JSONException(e);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	public static void populate(SoapObject so, JSONObject json)
			throws JSONException {

		Iterator<?> kit = json.keys();
		while (kit.hasNext()) {
			Object o;

			String key = kit.next().toString();
			Object v = json.get(key);
			if (v instanceof JSONObject) {
				SoapObject sv = new SoapObject();
				populate(sv, (JSONObject) v);
				o = sv;
			} else if (v instanceof JSONArray) {
				SoapObject sv = new SoapObject();
				JSONArray arr = JSONArray.class.cast(v);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject item = arr.getJSONObject(i);
					if (item != null) { //
						SoapObject io = new SoapObject();
						populate(io, item);
						sv.addSoapObject(io);
					}
				}
				o = sv;
			} else {
				o = v;
			}
			so.addProperty(key, o);
		}
	}
}

package com.cheeray.ws.bridge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.util.Vector;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.io.KXmlSerializer;
import org.kxml2.kdom.Element;
import org.xmlpull.v1.XmlPullParserException;

@Named
public class Soaper {

	@Inject
	Router router;

	public Vector<?> soap(HttpServletRequest req) throws JSONException,
			IOException {
		JSONObject json = new JSONObject(IO.read(req));
		// TODO: Route to SOAP ...
		String action = req.getAttribute(Route.ACTION).toString();
		if (action == null) {
			throw new JSONException("Missing SOAP action.");
		}
		final Route route = router.route(action);
		if (route == null) {
			throw new JSONException("Not support action:" + action);
		}
		final SoapObject so = new SoapObject(route.getNameSpace(),
				route.getAction());

		Envelope envelope = createEnvelope(so, IO.toElement(json, route.getNameSpace(),
				route.getAction()));
		KXmlSerializer xmlWriter = new KXmlSerializer();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		xmlWriter.setOutput(outputStream, "UTF-8");
		envelope.write(xmlWriter);
		xmlWriter.flush();
		Logger.getLogger(Soaper.class.getName()).info(outputStream.toString());
		
		HttpTransportSE ht = getHttpTransportSE(route.getSoapUrl());
		try {
			ht.call(so.getName(), envelope);
			Vector<?> ro = (Vector<?>) envelope.getResponse();
			return ro;
		} catch (HttpResponseException | XmlPullParserException | SoapFault e) {
			throw new JSONException(e);
		}
	}

	private final HttpTransportSE getHttpTransportSE(String serviceUrl) {
		HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, serviceUrl,
				60000);
		ht.debug = true;
		ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
		return ht;
	}

	private Envelope createEnvelope(SoapObject so, Element body) {
		Envelope envelope = new Envelope(
				SoapEnvelope.VER11, body);
		envelope.dotNet = false;
		envelope.implicitTypes = true;
		envelope.setAddAdornments(false);
		envelope.setOutputSoapObject(so);
		return envelope;
	}
}

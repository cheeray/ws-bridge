package com.cheeray.ws.bridge;

import java.io.IOException;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlSerializer;

public class Envelope extends SoapSerializationEnvelope{

	final Node body;
	
	public Envelope(int version, Node body) {
		super(version);
		this.body = body;
	}

	@Override
	public void writeBody(XmlSerializer writer) throws IOException {
		if (encodingStyle != null) {
            writer.attribute(env, "encodingStyle", encodingStyle);
        }
		body.write(writer);
	}

}

package com.brassorange.eventapp.services.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.brassorange.eventapp.model.Profile;

public class ProfileParser extends DefaultHandler {

	private StringBuffer buffer = new StringBuffer();
	private Profile profile;
	
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		buffer.setLength(0);
		if (localName.equals("profile")) {
			profile = new Profile();
		}

	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException {
		if (localName.equals("id")) {
			profile.id = buffer.toString();
		} else if (localName.equals("firstName")) {
			profile.firstName = buffer.toString();
		} else if (localName.equals("middleNames")) {
			profile.middleNames = buffer.toString();
		} else if (localName.equals("lastName")) {
			profile.lastName = buffer.toString();
		} else if (localName.equals("title")) {
			profile.title = buffer.toString();
		} else if (localName.equals("biography")) {
			profile.biography = buffer.toString();
		} else if (localName.equals("imageName")) {
			profile.imageName = buffer.toString();
		} else if (localName.equals("email")) {
			profile.email = buffer.toString();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}

	public Profile retrieveProfile() {
		return profile;
	}

}

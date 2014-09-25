package com.brassorange.eventapp.services.parsers;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.brassorange.eventapp.model.Person;

public class PersonParser extends DefaultHandler {

	private StringBuffer buffer = new StringBuffer();
	private ArrayList<Person> people;
	private Person person;
	
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		buffer.setLength(0);
		if (localName.equals("people")) {
			people = new ArrayList<Person>();
		} else if (localName.equals("person")) {
			person = new Person();
		}

	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException {
		if (localName.equals("person")) {
			people.add(person);
		} else if (localName.equals("uid")) {
			person.uid = buffer.toString();
		} else if (localName.equals("firstName")) {
			person.firstName = buffer.toString();
		} else if (localName.equals("middleNames")) {
			person.middleNames = buffer.toString();
		} else if (localName.equals("lastName")) {
			person.lastName = buffer.toString();
		} else if (localName.equals("title")) {
			person.title = buffer.toString();
		} else if (localName.equals("biography")) {
			person.biography = buffer.toString();
		} else if (localName.equals("imageName")) {
			person.imageName = buffer.toString();
		} else if (localName.equals("email")) {
			person.email = buffer.toString();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}

	public ArrayList<Person> retrievePeople() {
		return people;
	}

}

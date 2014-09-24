package com.brassorange.eventapp.services;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.brassorange.eventapp.model.Person;
import com.brassorange.eventapp.model.Profile;
import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.services.parsers.PersonParser;
import com.brassorange.eventapp.services.parsers.ProfileParser;
import com.brassorange.eventapp.services.parsers.ProgramParser;

public class XmlParser {
	private XMLReader initializeReader() throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XMLReader xmlreader = parser.getXMLReader();
		return xmlreader;
	}
	
	public Program parseProgramResponse(String xml) {
		try {
			XMLReader xmlreader = initializeReader();
			ProgramParser programHandler = new ProgramParser();
			xmlreader.setContentHandler(programHandler);
			xmlreader.parse(new InputSource(new StringReader(xml)));
			return programHandler.retrieveAgenda();
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	public ArrayList<Person> parsePeopleResponse(String xml) {
		try {
			XMLReader xmlreader = initializeReader();
			PersonParser peopleHandler = new PersonParser();
			xmlreader.setContentHandler(peopleHandler);
			xmlreader.parse(new InputSource(new StringReader(xml)));
			return peopleHandler.retrievePeople();
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	public Profile parseProfileResponse(String xml) {
		try {
			XMLReader xmlreader = initializeReader();
			ProfileParser profileHandler = new ProfileParser();
			xmlreader.setContentHandler(profileHandler);
			xmlreader.parse(new InputSource(new StringReader(xml)));
			return profileHandler.retrieveProfile();
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

}

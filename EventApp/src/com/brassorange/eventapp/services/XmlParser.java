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
import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.services.parsers.PersonParser;
import com.brassorange.eventapp.services.parsers.ProgramParser;

public class XmlParser {
	private XMLReader initializeReader() throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// create a parser
		SAXParser parser = factory.newSAXParser();
		// create the reader (scanner)
		XMLReader xmlreader = parser.getXMLReader();
		return xmlreader;
	}
	
	public Program parseProgramResponse(String xml) {
		try {
			XMLReader xmlreader = initializeReader();
			ProgramParser agendaHandler = new ProgramParser();

			// assign our handler
			xmlreader.setContentHandler(agendaHandler);
			// perform the synchronous parse
			xmlreader.parse(new InputSource(new StringReader(xml)));

			return agendaHandler.retrieveAgenda();
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

			// assign our handler
			xmlreader.setContentHandler(peopleHandler);
			// perform the synchronous parse
			xmlreader.parse(new InputSource(new StringReader(xml)));

			return peopleHandler.retrievePeople();
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

}

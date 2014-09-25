package com.brassorange.eventapp.services.parsers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.brassorange.eventapp.model.Person;
import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.model.ProgramItem;

public class ProgramParser extends DefaultHandler {

	private StringBuffer buffer = new StringBuffer();
	private Program agenda;
	private ProgramItem pi;
	
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		buffer.setLength(0);
		if (localName.equals("agenda")) {
			agenda = new Program();
		} else if (localName.equals("event")) {
			pi = new ProgramItem();
		}

	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException {
		if (localName.equals("event")) {
			agenda.programItems.add(pi);
		} else if (localName.equals("splashUrl")) {
			agenda.splashUrl = buffer.toString();
		} else if (localName.equals("id")) {
			pi.id = buffer.toString();
		} else if (localName.equals("title")) {
			pi.title = buffer.toString();
		} else if (localName.equals("summary")) {
			pi.summary = buffer.toString();
		} else if (localName.equals("content")) {
			pi.content = buffer.toString();
		} else if (localName.equals("presenterUid")) {
			Person presenter = new Person();
			presenter.uid = buffer.toString();
			pi.presenter = presenter;
		} else if (localName.equals("date")) {
			try {
				pi.date = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.ENGLISH).parse(buffer.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (localName.equals("duration")) {
			pi.durationMin = 90;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}

	public Program retrieveAgenda() {
		return agenda;
	}

}

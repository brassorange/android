package com.brassorange.eventapp;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.brassorange.eventapp.services.FileUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SearchResultsActivity extends Activity {

	private TextView txtQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);

		txtQuery = (TextView)findViewById(R.id.txtQuery);

        handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

            Log.d(this.getClass().getSimpleName(), "handleIntent, query=" + query.getBytes().toString());

	        FileUtils fileUtils = new FileUtils(getApplicationContext());
	        String programXml = fileUtils.readFileFromInternalStorage("program.xml");
	        //String peopleXml = fileTools.readFileFromInternalStorage("people.xml");

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			NodeList results = null;
			try {
				builder = builderFactory.newDocumentBuilder();
				Document xmlDocument = builder.parse(new InputSource(new StringReader(programXml)));
				XPath xPath =  XPathFactory.newInstance().newXPath();
				String expression = "//agenda//event[contains(., '" + query + "')]";
	            results = (NodeList)xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				System.out.println("ParserConfigurationException: " + e);
			} catch (ParserConfigurationException e) {
				System.out.println("ParserConfigurationException: " + e);
			} catch (SAXException e) {
				System.out.println("SAXException: " + e);
			} catch (IOException e) {
				System.out.println("IOException: " + e);
			}
			if (results != null) {
				String r = "";
				for (int n=0; n<results.getLength(); n++) {
					Node node = results.item(n);
					r += "\n" + node.getNodeName() + node.getLastChild().getNodeValue();
				}
				txtQuery.setText("Search Query: " + r);
			}
		}
	}
}
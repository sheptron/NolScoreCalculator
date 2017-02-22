/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.EventList;
import IofXml30.java.ResultList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.JAXB;

/**
 *
 * @author shep
 */
public class EventorInterface {
    
    private static final String EVENTOR_BASE = "https://eventor.orienteering.asn.au/api/";

    public static String getEventorData(String eventorQuery) throws MalformedURLException, IOException {
       
        String API_KEY = EventorApiKey.getApiKey();
        
        URL baseUrl = new URL(EVENTOR_BASE + eventorQuery);

        URLConnection con = baseUrl.openConnection();

        con.setRequestProperty("Accept", "application/xml");
        con.setRequestProperty("ApiKey", API_KEY);

        String xmlString;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            xmlString = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                xmlString += inputLine;
            }   System.out.println(inputLine);
        }

        return xmlString;
    }
    
    /*public static String getEventorData(String eventorQuery, String args) throws MalformedURLException, IOException{
        
        URL baseUrl = new URL(EVENTOR_BASE + eventorQuery + args);

        URLConnection con = baseUrl.openConnection();

        con.setRequestProperty("Accept", "application/xml");
        con.setRequestProperty("ApiKey", API_KEY);
        //con.setRequestProperty("eventId", args);

        String xmlString;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            xmlString = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                xmlString += inputLine;
            }   System.out.println(inputLine);
        }

        return xmlString;
        
    }*/
    
    public static ResultList downloadResultList(EventList eventList, int eventIndex) throws IOException{
        
        String eventId = eventList.getEvent().get(eventIndex).getId().getValue();
                
        String eventorQuery = "results/event/iofxml?eventId=" + eventId;
        String xmlString = getEventorData(eventorQuery);
            
                
        ResultList thisResultList = JAXB.unmarshal(new StringReader(xmlString), ResultList.class);
        return thisResultList;
    }
}

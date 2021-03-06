/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.Event;
import IofXml30.java.EventList;
import IofXml30.java.ResultList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.UnmarshalException;
import static nolscorecalculator.NolScoreCalculator.DEV;

/**
 *
 * @author shep
 * 
 * TODO - could use results/organisation to grab selected (ie NOL) results only
 * 
 * Results from:
 * https://eventor.orientering.se/api/results/event/iofxml
 * Using (any) API key
 */
public class EventorInterface {
    
    private static final String EVENTOR_BASE = "https://eventor.orienteering.asn.au/api/";
    
    public static EventorApi.EventList getEventList(String fromDate, String toDate) {
        /*
        
        GET https://eventor.orientering.se/api/events
        Returnerar en lista med tävlingar som matchar sökparametrarna.
        
        fromDate 		0000-01-01              Startdatum (åååå-mm-dd).
        toDate                  9999-12-31              Slutdatum (åååå-mm-dd).
        fromModifyDate 		0000-01-01 00:00:00 	Inkluderar endast tävlingar som ändrats efter denna tidpunkt (åååå-mm-dd hh:mm:ss).
        toModifyDate 		9999-12-31 23:59:59 	Inkluderar endast tävlingar som ändrats före denna tidpunkt (åååå-mm-dd hh:mm:ss).
        eventIds                                        Kommaseparerad lista med tävlings-id:n. Utelämna för att inkludera alla tävlingar.
        organisationIds                                 Kommaseparerad lista med organisations-id:n för arrangörsklubbarna. Om ett distrikts organisations-id anges kommer alla tävlingar som arrangeras av en klubb i distriktet att inkluderas. Utelämna för att inkludera alla tävlingar.
        classificationIds                               Kommaseparerad lista med tävlingstyps-id:n, där 1=mästerskapstävling, 2=nationell tävling, 3=distriktstävling, 4=närtävling, 5=klubbtävling, 6=internationell tävling. Utelämna för att inkludera alla tävlingar.
        includeEntryBreaks 		false           Sätt till true för att inkludera tävlingens anmälningsstopp.
        includeAttributes 		false           Sätt till true för att inkludera tävlingens tävlingsattribut.
        */
    
        String classificationIds = "1,2"; // 1=Championship, 2=National

            String eventorQuery = "events?fromDate=" + fromDate + "&toDate=" + toDate + "&classificationIds=" + classificationIds;
            String iofXmlType = "EventList";

            String xmlString = EventorInterface.getEventorData(eventorQuery, "From Date " + fromDate + " To Date " + toDate);

            // Hack here - for some reason Eventor doesn't put in the iofVersion number so parsing the XML fails
            //xmlString = xmlString.replace("<EventList>", "<EventList xmlns=\"http://www.orienteering.org/datastandard/3.0\" iofVersion=\"3.0\">");

            EventorApi.EventList eventList = JAXB.unmarshal(new StringReader(xmlString), EventorApi.EventList.class);
            
            return eventList;
    }

    public static String getEventorData(String eventorQuery, String description) {

        String xmlString = "";
        String API_KEY = EventorApiKey.getApiKey();
        URL baseUrl;

        try {
            baseUrl = new URL(EVENTOR_BASE + eventorQuery);
        }
        catch (MalformedURLException e){
            JFrame frame = new JFrame("Warning");
            JOptionPane.showMessageDialog(frame, e.getMessage());
            return xmlString;
        }

        try {

            URLConnection con = baseUrl.openConnection();

            con.setRequestProperty("Accept", "application/xml");
            con.setRequestProperty("ApiKey", API_KEY);

            //String xmlString;
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            //xmlString = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                xmlString += inputLine;
            }
            //System.out.println(inputLine);
        } catch (IOException io) {
            System.out.println("Catching IOException");            
            System.out.println(io.getMessage());
            String warning = "Trying to get data for \"" + description + "\" got this error: " + io.getMessage();
            JOptionPane.showMessageDialog(null, warning);
        }

        return xmlString;
    }
            
    public static ResultList downloadResultList(EventorApi.EventList eventList, int eventIndex) throws Exception {
        
        String eventId = eventList.getEvent().get(eventIndex).getEventId().getContent(); //.getEventorId().getValue();
                
        String eventorQuery = "results/event/iofxml?eventId=" + eventId;
        String description = eventList.getEvent().get(eventIndex).getName().getContent();
        String xmlString = getEventorData(eventorQuery, description);
          
        if (xmlString.equals("")){
            // Somethings gone wrong in the download
            // We've alread shown a message - just make sure whoever asked for this data knows we've had an exception...
            throw new IOException();
        }
        
        try{        
            // Testing ONLY
            if (DEV) stringToFile(xmlString, description); // Dump downloaded XML to a file
            
            ResultList thisResultList = JAXB.unmarshal(new StringReader(xmlString), ResultList.class);        
            return thisResultList;
        }
        catch (DataBindingException e){
            return new ResultList();
        }
    }
    
    public static ResultList downloadResultListForEventRaceId(String eventId, String eventRaceId) throws Exception {
                
        String eventorQuery = "results/event/iofxml?eventId=" + eventId + "&eventRaceId=" + eventRaceId;
        String description = eventId + " : " + eventRaceId;
        String xmlString = getEventorData(eventorQuery, description);
          
        if (xmlString.equals("")){
            // Somethings gone wrong in the download
            // We've alread shown a message - just make sure whoever asked for this data knows we've had an exception...
            throw new IOException();
        }
        
        try{        
            // Testing ONLY
            if (DEV) stringToFile(xmlString, description); // Dump downloaded XML to a file
            
            ResultList thisResultList = JAXB.unmarshal(new StringReader(xmlString), ResultList.class);        
            return thisResultList;
        }
        catch (DataBindingException e){
            return new ResultList();
        }
    }
    
    public static Event downloadEvent(String eventId)  throws Exception{
        // https://eventor.orientering.se/api/event/{eventId}
        String eventorQuery = "event/" + eventId;
        String description = "";
        
        String xmlString = getEventorData(eventorQuery, description);
          
        if (xmlString.equals("")){
            // Somethings gone wrong in the download
            // We've alread shown a message - just make sure whoever asked for this data knows we've had an exception...
            throw new IOException();
        }
        
        try{        
            // Testing ONLY
            if (DEV) stringToFile(xmlString, description); // Dump downloaded XML to a file
            
            Event event = JAXB.unmarshal(new StringReader(xmlString), Event.class);        
            return event;
        }
        catch (DataBindingException e){
            return new Event();
        }
        
        
    }    
    
     public static ArrayList<String> downloadListOfEventRaceIds(String eventId)  throws Exception{
        // https://eventor.orientering.se/api/event/{eventId}
        String eventorQuery = "event/" + eventId;
        String description = "";
        
        String patternString = "<EventRaceId>(\\d+)</EventRaceId>";               
        
        String xmlString = getEventorData(eventorQuery, description);        
        
        if (xmlString.equals("")){
            // Somethings gone wrong in the download
            // We've alread shown a message - just make sure whoever asked for this data knows we've had an exception...
            throw new IOException();
        }
        
        try{        
            // Testing ONLY
            if (DEV) stringToFile(xmlString, description); // Dump downloaded XML to a file
            
            ArrayList<String> eventRaceIds = new ArrayList<>();
            
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(xmlString);
            
            while(matcher.find()) {                                
                eventRaceIds.add(matcher.group(1));        
            }
            return eventRaceIds;

        }
        catch (DataBindingException e){
            return new ArrayList<>();
        }
        
        
    }   
    
    public static void stringToFile(String xmlSource, String description) throws IOException {
        
        String cleanedDescription = description.replace("/", "-");
        java.io.FileWriter fw = new java.io.FileWriter("/home/shep/Desktop/" + cleanedDescription + ".xml");
        //fw.write(xmlSource);       
        //fw.close();
        
        BufferedWriter out = new BufferedWriter(fw);
        out.write(xmlSource);
        out.close();
        fw.close();
    }
}

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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.UnmarshalException;

/**
 *
 * @author shep
 * 
 * TODO - could use results/organisation to grab selected (ie NOL) results only
 */
public class EventorInterface {
    
    private static final String EVENTOR_BASE = "https://eventor.orienteering.asn.au/api/";

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
            JFrame frame = new JFrame("Warning");
            String warning = "Trying to get data for \"" + description + "\" got this error: " + io.getMessage();
            JOptionPane.showMessageDialog(frame, warning);
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
    
    public static ResultList downloadResultList(EventList eventList, int eventIndex) throws Exception {
        
        //String eventId = eventList.getEvent().get(eventIndex).getId().getValue();
        String eventId = eventList.getEvent().get(eventIndex).getEventorId().getValue();
                
        String eventorQuery = "results/event/iofxml?eventId=" + eventId;
        String description = eventList.getEvent().get(eventIndex).getName();
        String xmlString = getEventorData(eventorQuery, description);
          
        if (xmlString.equals("")){
            // Somethings gone wrong in the download
            // We've alread shown a message - just make sure whoever asked for this data knows we've had an exception...
            throw new IOException();
        }
        
        try{        
        ResultList thisResultList = JAXB.unmarshal(new StringReader(xmlString), ResultList.class);
        return thisResultList;
        }
        catch (DataBindingException e){
            return new ResultList();
        }
    }
}

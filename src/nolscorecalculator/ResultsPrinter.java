/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

//import IofXml30.java.ClassResult;
//import IofXml30.java.Clazz;
import IofXml30.java.Event;
import IofXml30.java.Id;
import IofXml30.java.PersonResult;
//import IofXml30.java.ObjectFactory;
//import IofXml30.java.Organisation;
//import IofXml30.java.Person;
//import IofXml30.java.PersonName;
//import IofXml30.java.PersonRaceResult;
//import IofXml30.java.PersonResult;
//import IofXml30.java.ResultList;
import NolXml10.NolClassResult;
import NolXml10.NolEvent;
import NolXml10.NolEventList;
import NolXml10.NolPersonRaceResult;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import static nolscorecalculator.NolScoreCalculator.CREATOR;

import NolXml10.NolPersonResult;
import NolXml10.NolResultList;
import NolXml10.ObjectFactory;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author shep
 */
public class ResultsPrinter {

    public String htmlResults;
    private ArrayList<Id> events;
    
    public ResultsPrinter() {
    }
    
    public void allResultsToNolXml(ArrayList<Entity>[] fullResultList, Map<Integer, Event> nolRaceNumberToId, String outputDirectory) throws JAXBException {
        
        // Produce the XML then use an XSL template to produce a HTML results file                
        
        // Number of Events
        int numberOfEvents = nolRaceNumberToId.size();
        
        if (numberOfEvents == 0) return;            
        
        ObjectFactory factory = new ObjectFactory();
        
        NolResultList resultList = factory.createNolResultList();
        resultList.setIofVersion("3.0");
        resultList.setCreator(CREATOR);
        
        ArrayList<NolClassResult> classResults = new ArrayList<>();
        
        ArrayList<NolEvent> nolEvents = new ArrayList<>();
        for (int i=0; i<numberOfEvents; i++){      
            NolEvent event = new NolEvent();
            event.setRaceNumber(String.format("%d", i+1));
            Event thisEvent = nolRaceNumberToId.get(i+1);
            // TODO what happens if thisEvent is NULL
            String thisEventName = thisEvent.getName();
            event.setName(thisEventName);
            nolEvents.add(event);
        }
        NolEventList eventList = factory.createNolEventList();
        eventList.setEvent(nolEvents);
        
        for (ArrayList<Entity> classResultList : fullResultList) {

            if (classResultList.isEmpty()) continue;
                
            ArrayList<NolPersonResult> personResults = new ArrayList<>();
            int place = 0;
            for (Entity athlete : classResultList) {

                place++;
                
                // Build the XML PersonResult
                NolPersonResult personResult = factory.createNolPersonResult();

                // Person 
                personResult.setName(athlete.getName());

                // Organisation                         
                personResult.setTeam(athlete.getOrganisation().getName());
                
                // Place
                personResult.setPlace(String.format("%d", place));
                
                // Total Score
                personResult.setTotal(String.format("%d", athlete.getTotalScore()));

                // Results (NOL Points)                 
                ArrayList<NolPersonRaceResult> thisPersonRaceResults = new ArrayList<>();
                for (Integer key : nolRaceNumberToId.keySet()){
                    
                    NolPersonRaceResult raceResult = factory.createNolPersonRaceResult();
                    raceResult.setScore(0);             // Add empty score for missed rounds/races
                    raceResult.setRaceNumber(key);                    

                    // Does this race exist for this athlete?    
                    int indexOfResult = athlete.getResults().indexOf(new Result(nolRaceNumberToId.get(key).getId()));
                    if (indexOfResult > -1) raceResult.setScore(athlete.getResults().get(indexOfResult).getScore());
                    
                    thisPersonRaceResults.add(raceResult);
                }
                
                // Sort Results by Race NUmber
                Collections.sort(thisPersonRaceResults, (NolPersonRaceResult a1, NolPersonRaceResult a2) -> a1.getRaceNumber() - a2.getRaceNumber());

                /*for (NolResult nolResult : athlete.getResults()) {
                    NolPersonRaceResult raceResult = factory.createNolPersonRaceResult();
                    raceResult.setScore(nolResult.getScore());
                    raceResult.setRaceNumber(0);

                    thisPersonRaceResults.add(raceResult);
                }*/

                // Build the XML PersonResult
                personResult.setResult(thisPersonRaceResults);

                // Finally add this Person Result to our list
                personResults.add(personResult);
      
                // Build Class
            }

            // Set up the Class (course) Result
            NolClassResult classResult = factory.createNolClassResult();          
            classResult.setNolClazz(classResultList.get(0).getNolCategory().toString());
            classResult.setEventList(eventList);
            classResult.setNolPersonResult(personResults);
            
            

            classResults.add(classResult);
        }

        resultList.setNolClassResult(classResults);
        
        File tempFile = new File(outputDirectory, "NOL_Results.xml");
        File outFile = new File(outputDirectory, "NOL_Results.html");

        JAXBContext jaxbContext = JAXBContext.newInstance("NolXml10");
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jaxbMarshaller.marshal(resultList, tempFile);

        // Now convert to HTML
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();

            Transformer transformer
                    = tFactory.newTransformer(new javax.xml.transform.stream.StreamSource("src/nolscorecalculator/NolHtmlResults.xsl"));

            // TODO get save file location from user
            transformer.transform(new javax.xml.transform.stream.StreamSource(tempFile),
                    new javax.xml.transform.stream.StreamResult(new FileOutputStream(outFile)));
            
            // TODO - should we delete the XML file?
        } catch (Exception e) {
            e.printStackTrace();
        }

    }    

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

//import IofXml30.java.ClassResult;
//import IofXml30.java.Clazz;
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

    ResultsPrinter(ArrayList<String> eventsList, ArrayList<Id> eventIds) {

        // Create object with header rows
        // Race results must be filled in later with ArrayList of Results
        StringBuilder html = new StringBuilder("<!DOCTYPE html>\n<html>\n<head>\n<style>\ntable, th, td {\nborder: 1px solid black;\nborder-collapse: collapse;\n}\n</style>\n</head>\n<body><table>");

        html.append("<tr><th>Place</th><th>Name</th><th>Club</th>");
        html.append("<th>Runs</th><th>Sum</th>");

        // Sort events             
        for (String event : eventsList) {
            String string = "<th>" + event + "</th>";
            html.append(string);
        }

        html.append("</tr>");

        this.htmlResults = html.toString();

        this.events = eventIds;
    }

    public void writeResults(ArrayList<NolAthlete> resultList) {

        StringBuilder html = new StringBuilder(this.htmlResults);

        int place = 1;

        for (NolAthlete athlete : resultList) {
            html.append("<tr>");
            // Place
            html.append("<td>").append(place).append("</td>");
            // Name
            html.append("<td>").append(athlete.name).append("</td>");
            // Club
            html.append("<td>").append(athlete.club).append("</td>");
            // Runs
            html.append("<td>").append(athlete.results.size()).append("</td>");
            // Sum
            html.append("<td>").append(athlete.totalScore).append("</td>");

            for (Id event : this.events) {

                String string = "<td>0</td> ";
                // Did athlete do this event?
                for (NolResult result : athlete.results) {
                    if (result.getId() == event) {
                        string = "<td>" + result.score + "</td> ";
                    }
                }

                html.append(string);
            }
            html.append("</tr>");
            place += 1;
        }
        //return "";
        this.htmlResults = html.toString();
    }

    /*public void writeJimSawkinsResults(ArrayList<Athlete> resultList, ArrayList<Athlete> division2ResultList){
        
        StringBuilder html = new StringBuilder("<!DOCTYPE html>\n<html>\n<head>\n<style>\ntable, th, td {\nborder: 1px solid black;\nborder-collapse: separate;\nmargin:0 20px;\n}\n</style>\n</head>\n<body><div>\n<table style=\"float: left\">\n<caption><h2>Division 1 Results</h2></caption>");
        html.append("<tr><th>Place</th><th>Name</th><th>Handicapped Km Rate</th>");
        
        int place = 1;

        for (Athlete athlete : resultList) {
            html.append("<tr>");
            // Place
            html.append("<td>").append(place).append("</td>");
            // Name
            html.append("<td>").append(athlete.name).append("</td>");
            // Club
            //html.append("<td>").append(athlete.club).append("</td>");
            // Handicapped Speed
            html.append("<td>").append(athlete.results.get(0).handicappedKmRate).append("</td>");
             // Course
             
             // Length
             
             // Time
             
             // Handicap
             //html.append("<td>").append(athlete.currentHandicap).append("</td>");
              
             //html.append("<td>").append(athlete.yearOfBirth).append("</td>");
            
            
            html.append("</tr>");
            place += 1;
        }
        
        html.append("</table>\n<table style=\"float: left\">\n<caption><h2>Division 2 Results</h2></caption>");
        html.append("<tr><th>Place</th><th>Name</th><th>Handicapped Km Rate</th>");
        
        //
        place = 1;

        for (Athlete athlete : division2ResultList) {
            html.append("<tr>");
            // Place
            html.append("<td>").append(place).append("</td>");
            // Name
            html.append("<td>").append(athlete.name).append("</td>");
            // Club
            //html.append("<td>").append(athlete.club).append("</td>");
            // Handicapped Speed
            html.append("<td>").append(athlete.results.get(0).handicappedKmRate).append("</td>");
             // Course
             
             // Length
             
             // Time
             
             // Handicap
             //html.append("<td>").append(athlete.currentHandicap).append("</td>");
              
             //html.append("<td>").append(athlete.yearOfBirth).append("</td>");
            
            
            html.append("</tr>");
            place += 1;
        }
        html.append("</table>\n</div>\n</body>\n</html>");
        //
        
        this.htmlResults = html.toString();
    }*/
    public String finaliseTable() {
        // Write "</table>" at the end of the string
        StringBuilder html = new StringBuilder(this.htmlResults);
        html.append("</table>\n</body>\n</html>");
        this.htmlResults = html.toString();
        return this.htmlResults;
    }

    public static void xmlToHtml(String xmlString) {

    }

//    public void resultsToXml(ArrayList<NolAthlete> overallResultList) throws JAXBException {
//        // Produce an XML for Eventor
//
//        // TODO Single ArrayList for now but do an Array of ArrayLists...
//        ObjectFactory factory = new ObjectFactory();
//
//        // Event Name
//        // Event Date            
//        //currentYear = eventsList.get(0).getYear();
//        ///String event
//        //double fastestTime = overallResultList.get(0).results.get(0).getHandicappedKmRate();
//        // Build Person Results
//        ArrayList<PersonResult> personResults = new ArrayList<>();
//        int position = 1;
//        for (NolAthlete athlete : overallResultList) {
//
//            // Build the XML PersonResult
//            PersonResult personResult = factory.createPersonResult();
//
//            // Person 
//            PersonName personName = factory.createPersonName();            
//            personName.setFamily(athlete.surname);
//            personName.setGiven(athlete.firstName);
//            Person person = factory.createPerson();
//            person.setName(personName);
//            //person.setSex(athlete.getSex());                                  
//            //Id personId = factory.createId();
//            //personId.setValue(String.valueOf(athlete.id));
//            // Only Set the ID if The Athlete has one so eventor won't cry 
//            //if (athlete.id != 0) {
//            //    ArrayList<Id> personIds = new ArrayList<>();
//            //    personIds.add(personId);
//            //    person.setID(personIds);
//            //}
//            // Organisation
//            Organisation organisation = factory.createOrganisation();
//            organisation.setId(athlete.getOrganisation().getId());
//
//            // Organisation (club)
//            //organisation.setName(athlete.organisation.getName());
//            //organisation.setShortName(athlete.organisation.getShortName());
//            //Country cuntry = factory.createCountry();
//            //cuntry.setCode(athlete.organisation.getCountry()); // TODO XOrganisation needs an XCountry to get this to work properly!
//            //cuntry.setValue(athlete.organisation.getCountry());
//            //organisation.setCountry(cuntry); 
//            // Don't add an empty organisation (eventor doesn't like it)
//            //boolean isMember = !athlete.organisation.getId().equals("");
//
//            // Results
//            // NOL Points - but we will use Race Time
//            // TODO Race Numbers
//            ArrayList<PersonRaceResult> thisPersonRaceResults = new ArrayList<>();
//            for (NolResult nolResult : athlete.getResults()) {
//                PersonRaceResult raceResult = factory.createPersonRaceResult();
//                raceResult.setTime((double) nolResult.getScore());
//                //raceResult.setRaceNumber(BigInteger.ONE);
//
//                thisPersonRaceResults.add(raceResult);
//            }
//
//            // Status
//            //if (athlete.results.get(0).status) {
//            //    result.setStatus(ResultStatus.OK);
//            //}
//            //else result.setStatus(ResultStatus.DISQUALIFIED);
//            // TODO Time Behind
//            //double timeBehind = athlete.results.get(0).getHandicappedKmRate() - fastestTime;
//            //result.setTimeBehind(timeBehind);
//            // Position
//            //result.setPosition(BigInteger.valueOf(position));
//            // XML needs an array of results (there will only be one result)
//            //ArrayList<PersonRaceResult> results = new ArrayList<>();
//            //results.add(result);
//            // Build the XML PersonResult
//            //PersonResult personResult = factory.createPersonResult();                    
//            personResult.setPerson(person);
//            //if (isMember) personResult.setOrganisation(organisation);
//            personResult.setResult(thisPersonRaceResults);
//
//            // Finally add this Person Result to our list
//            personResults.add(personResult);
//
//            position += 1;
//            // Build Class
//        }
//
//        // Set up the Class (course) Result (there will only be one - Handicap)
//        ClassResult classResult = factory.createClassResult();
//        classResult.setPersonResult(personResults);
//        ArrayList<ClassResult> classResults = new ArrayList<>();
//        classResults.add(classResult);
//
//        Clazz clazz = factory.createClass();
//        //Id classId = factory.createId();
//        //classId.setValue("1");
//        //clazz.setId(classId);
//        clazz.setName(overallResultList.get(0).getNolCategory().toString());
//        //clazz.setShortName("Handicap");
//        classResult.setClazz(clazz);
//
//        // Course
//        //SimpleRaceCourse course = factory.createSimpleRaceCourse();
//        //course.setLength(1000.0);
//        //ArrayList<SimpleRaceCourse> courses = new ArrayList<>();  
//        //courses.add(course);
//        //classResult.setCourse(courses);
//        ResultList resultList = factory.createResultList();
//        resultList.setClassResult(classResults);
//        resultList.setIofVersion("3.0");
//        resultList.setCreator(CREATOR);
//
//        //Event event = factory.createEvent();
//        //Id eventId = factory.createId();
//        //eventId.setValue("1");
//
//        // Event Name
//        //event.setName("Test");
//
//        //resultList.setEvent(event);
//
//        //JAXBElement<ResultList> element = factory. .createResultList();
//        String filename = "NOL_test.xml";
//
//        File file = new File("/home/shep/Desktop/", filename);
//
//        //File file = new File("/home/shep/Desktop/testFile.xml");
//        JAXBContext jaxbContext = JAXBContext.newInstance("IofXml30.java");
//        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//
//        // output pretty printed
//        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//        jaxbMarshaller.marshal(resultList, file);
//        
//        // Now convert to HTML
//        try {
//            TransformerFactory tFactory = TransformerFactory.newInstance();
//
//            Transformer transformer =
//                    tFactory.newTransformer
//                 (new javax.xml.transform.stream.StreamSource
//                    ("NolHtmlResults.xsl"));
//
//            transformer.transform
//              (new javax.xml.transform.stream.StreamSource(file),
//                      new javax.xml.transform.stream.StreamResult
//                    ( new FileOutputStream("howto.html")));
//        }
//        catch (Exception e) {
//            e.printStackTrace( );
//        }
//
//    }
    
    public void resultsToNolXml(ArrayList<NolAthlete> overallResultList) throws JAXBException {
        // Produce an XML for Eventor
        
        ArrayList<NolClassResult> classResults = new ArrayList<>();

        // TODO Single ArrayList for now but do an Array of ArrayLists...
        ObjectFactory factory = new ObjectFactory();
        
        ArrayList<NolPersonResult> personResults = new ArrayList<>();
        int position = 1;
        for (NolAthlete athlete : overallResultList) {

            // Build the XML PersonResult
            NolPersonResult personResult = factory.createNolPersonResult();

            // Person 
            personResult.setName(athlete.getName());
            
            // Organisation                        
            personResult.setTeam(athlete.getOrganisation().getName());
            
            // Results
            // NOL Points
            // TODO Race Numbers
            // TODO add empty score for missed rounds/races
            ArrayList<NolPersonRaceResult> thisPersonRaceResults = new ArrayList<>();
            for (NolResult nolResult : athlete.getResults()) {
                NolPersonRaceResult raceResult = factory.createNolPersonRaceResult();
                raceResult.setScore(nolResult.getScore());
                raceResult.setRaceNumber(0); // TODO

                thisPersonRaceResults.add(raceResult);
            }

            
            // Build the XML PersonResult
            personResult.setResult(thisPersonRaceResults);

            // Finally add this Person Result to our list
            personResults.add(personResult);

            position += 1;
            // Build Class
        }

        // Set up the Class (course) Result
        NolClassResult classResult = factory.createNolClassResult();
        classResult.setNolPersonResult(personResults);   
        
        classResult.setNolClazz(overallResultList.get(0).getNolCategory().toString());

        classResults.add(classResult);

        NolResultList resultList = factory.createNolResultList();
        resultList.setNolClassResult(classResults);
        resultList.setIofVersion("3.0");
        resultList.setCreator(CREATOR);

        String filename = "NOL_test.xml";

        File file = new File("/home/shep/Desktop/", filename);

        JAXBContext jaxbContext = JAXBContext.newInstance("NolXml10");
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jaxbMarshaller.marshal(resultList, file);
        
        // Now convert to HTML
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();

            Transformer transformer =
                    tFactory.newTransformer
                 (new javax.xml.transform.stream.StreamSource
                    ("src/nolscorecalculator/NolHtmlResults.xsl"));
            
            // TODO get save file location from user

            transformer.transform
              (new javax.xml.transform.stream.StreamSource(file),
                      new javax.xml.transform.stream.StreamResult
                    ( new FileOutputStream("howto.html")));
        }
        catch (Exception e) {
            e.printStackTrace( );
        }

    }
    
    public void allResultsToNolXml(ArrayList<NolAthlete>[] fullResultList, Map<Integer, Id> nolRaceNumberToId, String outputDirectory) throws JAXBException {
        
        // Produce the XML then use an XSL template to produce a HTML results file
        
        // Number of Events
        int numberOfEvents = nolRaceNumberToId.size();
        
        ObjectFactory factory = new ObjectFactory();
        
        NolResultList resultList = factory.createNolResultList();
        resultList.setIofVersion("3.0");
        resultList.setCreator(CREATOR);
        
        ArrayList<NolClassResult> classResults = new ArrayList<>();
        
        ArrayList<NolEvent> nolEvents = new ArrayList<>();
        for (int i=0; i<numberOfEvents; i++){      
            NolEvent event = new NolEvent();
            event.setRaceNumber(String.format("%d", i+1));
            nolEvents.add(event);
        }
        NolEventList eventList = factory.createNolEventList();
        eventList.setEvent(nolEvents);
        
        for (ArrayList<NolAthlete> classResultList : fullResultList) {

            ArrayList<NolPersonResult> personResults = new ArrayList<>();
            int place = 0;
            for (NolAthlete athlete : classResultList) {

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
                    int indexOfResult = athlete.getResults().indexOf(new NolResult(nolRaceNumberToId.get(key)));
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
    
    public void teamResultsToNolXml(ArrayList<NolTeam>[] fullResultList, String outputDirectory) throws JAXBException {
        
        // TODO just cast the teams into individuals and reuse!
        
        // Produce the XML then use an XSL template to produce a HTML results file
        
        ObjectFactory factory = new ObjectFactory();
        
        NolResultList resultList = factory.createNolResultList();
        resultList.setIofVersion("3.0");
        resultList.setCreator(CREATOR);
        
        ArrayList<NolClassResult> classResults = new ArrayList<>();
        
        for (ArrayList<NolTeam> classResultList : fullResultList) {

            ArrayList<NolPersonResult> teamResults = new ArrayList<>();
            int position = 1;
            for (NolTeam team : classResultList) {

                // Build the XML PersonResult
                NolPersonResult personResult = factory.createNolPersonResult();

                // Person 
                personResult.setName(team.getName());

                // Organisation                        
                //personResult.setTeam(team.getOrganisation().getName());

                // Results
                // NOL Points
                // TODO Race Numbers
                // TODO add empty score for missed rounds/races
                ArrayList<NolPersonRaceResult> thisPersonRaceResults = new ArrayList<>();
                for (NolTeamResult nolResult : team.getResults()) {
                    NolPersonRaceResult raceResult = factory.createNolPersonRaceResult();
                    raceResult.setScore(nolResult.getScore());
                    raceResult.setRaceNumber(0); // TODO

                    thisPersonRaceResults.add(raceResult);
                }

                // Build the XML PersonResult
                personResult.setResult(thisPersonRaceResults);

                // Finally add this Person Result to our list
                teamResults.add(personResult);

                position += 1;
                // Build Class
            }

            // Set up the Class (course) Result
            NolClassResult classResult = factory.createNolClassResult();
            classResult.setNolPersonResult(teamResults);
            classResult.setNolClazz(classResultList.get(0).getNolCategory().toString());

            classResults.add(classResult);
        }

        resultList.setNolClassResult(classResults);
        
        File tempFile = new File(outputDirectory, "NOL_Team_Results.xml");
        File outFile = new File(outputDirectory, "NOL_Team_Results.html");

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

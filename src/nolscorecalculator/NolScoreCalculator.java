/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nolscorecalculator;

import IofXml30.java.ClassResult;
import IofXml30.java.EventList;
import IofXml30.java.Id;
import IofXml30.java.ObjectFactory;
import IofXml30.java.Organisation;
import IofXml30.java.OrganisationList;
import IofXml30.java.PersonResult;
import IofXml30.java.ResultList;
import IofXml30.java.ResultStatus;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import NolXml10.NolResultList;
import java.io.FileOutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;


/**
 *
 * @author shep
 */
public class NolScoreCalculator {
    
    public static final boolean DEV =true;

    public static final String CREATOR = "Sheptron Industries";
    public static final String EVENT_SELECTION_DIALOG_STRING = "Select all the NOL races from the list below...";
    
    // TODO NolCategory toString insert space
    public enum NolCategory {
        SeniorMen, SeniorWomen, JuniorMen, JuniorWomen
    };
    
    public enum NolTeamName {
        Arrows, Cockatoos, Cyclones, Foresters, Nomads, Nuggets, Stingers
    };
    
    // Map of Eventor ID to Team Names - hard coded, maybe not a good idea? Can we determine NOL teams from Eventor download only?
    public static Map<String, String> nolOrganisations = createNolOrganisationsMap();
    
    public static ArrayList<NolTeam> NOLSeasonTeams = createNolSeasonTeams();
    
    private static final String[] VALID_ELITE_CLASSES = {"M21E", "W21E", "M17-20E", "M-20E", "W17-20E", "W-20E"};
    private static final String[] VALID_NONELITE_CLASSES = {"M21A", "W21A", "M20A", "W20A"};

    /**
     * @param args the command line arguments
     * @throws java.net.MalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException, IOException, JAXBException, SAXException, ParserConfigurationException {
        {
            // Getting Organisation List for Development...
            //https://eventor.orienteering.asn.au/api/organisations&key=780b352c5c1e4c718e4ad35b48e77397
            //String query = "organisations";
            //String xml = EventorInterface.getEventorData(query);
            //OrganisationList organisationList = JAXB.unmarshal(new StringReader(xml), OrganisationList.class);
            
            /// TESTING
//            String filename = "NOL_test.xml";
//
//            File file = new File("/home/shep/Desktop/", filename);
//
//            try {
//                TransformerFactory tFactory = TransformerFactory.newInstance();
//
//                Transformer transformer = tFactory.newTransformer(new javax.xml.transform.stream.StreamSource("src/nolscorecalculator/NolHtmlResults.xsl"));
//
//                transformer.transform(new javax.xml.transform.stream.StreamSource(file),
//                        new javax.xml.transform.stream.StreamResult(new FileOutputStream("/home/shep/Desktop/NOL_test.html")));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            
            /// END TESTING
            
            // TODO get dates to/from        
            
            //Map<String, String> nolOrganisationz = createNolOrganisationsMap();

            String fromDate = "2016-10-01";
            String toDate = "2016-10-10";
            String classificationIds = "1,2"; // 1=Championship, 2=National

            //String eventorQuery = "events?fromDate=2016-12-01&toDate=2016-12-31";
            String eventorQuery = "events?fromDate=" + fromDate + "&toDate=" + toDate + "&classificationIds=" + classificationIds;
            String iofXmlType = "EventList";

            String xmlString = EventorInterface.getEventorData(eventorQuery);

            // Hack here - for some reason Eventor doesn't put in the iofVersion number so parsing the XML fails
            xmlString = xmlString.replace("<EventList>", "<EventList xmlns=\"http://www.orienteering.org/datastandard/3.0\" iofVersion=\"3.0\">");

            EventList eventList = JAXB.unmarshal(new StringReader(xmlString), EventList.class);

            // Now sort which event we want (we want to get their EventId so we can download the results)
            // Find all with "NOL" in the title?
            // Give user a selection box
            // Build up a list
            int[] indexOfSelectedEvents;
            int numberOfDownloadedEvents = eventList.getEvent().size();
            String eventsInDateRange[] = new String[numberOfDownloadedEvents];
            for (int i = 0; i < numberOfDownloadedEvents; i++) {
                eventsInDateRange[i] = eventList.getEvent().get(i).getName();
            }

            JList list = new JList(eventsInDateRange);
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JOptionPane jp = new JOptionPane();

            int selection = jp.showConfirmDialog(null, new JScrollPane(list), EVENT_SELECTION_DIALOG_STRING, JOptionPane.OK_CANCEL_OPTION);

            if (selection == JOptionPane.CANCEL_OPTION) {
                // Code to use when CANCEL is PRESSED.
                // Exit? do it for now... prob need to give the user a warning.
                return;
            }

            indexOfSelectedEvents = list.getSelectedIndices();

            int numberOfEvents = indexOfSelectedEvents.length;

            // Create a List of NOL Athletes t store all our results in
            ArrayList<NolAthlete> NOLSeasonResults = new ArrayList<>();
            ArrayList<Id> NOLSeasonEvents = new ArrayList<>();
            ArrayList<String> NOLSeasonEventString = new ArrayList<>();                        

            // Get Results
            //https://eventor.orientering.se/api/results/event/iofxml 
            // For each selected event
            for (int i = 0; i < numberOfEvents; i++) {
                Id eventId = eventList.getEvent().get(indexOfSelectedEvents[i]).getId();
                NOLSeasonEvents.add(eventId);
                NOLSeasonEventString.add(eventList.getEvent().get(indexOfSelectedEvents[i]).getName());
                ResultList thisResultList = EventorInterface.downloadResultList(eventList, indexOfSelectedEvents[i]);

                // Find the right classes : this may be complicated if we have M/W21A instead of E
                // Use E, if there's no E then use A
                // TODO Consider A and B finals
                boolean usingEliteClasses = isUsingEliteClasses(thisResultList);

                // Run through again and process results
                for (ClassResult classResult : thisResultList.getClassResult()) {
                    String className = classResult.getClazz().getName();

                    if (isValidClass(className, usingEliteClasses)) {
                        // Assign Points
                        
                        NolCategory nolCategory = getNolCategory(className);
                        
                        for (PersonResult personResult : classResult.getPersonResult()) {

                            // Individual
                            // To deal with runners that compete in Junior and Senior we 
                            // split an athlete into a Junior and a Senior version...
                            // Ignore someone if they didn't start - otherwise non-OK status will get ZERO points
                            if (personResult.getResult().get(0).getStatus() == ResultStatus.DID_NOT_START) {
                                continue;
                            }
                            
                            if (DEV) {
                                // DEV ONLY - translate club into state team (2016 had no NOL teams in Eventor)
                                Organisation organisation = personResult.getOrganisation();
                                organisation = testingOnlyTranslateOrganisationId(organisation);
                                personResult.setOrganisation(organisation);
                                // END DEV ONLY
                            }

                            // Create NOL Athlete and Result from the IOF PersonResult
                            NolAthlete nolAthlete = new NolAthlete(personResult, nolCategory);
                            NolResult nolResult = new NolResult(personResult, eventId);

                            // Do we need a new athlete or create a new one?
                            if (NOLSeasonResults.contains(nolAthlete)) {
                                // The NOL Athlete exists so add this races result
                                int k = NOLSeasonResults.indexOf(nolAthlete);
                                NOLSeasonResults.get(k).addResult(nolResult);
                            } else {
                                // Creating a new NOL Athlete and add them to the seasons result list
                                nolAthlete.addResult(nolResult);
                                NOLSeasonResults.add(nolAthlete);
                            }
                            
                            // Team
                            
                            // Can use nolAthlete and nolResult here                        
                            
                            
                            // Find this athletes team
                            NolTeam thisNolTeam = new NolTeam(personResult.getOrganisation(), nolCategory);
                            int indexOfNolTeam = NOLSeasonTeams.indexOf(thisNolTeam);
                            
                            if (indexOfNolTeam == -1){
                                continue;
                            } // NOT in a NOL team!
                            
                            // Add this result 
                            // See if there's results from this race
                            NolTeamResult thisNolTeamResult = new NolTeamResult(eventId);
                            int indexOfNolTeamResult = NOLSeasonTeams.get(indexOfNolTeam).getNolTeamResults().indexOf(thisNolTeamResult);
                            
                            if (indexOfNolTeamResult == -1){
                                // This athlete is the first in their team to add a result for this race
                                thisNolTeamResult.addIndividualResult(personResult);
                                NOLSeasonTeams.get(indexOfNolTeam).addResult(thisNolTeamResult);
                            } 
                            else
                            {
                                // Another team member has already had a result added
                                NOLSeasonTeams.get(indexOfNolTeam).getNolTeamResults().get(indexOfNolTeamResult).addIndividualResult(personResult);
                            }                                                    
                        }
                    }
                }
            }

            // Calculate Total Individual Scores
            int numberOfRaceToCount;
            // Count an extra race early on in the season
            if (numberOfEvents < 8) {
                numberOfRaceToCount = (int) Math.ceil((double) numberOfEvents / 2.0) + 1;
            } else {
                numberOfRaceToCount = (int) Math.ceil((double) numberOfEvents / 2.0);
            }
            for (NolAthlete nolAthlete : NOLSeasonResults) {
                nolAthlete.updateTotalScore(numberOfRaceToCount);
            }

            // Now Sort the Results -Decreasing Total Score
            // We've got all NOL Categories mixed in here but that doesn't matter - we'll selectively
            // write them out...
            Collections.sort(NOLSeasonResults, (NolAthlete a1, NolAthlete a2) -> a2.totalScore - a1.totalScore);

            /////////////////////////////////////
            // Now publish
            
            // HACK with HTML - TODO use XSLT!
            ResultsPrinter resultsPrinter = new ResultsPrinter(NOLSeasonEventString, NOLSeasonEvents);

            // Build Result lists for each Category
            ArrayList<NolAthlete> juniorMenResults = new ArrayList<>();
            ArrayList<NolAthlete> juniorWomenResults = new ArrayList<>();
            ArrayList<NolAthlete> seniorMenResults = new ArrayList<>();
            ArrayList<NolAthlete> seniorWomenResults = new ArrayList<>();
            for (NolAthlete nolAthlete : NOLSeasonResults) {
                NolCategory nolCategory = nolAthlete.getNolCategory();
                switch (nolAthlete.getNolCategory()) {
                    case SeniorMen:
                        seniorMenResults.add(nolAthlete);
                        break;
                    case SeniorWomen:
                        seniorWomenResults.add(nolAthlete);
                        break;
                    case JuniorMen:
                        juniorMenResults.add(nolAthlete);
                        break;
                    case JuniorWomen:
                        juniorWomenResults.add(nolAthlete);
                }
            }
            
            resultsPrinter.resultsToNolXml(seniorMenResults);
            
            //resultsPrinter.resultsToXml(seniorMenResults);
            
            // Just write Senior Men for now...
            resultsPrinter.writeResults(seniorMenResults);

            resultsPrinter.finaliseTable();
            
            String outputDirectory = getOutputDirectory();
            
            String outFilename = outputDirectory + "/NOL_Individual_Results.html";
            StringToFile.write(outFilename, resultsPrinter.htmlResults);
            
            
            // Team Scores

        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    private static boolean isUsingEliteClasses(ResultList resultList) {

        boolean usingEliteClasses = false;

        // Run through and decide if we have Elite (E) or A classes
        for (int j = 0; j < resultList.getClassResult().size(); j++) {
            String className = resultList.getClassResult().get(j).getClazz().getName();
            if (className.contains("M21E") || className.contains("W21E")) {
                usingEliteClasses = true;
            }
        }

        return usingEliteClasses;
    }

    private static boolean isValidClass(String className, boolean usingEliteClasses) {

        boolean validClass = false;

        if (usingEliteClasses) {
            for (String eliteClassName : VALID_ELITE_CLASSES) {
                if (className.contains(eliteClassName)) {
                    validClass = true;
                }
            }
        } else {
            for (String noneliteClassName : VALID_NONELITE_CLASSES) {
                if (className.contains(noneliteClassName)) {
                    validClass = true;
                }
            }
        }
        return validClass;
    }
    
    private static NolCategory getNolCategory(String className){
        // Decide what NOL Category this result was in
        NolCategory nolCategory;
        
        if (className.contains("W")){
            if (className.contains("21")) nolCategory = NolScoreCalculator.NolCategory.SeniorWomen;
            else nolCategory = NolScoreCalculator.NolCategory.JuniorWomen;
            
        }
        else {
            if (className.contains("21")) nolCategory = NolScoreCalculator.NolCategory.SeniorMen;
            else nolCategory = NolScoreCalculator.NolCategory.JuniorMen;
        }
        
        return nolCategory;
    }
    
    private static String getOutputDirectory(){
        // Get Output Directory
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setMultiSelectionEnabled(false);
            fc.setDialogTitle("Select a directory to save the results file...");
            
            File folder;
            File[] listOfFiles;
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    listOfFiles = new File[1];
                    listOfFiles[0] = fc.getSelectedFile();
                    folder = fc.getSelectedFile().getParentFile();
                    return fc.getSelectedFile().toString();
            
            } 
            else {
                InformationDialog.infoBox("No directory selected, press OK to exit.", "Warning");
                return "";
            }
    } 
    

    private static Map<String, String> createNolOrganisationsMap()
    {
        Map<String,String> myMap = new HashMap<>();
        myMap.put("628", "Vic Nuggets");
        myMap.put("629", "ACT Cockatoos");
        myMap.put("630", "NSW Stingers");
        myMap.put("631", "Qld Cyclones");
        myMap.put("632", "SA Arrows");
        myMap.put("633", "Tas Foresters");
        myMap.put("634", "WA Nomads");
        myMap.put("0", "No Team");
        return myMap;
    }
    
    private static ArrayList<NolTeam> createNolSeasonTeams()
    {
        // This should create Senior and Junior Men and Women teams
        // for each of the State Teams defined in the nolOrganisations map
        ArrayList<NolTeam> nolSeasonTeams = new ArrayList<>();
        
        Iterator it = nolOrganisations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            Id id = new Id();
            id.setValue((String) pair.getKey());
            Organisation organisation = new Organisation();
            organisation.setId(id);
            organisation.setName((String) pair.getValue());
            
            for (NolCategory nolCategory : NolCategory.values()){                            
                NolTeam nolTeam = new NolTeam(organisation, nolCategory);
                
                nolSeasonTeams.add(nolTeam);
            }
            //it.remove(); // avoids a ConcurrentModificationException
        }        
        
        return nolSeasonTeams;
    }
    
    private static Organisation testingOnlyTranslateOrganisationId(Organisation organisation){

        // When testing on 2016 results
        Organisation newOrganisation = new Organisation();
        Id id = new Id();
        // The last letter in the short name is the state
        String shortName = organisation.getShortName();
        
        String x = "";
        if (shortName.endsWith("V")) x = "628";            
        else if (shortName.endsWith("A")) x = "629";            
        else if (shortName.endsWith("N")) x = "630";
        else if (shortName.endsWith("Q")) x = "631";
        else if (shortName.endsWith("S")) x = "632";
        else if (shortName.endsWith("T")) x = "633";
        else if (shortName.endsWith("W")) x = "634";
        else x = "0";
        
        id.setValue(x);        
        newOrganisation.setId(id);
        newOrganisation.setName(nolOrganisations.get(x));
             
        
        return newOrganisation;
    }

}

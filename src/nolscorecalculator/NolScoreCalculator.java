/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nolscorecalculator;

import IofXml30.java.ClassResult;
import IofXml30.java.EventList;
import IofXml30.java.Id;
import IofXml30.java.Organisation;
import IofXml30.java.PersonResult;
import IofXml30.java.ResultList;
import IofXml30.java.ResultStatus;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;



/**
 *
 * @author shep
 */
public class NolScoreCalculator {
    
    // TODO Easter NOT WORKING - funny setup using "All Days"
    
    public static final boolean DEV =true;

    public static final String CREATOR = "Sheptron Industries";
    public static final String EVENT_SELECTION_DIALOG_STRING = "Select all the NOL races from the list below...";
    
    public enum NolCategory {
        SeniorMen, SeniorWomen, JuniorMen, JuniorWomen;
        
        @Override
        public String toString() {
            switch (this) {
                case SeniorMen:
                    return "Senior Men";
                case SeniorWomen:
                    return "Senior Women";
                case JuniorMen:
                    return "Junior Men";
                case JuniorWomen:
                    return "Junior Women";
                default:
                    throw new IllegalArgumentException();
            }
        }
    };
 
    public enum NolTeamName {
        Arrows, Cockatoos, Cyclones, Foresters, Nomads, Nuggets, Stingers
    };
    
    // Map of Eventor ID to Team Names - hard coded, maybe not a good idea? Can we determine NOL teams from Eventor download only?
    public static Map<String, String> nolOrganisations = createNolOrganisationsMap();
    
    public static ArrayList<Entity>[] NOLSeasonTeams = createNolSeasonTeams();
    
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
            
//            /// TESTING
//            String filename = "NOL_Results.xml";
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

            String fromDate = "2016-01-01";
            String toDate = "2016-10-10";
            String classificationIds = "1,2"; // 1=Championship, 2=National

            //String eventorQuery = "events?fromDate=2016-12-01&toDate=2016-12-31";
            String eventorQuery = "events?fromDate=" + fromDate + "&toDate=" + toDate + "&classificationIds=" + classificationIds;
            String iofXmlType = "EventList";

            String xmlString = EventorInterface.getEventorData(eventorQuery, "From Date " + fromDate + " To Date " + toDate);

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
            //JOptionPane jp = new JOptionPane();

            int selection = JOptionPane.showConfirmDialog(null, new JScrollPane(list), EVENT_SELECTION_DIALOG_STRING, JOptionPane.OK_CANCEL_OPTION);

            if (selection == JOptionPane.CANCEL_OPTION) {
                // Code to use when CANCEL is PRESSED.
                // Exit? do it for now... prob need to give the user a warning.
                return;
            }

            indexOfSelectedEvents = list.getSelectedIndices();

            int numberOfEvents = indexOfSelectedEvents.length;

            // Create a List of NOL Athletes to store all our results in
            ArrayList<Entity> NOLSeasonResults = new ArrayList<>();
            
            // Create a List of all the NOL Races 
            ArrayList<Id> NOLSeasonEvents = new ArrayList<>();
            ArrayList<String> NOLSeasonEventString = new ArrayList<>();                        

            // Get Results (https://eventor.orientering.se/api/results/event/iofxml)
            // For each selected event
            for (int i = 0; i < numberOfEvents; i++) {
                Id eventId = eventList.getEvent().get(indexOfSelectedEvents[i]).getId();
                NOLSeasonEvents.add(eventId);
                NOLSeasonEventString.add(eventList.getEvent().get(indexOfSelectedEvents[i]).getName());
                
                // Get this result list from Eventor (to do - get only the relevant classes!)
                ResultList thisResultList;
                try {
                    thisResultList = EventorInterface.downloadResultList(eventList, indexOfSelectedEvents[i]);
                }
                catch (Exception e){
                    // Somethings gone wrong, nothing we can do!
                    int klj = 0;
                    continue;
                }
                    
                
                
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
                        
                        //ArrayList<Result> nolTeamResults = new ArrayList<>();
                        ArrayList<Result> nolTeamResults = createEmptyNolTeamResults(nolCategory, eventId);
                        
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
                            Entity nolAthlete = new Entity(personResult, nolCategory);
                            Result nolResult = new Result(personResult, eventId);

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
                            boolean isTeamResult = true;
                            Result thisNolTeamResult = new Result(eventId, personResult.getOrganisation(), nolCategory, isTeamResult);
                            
                            // Find the right team result and add this personResult to it
                            // TODO speedup : keep an index to lookup rather than loop through
                            for (Result res : nolTeamResults){
                                // Check Organisation Id Value
                                if(res.getOrganisation().getId().getValue().equals(personResult.getOrganisation().getId().getValue())) {                                    
                                    res.addIndividualResult(personResult); // Add result
                                    continue;   // No need to keep looking in nolTeamResults
                                }                                
                            }                                                  
                        }
                        
                        // This class is finished so now assign team points
                        // Sort this last lot of results
                        // TODO Relays - need to set isRelay boolen in team result!
                        Collections.sort(nolTeamResults, new NolTeamResultCompare());
                        
                        // Now they're sorted so add placings and calculate points (score)
                        int placing = 0;
                        for (Result nolTeamResult : nolTeamResults){
                            placing++; 
                            nolTeamResult.setPlacing(placing);
                            nolTeamResult.calculateScore();
                            
                            // Add these race results to teams
                            Entity thisNolTeam = new Entity(nolTeamResult.getOrganisation(), nolCategory);
                            int indexOfNolTeam = NOLSeasonTeams[nolCategory.ordinal()].indexOf(thisNolTeam);
                            
                            if (indexOfNolTeam == -1 || thisNolTeam.name.equals("No Team")){ 
                                continue;
                            } // NOT in a NOL team!
                            // Add this result 
                            
                            NOLSeasonTeams[nolCategory.ordinal()].get(indexOfNolTeam).addResult(nolTeamResult);                                                      
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
            for (Entity nolAthlete : NOLSeasonResults) {
                nolAthlete.updateTotalScore(numberOfRaceToCount);
            }

            // Now Sort the Results -Decreasing Total Score
            // We've got all NOL Categories mixed in here but that doesn't matter - we'll selectively write them out ...
            // TODO Make NOLSeasonResults be an array [] of ArrayLists, each element being for a category like NOLSeasonTeams
            Collections.sort(NOLSeasonResults, (Entity a1, Entity a2) -> a2.totalScore - a1.totalScore); // Sort Individual
            
            // Sort Team Results
            for (NolCategory nolCategory : NolCategory.values()){                
                Collections.sort(NOLSeasonTeams[nolCategory.ordinal()], (Entity e1, Entity e2) -> e2.getTotalScore() - e1.getTotalScore());                                            
            }
            
            // Create a List Mapping EventIds to NOL Race Numbers
            // TODO prompt the user for this using NOLSeasonEvents and NOLSeasonEventString
            Map<Integer, Id> nolRaceNumberToId = new HashMap<>();         
            int nolRaceNumber = 0;
            for (Id id : NOLSeasonEvents) {
                nolRaceNumber++;
                nolRaceNumberToId.put(nolRaceNumber, id);
            }            

            /////////////////////////////////////
            // Now publish
            
            // XML -> (XSLT) -> HTML            

            // Build Result lists for each Category
            // TODO simplify this build (use a loop somehow?)            
            ArrayList<Entity> juniorMenResults = new ArrayList<>();
            ArrayList<Entity> juniorWomenResults = new ArrayList<>();
            ArrayList<Entity> seniorMenResults = new ArrayList<>();
            ArrayList<Entity> seniorWomenResults = new ArrayList<>();
            for (Entity nolAthlete : NOLSeasonResults) {
                //NolCategory nolCategory = nolAthlete.getNolCategory();
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

            int numberOfNolCategories = NolCategory.values().length;
            ArrayList<Entity>[] resultsForPrinting = new ArrayList[numberOfNolCategories*2];  // Individual + Team Results
            resultsForPrinting[NolCategory.SeniorMen.ordinal()] = seniorMenResults;
            resultsForPrinting[NolCategory.SeniorWomen.ordinal()] = seniorWomenResults;
            resultsForPrinting[NolCategory.JuniorMen.ordinal()] = juniorMenResults;
            resultsForPrinting[NolCategory.JuniorWomen.ordinal()] = juniorWomenResults;
            
            // Add Team Results to resultsForPrinting
            for (NolCategory nolCategory : NolCategory.values()){
                resultsForPrinting[numberOfNolCategories+nolCategory.ordinal()] = NOLSeasonTeams[nolCategory.ordinal()];
            }
            
            // Get User input - where to save file?
            String outputDirectory = getOutputDirectory();
            
            ResultsPrinter resultsPrinter = new ResultsPrinter();
            resultsPrinter.allResultsToNolXml(resultsForPrinting, nolRaceNumberToId, outputDirectory);
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
        //myMap.put("0", "No Team");
        return myMap;
    }
    
    private static ArrayList<Entity>[] createNolSeasonTeams()
    {
        // This should create Senior and Junior Men and Women teams
        // for each of the State Teams defined in the nolOrganisations map
        ArrayList<Entity>[] nolSeasonTeams = new ArrayList[NolCategory.values().length];
        // Initialise!
        for (int i = 0; i<NolCategory.values().length; i++) nolSeasonTeams[i] = new ArrayList<>();
        
        Iterator it = nolOrganisations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            Id id = new Id();
            id.setValue((String) pair.getKey());
            Organisation organisation = new Organisation();
            organisation.setId(id);
            organisation.setName((String) pair.getValue());
            
            for (NolCategory nolCategory : NolCategory.values()){                            
                Entity nolTeam = new Entity(organisation, nolCategory);
                
                int test = nolCategory.ordinal();
                
                nolSeasonTeams[nolCategory.ordinal()].add(nolTeam);
            }            
        }        
        
        return nolSeasonTeams;
    }
    
    private static ArrayList<Result> createEmptyNolTeamResults(NolCategory nolCategory, Id eventId){
        
        ArrayList<Result> nolTeamResults = new ArrayList<>();
        
        for (Map.Entry pair : nolOrganisations.entrySet()) {
            
            // Create an empty Result for each team (organisation)
            Id id = new Id();
            id.setValue((String) pair.getKey());
            Organisation organisation = new Organisation();
            organisation.setId(id);
            organisation.setName((String) pair.getValue());
            boolean isTeamResult = true;
            
            Result result = new Result(eventId, organisation, nolCategory, isTeamResult);
            
            nolTeamResults.add(result);
        }    
                
        return nolTeamResults;        
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

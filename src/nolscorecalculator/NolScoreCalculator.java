/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nolscorecalculator;

import IofXml30.java.ClassResult;
import IofXml30.java.Event;
import IofXml30.java.EventForm;
import IofXml30.java.EventList;
import IofXml30.java.Id;
import IofXml30.java.Organisation;
import IofXml30.java.PersonResult;
import IofXml30.java.ResultList;
import IofXml30.java.ResultStatus;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import nolscorecalculator.Result.TeamResultType;
import org.xml.sax.SAXException;

/**
 *
 * @author shep
 */
public class NolScoreCalculator {
    
    // TODO Easter NOT WORKING - funny setup using "All Days"
    // TODO Juniors included in Senior results for Sprint Races (just dodgy this up)  
    // TODO mouse over race name on all scores
    // TODO add number of races counting to html output
    // TODO filename: add number of races etc
    
    public static final boolean DEV = false;

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
    
    private static final String[] VALID_ELITE_CLASSES = {"M21E", "W21E", "M17-20E", "M-20E", "M20E", "W17-20E", "W-20E", "W20E"};
    private static final String[] VALID_NONELITE_CLASSES = {"M21A", "W21A", "M20A", "W20A"};

    /**
     * @param args the command line arguments
     * @throws java.net.MalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException, IOException, JAXBException, SAXException, ParserConfigurationException {
        {         
            // TODO get dates to/from from user       
            //DateSelector dateSelector = new DateSelector(); 
            //String startDate = dateSelector.getStartDate();
            //int jkl = 0;
            
            
            // Testing                      
            /*
            Map<Integer, Event> num2Event = new HashMap<>();         
            int raceNumber = 0;
            String [] strings = new String[5];
            for (int i = 0; i<5; i++) {
                raceNumber++;
                Event event = new Event();
                String name = String.format("Event Number %d", raceNumber);
                event.setName(name);
                Id id = new Id();
                id.setValue(String.format("%d", raceNumber));
                event.setId(id);
                num2Event.put(raceNumber, event);
                strings[i] = name;
            } 
            
            Object[] options = {"Done", "Move Up", "Move Down"};
            JList  jlist = new JList(strings);
            jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            int s = -1;
            // Done = 0; Up = 1; Down = 2;
            while (s != 0) {
                // Do a resort according to the users last button press
                // We need to keep track of where things are at...
                s = JOptionPane.showOptionDialog(null, new JScrollPane(jlist), "Title", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
            }*/
            // END Testing
            
            String fromDate = "2017-03-01";//"2017-03-01";
            String toDate = "2017-03-30"; //2017-10-31";
            
            EventList eventList = EventorInterface.getEventList(fromDate, toDate);
            
            // TODO Sort by putting events with names including "NOL" up the top

            // Now sort which event we want (we want to get their EventId so we can download the results)
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
            ArrayList<String> NOLSeasonEventString = new ArrayList<>();  
            ArrayList<Event> NOLSeasonEventList = new ArrayList<>();

            // Get Results For each selected event
            for (int i = 0; i < numberOfEvents; i++) {
             
                //Id eventId = eventList.getEvent().get(indexOfSelectedEvents[i]).getId();                
                NOLSeasonEventString.add(eventList.getEvent().get(indexOfSelectedEvents[i]).getName());
                
                System.out.println(eventList.getEvent().get(indexOfSelectedEvents[i]).getName());
                
                // Get this result list from Eventor (to do - get only the relevant classes!)
                ResultList thisResultList;
                try {
                    thisResultList = EventorInterface.downloadResultList(eventList, indexOfSelectedEvents[i]);
                }
                catch (Exception e){
                    // Somethings gone wrong, nothing we can do! // TODO - try again in a little while??
                    continue;
                }
                
                // Keep a record of this event (want date of events to use for race numbers)
                NOLSeasonEventList.add(thisResultList.getEvent());
                Id eventId = thisResultList.getEvent().getId();
                
                // Find the right classes : this may be complicated if we have M/W21A instead of E
                // Use E, if there's no E then use A                
                boolean usingEliteClasses = isUsingEliteClasses(thisResultList);
                
                // Team Result type is important: Normally we use sum of individual race times, 
                // when we have A and B finals we use sum of individual scores
                TeamResultType teamResultType = TeamResultType.Normal;
                
                boolean usingAandBfinals = false;
                if (usingEliteClasses) usingAandBfinals = isUsingAandBfinals(thisResultList);
                    
                // Merge Results for A and B finals if they exist (actually append B final to A final)
                if (usingAandBfinals) {
                    thisResultList = mergeAandBfinals(thisResultList);
                    teamResultType = TeamResultType.AandBfinal;
                }

                // Run through again and process results
                for (ClassResult classResult : thisResultList.getClassResult()) {
                    String className = classResult.getClazz().getName();

                    if (isValidClass(className, usingEliteClasses)) {
                        // Assign Points
                        
                        NolCategory nolCategory = getNolCategory(className);
                                                
                        ArrayList<Result> nolTeamResults = createEmptyNolTeamResults(nolCategory, eventId, teamResultType);
                        
                        for (PersonResult personResult : classResult.getPersonResult()) {

                            // Individual
                            // To deal with runners that compete in Junior and Senior we 
                            // split an athlete into a Junior and a Senior version...
                            // Ignore someone if they didn't start - otherwise non-OK status will get ZERO points
                            if (personResult.getResult().get(0).getStatus() == ResultStatus.DID_NOT_START) {
                                continue;
                            }
                            if (personResult.getResult().get(0).getStatus() == ResultStatus.INACTIVE) {
                                continue;
                            }
                            
                            if (DEV) {
                                // DEV ONLY - translate club into state team (2016 had no NOL teams in Eventor)
                                // Note some races - eg Melbourne Sprint races will fail here... no N/A/W/Q etc appended at end of club names!
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
                            if (personResult.getOrganisation() == null || personResult.getOrganisation().getId() == null){
                                // This person isn't in a team
                                continue;
                            }
                                                       
                            // Find the right team result and add this personResult to it
                            // TODO speedup : keep an index to lookup rather than loop through
                            for (Result res : nolTeamResults){
                                // Check Organisation Id Value                                
                                if(res.getOrganisation().getId().getValue().equals(personResult.getOrganisation().getId().getValue())) {  
                                    
                                    switch(teamResultType) {
                                        case Normal:
                                            res.addIndividualResult(personResult); // Add result
                                            break;
                                        case AandBfinal:
                                            res.addIndividualResult(nolResult);
                                    }
                                    continue;   // No need to keep looking in nolTeamResults
                                }                                
                            }                                                  
                        }
                        // nsw 14, qld 17, sa 22, tas 30, wa 7, vic 33, act 65
                        
                        // This class is finished so now assign team points
                        // Sort this last lot of results
                        // TODO Relays - need to set isRelay boolen in team result!                         
                        switch (teamResultType){
                            case Normal :
                                Collections.sort(nolTeamResults, new NolTeamResultCompare());
                                break;
                                      
                            case AandBfinal :
                                Collections.sort(nolTeamResults, new NolTeamResultAandBfinalsCompare());
                                            
                        }
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
            
            // Sort events by date
            Collections.sort(NOLSeasonEventList, new EventCompare());

            // Calculate the number of individual events in the Season
            int numberOfIndividualEvents = 0;
            for (Event event : NOLSeasonEventList){
                if (event.getForm().isEmpty()){
                    // TODO Prompt the user whether this was an individual result
                }
                else if (event.getForm().get(0) == EventForm.INDIVIDUAL){
                    numberOfIndividualEvents += 1;
                }
            }
            
            // Calculate Total Individual Scores            
            for (Entity nolAthlete : NOLSeasonResults) {
                nolAthlete.updateTotalScore(numberOfIndividualEvents);
            }

            // Now Sort the Results -Decreasing Total Score
            // We've got all NOL Categories mixed in here but that doesn't matter - we'll selectively write them out ...
            // TODO Make NOLSeasonResults be an array [] of ArrayLists, each element being for a category like NOLSeasonTeams
            Collections.sort(NOLSeasonResults, (Entity a1, Entity a2) -> a2.totalScore - a1.totalScore); // Sort Individual                   
            
            // Calculate Total Team Scores 
            for (NolCategory nolCategory : NolCategory.values()){
                for (Entity nolTeam : NOLSeasonTeams[nolCategory.ordinal()]){
                    nolTeam.updateTotalScore(numberOfEvents);
                }
            }
            
            // Sort Team Results - we need to know which is the most recent event for this
            Id mostRecentEventId = NOLSeasonEventList.get(NOLSeasonEventList.size()-1).getId();
            for (NolCategory nolCategory : NolCategory.values()){                
                //Collections.sort(NOLSeasonTeams[nolCategory.ordinal()], (Entity e1, Entity e2) -> e2.getTotalScore() - e1.getTotalScore());
                Collections.sort(NOLSeasonTeams[nolCategory.ordinal()], (Entity e1, Entity e2) -> e2.getSortableTeamScore(mostRecentEventId) - e1.getSortableTeamScore(mostRecentEventId));
            }
            
            // Create a List Mapping EventIds to NOL Race Numbers - sort Events by date/time
            // TODO prompt the user to check/correct this using NOLSeasonEvents and NOLSeasonEventString
            // maybe use a JOptionPane so it's modal
            // Prompt the user to select races that need their number changed?
            //Object[] options = {"Done", "Move Up", "Move Down"};            
            //selection = JOptionPane.showConfirmDialog(null, new JScrollPane(list), EVENT_SELECTION_DIALOG_STRING, JOptionPane.YES_NO_CANCEL_OPTION);
            
            Map<Integer, Event> nolRaceNumberToEvent = new HashMap<>();         
            int nolRaceNumber = 0;
            for (Event event : NOLSeasonEventList) {
                nolRaceNumber++;
                nolRaceNumberToEvent.put(nolRaceNumber, event);
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
            // // TODO out of bounds exception here when empty
            resultsPrinter.allResultsToNolXml(resultsForPrinting, nolRaceNumberToEvent, outputDirectory);
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
    
    private static boolean isUsingAandBfinals(ResultList resultList) {

        // Decide if there are Elite A and B classes
        boolean usingEAclasses = false;
        boolean usingEBclasses = false;

        // Run through and decide if we have Elite (E) or A classes
        for (int j = 0; j < resultList.getClassResult().size(); j++) {
            String className = resultList.getClassResult().get(j).getClazz().getName();
            if (className.contains("M21EA") || className.contains("W21EA")) {
                usingEAclasses = true;
            }
            if (className.contains("M21EB") || className.contains("W21EB")) {
                usingEBclasses = true;
            }
        }

        return (usingEAclasses && usingEBclasses);
    }
    
    private static ResultList mergeAandBfinals(ResultList thisResultList){
                
        boolean usingEliteClasses = isUsingEliteClasses(thisResultList);
        
        // TODO - this could be done more cleverly
        // Grab the numbers in the A Finals
        
        // Use a Map to keep track of how many runners in each classes A Final
        Map<NolCategory,Integer> sizeOfAfinals = new HashMap<>();               
        for (NolCategory nolCategory : NolCategory.values()){
            sizeOfAfinals.put(nolCategory, 0);
        }
        
        // Use a Map to keep track of where the A finals are in the result list
        Map<NolCategory,Integer> locationOfAfinals = new HashMap<>();
        
        for (ClassResult classResult : thisResultList.getClassResult()){
            
            String className = classResult.getClazz().getName();
            
            if (isValidClass(className, usingEliteClasses)) {
                
                NolCategory nolCategory = getNolCategory(className);
                if (className.contains("A")){
                    // Assuming Only A finals have an "A" in the class name!
                    int numberOfRunnersInThisFinal = classResult.getPersonResult().size();
                    sizeOfAfinals.put(nolCategory, numberOfRunnersInThisFinal);                               
                }
                
            }                      
        }
        
        // No just go through the B finals and add the number of A final runners to each RersonResult placing
        for (ClassResult classResult : thisResultList.getClassResult()){
            
            String className = classResult.getClazz().getName();
            
            if (isValidClass(className, usingEliteClasses)) {
                
                NolCategory nolCategory = getNolCategory(className);
                if (className.contains("B")){
                    // Assuming Only A finals have an "A" in the class name!
                    int numberOfRunnersInThisFinal = sizeOfAfinals.get(nolCategory);
                    
                    for (PersonResult personResult : classResult.getPersonResult()){
                        
                        if (personResult.getResult().get(0).getPosition() != null){
                            int oldPosition = personResult.getResult().get(0).getPosition().intValue();
                            int newPosition = oldPosition + numberOfRunnersInThisFinal;
                            
                            personResult.getResult().get(0).setPosition(BigInteger.valueOf(newPosition));
                        }
                    }
                 
                    // Now find Corresponding A Final and add this class to it
                    for (ClassResult anotherClassResult : thisResultList.getClassResult()) {
                        String anotherClassName = anotherClassResult.getClazz().getName();
                        NolCategory anotherNolCategory = getNolCategory(anotherClassName);
                        if (isValidClass(anotherClassName, usingEliteClasses) && anotherNolCategory.equals(nolCategory) && anotherClassName.contains("A")) {
                            // stick className on the end of anotherClassName
                            int k = 0;
                            
                            List<PersonResult> anotherListPersonResults = anotherClassResult.getPersonResult();
                            for (PersonResult personResult : classResult.getPersonResult()){
                                anotherListPersonResults.add(personResult);
                            }                            
                        }
                    }
                    
                    // change the name of the B class so we don't pick it up later (TODO can we remove it safely?)
                    classResult.getClazz().setName("DO NOT USE");
                }                                                
            }                      
        }
        
        return thisResultList;
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
    
    private static ArrayList<Result> createEmptyNolTeamResults(NolCategory nolCategory, Id eventId, TeamResultType teamResultType){
        
        ArrayList<Result> nolTeamResults = new ArrayList<>();
        
        for (Map.Entry pair : nolOrganisations.entrySet()) {
            
            // Create an empty Result for each team (organisation)
            Id id = new Id();
            id.setValue((String) pair.getKey());
            Organisation organisation = new Organisation();
            organisation.setId(id);
            organisation.setName((String) pair.getValue());
            boolean isTeamResult = true;
            
            Result result = new Result(eventId, organisation, nolCategory, teamResultType);
            
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

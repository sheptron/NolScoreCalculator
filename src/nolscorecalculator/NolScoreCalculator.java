/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.ClassResult;
import IofXml30.java.Event;
import IofXml30.java.EventForm;
import IofXml30.java.Id;
import IofXml30.java.Organisation;
import IofXml30.java.PersonResult;
import IofXml30.java.ResultList;
import IofXml30.java.ResultStatus;
import IofXml30.java.TeamResult;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import nolscorecalculator.Result.TeamResultType;

/**
 *
 * @author shep
 */
public class NolScoreCalculator {

    // TODO Once a team member always a team member (if someone enters a race as eg MFR, but some races as VIC, then make them always VIC)
    // TODO Juniors included in Senior results for Sprint Races (just dodgy this up)        
    // TODO handle classes being voided or cancelled
    // TODO user select date range
    // TODO Individual point scores don't display club if not NOL team
    // TODO we need a way to disqualify runners who didn't wear their NOL team uniform
    
    public static final boolean DEV = false;
    
    public static boolean USE_STRICT_COMPETITOR_MATCHING = true;   // Use Eventor ID to match athletes

    public static final String CREATOR = "Sheptron Industries";
    public static final String EVENT_SELECTION_DIALOG_STRING = "Select all the NOL races from the list below...";

    public enum NolCategory {
        SeniorMen, SeniorWomen, JuniorMen, JuniorWomen, SeniorMixed, JuniorMixed;

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
                case SeniorMixed:
                    return "Senior Mixed";
                case JuniorMixed:
                    return "Junior Mixed";
                default:
                    throw new IllegalArgumentException();
            }
        }
    };

    public enum NolAgeCategory {
        Senior, Junior;
    }
    
    public static final int NUMBER_MIXED_CATEGORIES = 2;

    public enum NolTeamName {
        Arrows, Cockatoos, Cyclones, Foresters, Nomads, Nuggets, Stingers
    };

    // Map of Eventor ID to Team Names - hard coded, maybe not a good idea? 
    // TODO Can we determine NOL teams from Eventor download only?
    public static Map<String, String> nolOrganisations = createNolOrganisationsMap();
    
    public static Map<String, Integer> nolTeamResultsIndexes = new HashMap<>();

    public static ArrayList<Entity>[] NOLSeasonTeams = createNolSeasonTeams();

    // Comprehensive list of all possible age categories we're going to count in the NOL
    private static final String[] VALID_ELITE_CLASSES = {"M21E", "Men 21 Elite", "Men 21E",  "W21E", "Women 21 Elite", "Women 21E", "Mixed Elite Relay"};
    private static final String[] VALID_JUNIOR_ELITE_CLASSES = {"M17-20E", "M-20E", "Men 20 Elite", "Men 20E", "M20E", "W17-20E", "W-20E", "W20E", "Women 20 Elite", "Women 20E", "Mixed Junior Elite Relay"};
    // TODO can we try and work out which are the elite classes? Seems event organisers keep finding a different way to same the same thing... 
    private static final String[] VALID_NONELITE_CLASSES = {"M21A", "W21A"};
    private static final String[] VALID_JUNIOR_NONELITE_CLASSES = {"M20A", "W20A", "M18A", "W18A"};

    public static void main(String[] args)  {
        { //throws MalformedURLException, IOException, JAXBException, SAXException, ParserConfigurationException
                      
            final NolProgressBar progressBar = new NolProgressBar();

            JFrame frame = new JFrame("NOL Score Calculator Progress");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(progressBar);
            frame.pack();
            frame.setVisible(true);
            
            // Get date (year) from user (TODO -get month/day as well)
            int thisYearInt = Year.now().getValue();
            int numberOfYearsOption = 5;
            String[] choices = new String[numberOfYearsOption];
            for (int ii=0; ii<numberOfYearsOption; ii++){
                choices[ii] = String.format("%d",thisYearInt-ii);
            }
            String input = (String) JOptionPane.showInputDialog(null, "Select year...", "NOL Season Selection", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
            
            if(input==null) {
                System.exit(0);
                return;
            }        
            
            String fromDate = input + "-01-01";//"2017-03-01";
            String toDate = input + "-12-31"; //2017-10-31";
            
            String thisYear = fromDate.substring(0, 4);
            
            // The last (individual) race of the season scores (3) extra points            
            Event lastIndividualEvent = new Event();
            
            //JDatePicker jDatePicker = new JDatePicker();

            progressBar.updateBar(0, "Downloading events list from Eventor\n");
            EventorApi.EventList eventList = EventorInterface.getEventList(fromDate, toDate);
            
            // Sort by putting events with names including "NOL" (etc) up the top (and MTBO at the bottom)
            Collections.sort(eventList.getEvent(), new NolEventCompare());
           
            // Now sort which event we want (we want to get their EventId so we can download the results)
            int[] indexOfSelectedEvents;
            int numberOfDownloadedEvents = eventList.getEvent().size();
            String eventsInDateRange[] = new String[numberOfDownloadedEvents];
            for (int i = 0; i < numberOfDownloadedEvents; i++) {
                eventsInDateRange[i] = eventList.getEvent().get(i).getName().getContent();
            }

            JList list = new JList(eventsInDateRange);
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            int selection = JOptionPane.showConfirmDialog(null, new JScrollPane(list), EVENT_SELECTION_DIALOG_STRING, JOptionPane.OK_CANCEL_OPTION);

            if (selection == JOptionPane.CANCEL_OPTION) {
                // Code to use when CANCEL is PRESSED.
                // Exit? do it for now... prob need to give the user a warning.
                return;
            }

            indexOfSelectedEvents = list.getSelectedIndices();
            
            int numberOfEvents = indexOfSelectedEvents.length;
            int numberOfRaces = 0;    // Thise is for where an event has multiple races                       
            
            // Create a List of NOL Athletes to store all our results in
            ArrayList<Entity> NOLSeasonResults = new ArrayList<>();

            // Create a List of all the NOL Races                         
            ArrayList<Event> NOLSeasonEventList = new ArrayList<>();
            
            progressBar.updateBar(0, "Downloading Results for...\n");

            // Get Results For each selected event
            for (int i = 0; i < numberOfEvents; i++) {

                EventorApi.Event event = eventList.getEvent().get(indexOfSelectedEvents[i]);
                String eventIdString = event.getEventId().getContent(); //.getEventorId().getValue();

                System.out.println(event.getName().getContent());
                progressBar.updateBar((int)Math.round(100.0*(double)(i+1)/(double)numberOfEvents), event.getName().getContent() + "\n");

                // Get this result list from Eventor (to do - get only the relevant classes!)
                ResultList thisResultList;
                ArrayList<String> eventRaceIds;
                try {
                    thisResultList = EventorInterface.downloadResultList(eventList, indexOfSelectedEvents[i]);
                    eventRaceIds = EventorInterface.downloadListOfEventRaceIds(eventIdString);
                } catch (Exception e) {
                    // Somethings gone wrong, nothing we can do! We've already given the user a warning...
                    // TODO - try again in a little while?? shuffle this one back into the list??
                    continue;
                }            
                
                // RELAYS
                boolean isRelay = thisResultList.getEvent().getForm().get(0).equals(EventForm.RELAY); 
                boolean isMixedRelay = false;
                              
                // Trim the Result List (get rid of non-NOL classes) - just to make things a bit quicker
                ArrayList<ClassResultExtended> resultList = trimResultList(thisResultList, isRelay);
                // TODO trimResults needs to remove B finals for juniors
                if (isRelay) isMixedRelay = getIsMixedRelay(resultList);

                // There may be more than one race
                int numberOfRacesInThisEvent = thisResultList.getEvent().getRace().size();

                for (int raceNumber = 1; raceNumber <= numberOfRacesInThisEvent; raceNumber++) { 
                    
                    numberOfRaces += 1;
                    
                    Event thisEvent = generateEventStage(thisResultList.getEvent(), eventRaceIds, raceNumber, numberOfRacesInThisEvent);

                    Id eventId = thisEvent.getId();
                    
                    NOLSeasonEventList.add(thisEvent);                                                          
                    
                    // Go Through Each Class And Process Results
                    for (ClassResultExtended classResult : resultList) {
                        String className = classResult.getClazz().getName();
                        
                        TeamResultType teamResultType = classResult.getTeamResultType();                                                

                        /* 
                        Decide here which method we should use to calculate team result
                        It may be different for different classes
                         */
                        // Assign Points
                        NolCategory nolCategory = getNolCategory(className);

                        ArrayList<Result> nolTeamResults = createEmptyNolTeamResults(nolCategory, eventId, teamResultType);

                        if (isRelay) {

                            // Organisers don't always use this field so try it but we have a backup option...
                            int numberOfLegs = classResult.getClazz().getMinNumberOfTeamMembers().intValue();
                            if (numberOfLegs < 2){ // Guess that means the above failed
                                numberOfLegs = Result.getNumberOfRelayLegs(classResult);
                            }
                            
                            for (TeamResult teamResult : classResult.getTeamResult()) {
                                
                                if (thisEvent.getId().getValue().equals("6392")) {
                                    /* 
                                    TODO organisers seem to use (eg) "ACT 1" instead of NOL team names, 
                                    Prob best to try for NOL team names and if there are none then try this approach...
                                    */                                
                                    Organisation organisation = testingOnlyTranslateOrganisationId(teamResult);
                                    teamResult.getOrganisation().clear();
                                    teamResult.getOrganisation().add(organisation);  
                                }

                                // Hack for another eventor fuckup                               
                                if (thisEvent.getId().getValue().equals("6783") && teamResult.getOrganisation().isEmpty()) {
                                    // Eventor have fucked the results for
                                    // "Official results for Melbourne Sprint Weekend Race 3, Sprint Relay - NOL Managers only for relay team selections"
                                    // So we need to fudge it
                                    Organisation organisation = testingOnlyTranslateOrganisationId(teamResult);
                                    teamResult.getOrganisation().add(organisation);  
                                }
                                //
                                
                                // NOL Team                               
                                if (teamResult.getOrganisation() == null || teamResult.getOrganisation().isEmpty() || teamResult.getOrganisation().get(0).getId() == null 
                                        || nolTeamResultsIndexes.get(teamResult.getOrganisation().get(0).getId().getValue()) == null) {
                                    // This team isn't a NOL team   
                                    continue;
                                }
                                
                                // Find the right team result and add this teamResult to it (if a team result already exists it will only replace it if the time is faster)
                                int index = nolTeamResultsIndexes.get(teamResult.getOrganisation().get(0).getId().getValue());
                                nolTeamResults.get(index).addRelayResult(teamResult, numberOfLegs);                                
                            }
                            
                        } 
                        else {
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
                                if (personResult.getResult().get(0).getRaceNumber().intValue() != raceNumber) {
                                    continue;
                                }
                               
                                // Create NOL Athlete and Result from the IOF PersonResult
                                Entity nolAthlete = new Entity(personResult, nolCategory);
                                Result nolResult = new Result(personResult, eventId);
                                
                                // Hack to fix membership issues (hard coded)
                                //nolAthlete = NolTeamCorrection.fixNolTeamMembership(nolAthlete);

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
                                if (personResult.getOrganisation() == null || personResult.getOrganisation().getId() == null 
                                        || nolTeamResultsIndexes.get(personResult.getOrganisation().getId().getValue()) == null) {
                                    // This person isn't in a NOL team                                    
                                    continue;
                                }
                                
                                // HACK FOR BELINDA LAWFORD in 2018 QLD WEEKEND
                                // 6045 and 6046
                                if (eventId.getValue().equals("6045") || eventId.getValue().equals("6046")){
                                    if (nolAthlete.name.toLowerCase().contains("belinda lawford")){
                                        continue;
                                    }                                                                    
                                }
                                
                                // HACK FOR LIIS JOHANSON in 2018 AUS SPRINT CHAMPIONSHIPS
                                //if (eventId.getValue().equals("6390")){
                                //    if (nolAthlete.name.toLowerCase().contains("liis johanson")){
                                //        continue;
                                //    }                                                                    
                                //}
                                

                                // Find the right team result and add this personResult to it                                
                                int index = nolTeamResultsIndexes.get(personResult.getOrganisation().getId().getValue());
                                switch (teamResultType) {
                                    case RaceTimes:
                                        nolTeamResults.get(index).addIndividualResult(personResult); // Add result
                                        break;
                                    case Placings:
                                        nolTeamResults.get(index).addIndividualResult(personResult, teamResultType);
                                        break;
                                    case NolScores:
                                        nolTeamResults.get(index).addIndividualResult(nolResult);                                        
                                }                            
                            }
                        }

                        // This class is finished so now assign team points
                        // Sort this last lot of results                                               
                        switch (teamResultType) {
                            case NolScores:
                                Collections.sort(nolTeamResults, new NolTeamResultAandBfinalsCompare());
                                break;

                            default:
                                Collections.sort(nolTeamResults, new NolTeamResultCompare());
                        }
                        // Now they're sorted so add placings and calculate points (score)
                        int placing = 0;
                        for (Result nolTeamResult : nolTeamResults) {
                            placing++;
                            nolTeamResult.setPlacing(placing);
                            nolTeamResult.calculateScore();

                            // Add these race results to teams
                            if (isRelay && isMixedRelay) {
                                // Copy Mixed results into Men and Women
                                NolCategory[] nolCategories = new NolCategory[2];
                                switch (nolCategory) {
                                    case SeniorMixed:
                                        nolCategories[0] = NolCategory.SeniorMen;
                                        nolCategories[1] = NolCategory.SeniorWomen;
                                        break;
                                    case JuniorMixed:
                                        nolCategories[0] = NolCategory.JuniorMen;
                                        nolCategories[1] = NolCategory.JuniorWomen;
                                }

                                for (NolCategory category : nolCategories) {
                                    Entity thisNolTeam = new Entity(nolTeamResult.getOrganisation(), category);
                                    int indexOfNolTeam = NOLSeasonTeams[category.ordinal()].indexOf(thisNolTeam);

                                    if (indexOfNolTeam == -1 || thisNolTeam.name.equals("No Team")) {
                                        continue;
                                    } // NOT in a NOL team!
                                    // Add this result 

                                    NOLSeasonTeams[category.ordinal()].get(indexOfNolTeam).addResult(nolTeamResult);
                                }
                            } 
                            else {
                                Entity thisNolTeam = new Entity(nolTeamResult.getOrganisation(), nolCategory);
                                int indexOfNolTeam = NOLSeasonTeams[nolCategory.ordinal()].indexOf(thisNolTeam);

                                if (indexOfNolTeam == -1 || thisNolTeam.name.equals("No Team")) {
                                    continue;
                                } // NOT in a NOL team!
                                // Add this result 

                                NOLSeasonTeams[nolCategory.ordinal()].get(indexOfNolTeam).addResult(nolTeamResult);
                            }
                        }                        
                    }
                }
            }

            // Sort events by date
            Collections.sort(NOLSeasonEventList, new EventCompare());

            // Calculate the number of individual events in the Season
            int numberOfIndividualEvents = 0;
            for (Event event : NOLSeasonEventList) {
                if (event.getForm().isEmpty()) {
                    // TODO Prompt the user whether this was an individual result
                } else if (event.getForm().get(0) == EventForm.INDIVIDUAL) {
                    numberOfIndividualEvents += 1;
                    lastIndividualEvent = event;
                }
            }
            
            // Check with User if this is the final individual race of the season
            boolean lastIndividualRaceOfSeason = promptUserConfirmLastRaceOfSeason(lastIndividualEvent);

            // Calculate Total Individual Scores            
            for (Entity nolAthlete : NOLSeasonResults) {
             
                // First update score from final race of season (extra points)
                if (lastIndividualRaceOfSeason) {                    
                        nolAthlete.setEventResultToFinalForSeason(lastIndividualEvent);
                }

                // Now update this athletes total score for the season
                nolAthlete.updateTotalScore(numberOfIndividualEvents);
            }

            // Now Sort the Results -Decreasing Total Score
            // We've got all NOL Categories mixed in here but that doesn't matter - we'll selectively write them out ...
            // TODO Make NOLSeasonResults be an array [] of ArrayLists, each element being for a category like NOLSeasonTeams
            Collections.sort(NOLSeasonResults, (Entity a1, Entity a2) -> a2.totalScore - a1.totalScore); // Sort Individual                   

            // Calculate Total Team Scores 
            for (NolCategory nolCategory : NolCategory.values()) {
                for (Entity nolTeam : NOLSeasonTeams[nolCategory.ordinal()]) {
                    nolTeam.updateTotalScore(numberOfRaces);
                }
            }

            // Sort Team Results - we need to know which is the most recent event for this
            Id mostRecentEventId = NOLSeasonEventList.get(NOLSeasonEventList.size() - 1).getId();
            for (NolCategory nolCategory : NolCategory.values()) {
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
            progressBar.updateBar(100, "Building NOL scores HTML file" + "\n");
            // XML -> (XSLT) -> HTML            
            // Build Result lists for each Category
            // TODO simplify this build (use a loop somehow?)            
            ArrayList<Entity> juniorMenResults = new ArrayList<>();
            ArrayList<Entity> juniorWomenResults = new ArrayList<>();
            ArrayList<Entity> seniorMenResults = new ArrayList<>();
            ArrayList<Entity> seniorWomenResults = new ArrayList<>();
            for (Entity nolAthlete : NOLSeasonResults) {                
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

            int numberOfNolCategories = NolCategory.values().length - NUMBER_MIXED_CATEGORIES; // We don't count the two mixed categories in here
            ArrayList<Entity>[] resultsForPrinting = new ArrayList[numberOfNolCategories * 2];  // Individual + Team Results
            resultsForPrinting[NolCategory.SeniorMen.ordinal()] = seniorMenResults;
            resultsForPrinting[NolCategory.SeniorWomen.ordinal()] = seniorWomenResults;
            resultsForPrinting[NolCategory.JuniorMen.ordinal()] = juniorMenResults;
            resultsForPrinting[NolCategory.JuniorWomen.ordinal()] = juniorWomenResults;

            // Add Team Results to resultsForPrinting
            for (NolCategory nolCategory : NolCategory.values()) {
                
                if (nolCategory == NolCategory.SeniorMixed || nolCategory == NolCategory.JuniorMixed) continue;
                
                resultsForPrinting[numberOfNolCategories + nolCategory.ordinal()] = NOLSeasonTeams[nolCategory.ordinal()];
            }
            
            progressBar.updateBar(100, "Writing NOL scores HTML file" + "\n");

            // Get User input - where to save file?
            String outputDirectory = getOutputDirectory();
            
            progressBar.updateBar(100, String.format("Save location is %s", outputDirectory) + "\n");

            ResultsPrinter resultsPrinter = new ResultsPrinter();

                // TODO out of bounds exception here when empty
                try { 
                    resultsPrinter.allResultsToNolXml(resultsForPrinting, nolRaceNumberToEvent, outputDirectory, thisYear);          
                }
                catch(JAXBException | IOException | TransformerException e){
                    // Notify The User Something has Gone Wrong
                    JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);                    
                }
                
            progressBar.updateBar(100, "Done..." + "\n");
            progressBar.updateBar(100, "You can close now this window." + "\n");
        }        
    }
    
    private static Event generateEventStage(Event mainEvent, ArrayList<String> eventRaceIds, int raceNumber, int numberOfRacesInThisEvent) {
        // When we have multiple events (multiday race) use this method to split the 
        // datas from Eventor into separate events
        
        Id eventId = new Id();
        eventId.setValue(eventRaceIds.get(raceNumber - 1));
        eventId.setType(mainEvent.getId().getType());

        //Event mainEvent = thisResultList.getEvent();
        Event newEvent = new Event();
        if (numberOfRacesInThisEvent > 1) {
            newEvent.setName(mainEvent.getName() + " Race " + raceNumber);
        } else {
            newEvent.setName(mainEvent.getName());
        }
        if (!mainEvent.getForm().isEmpty()) {
            newEvent.setForm(mainEvent.getForm());
        }
        newEvent.setStartTime(mainEvent.getRace().get(raceNumber - 1).getStartTime());
        newEvent.setId(eventId);
        //
        
        return newEvent;
    }
    
    private static boolean promptUserConfirmLastRaceOfSeason(Event lastIndividualEvent) {
        
        String lastIndEventPromptString = "Is " + lastIndividualEvent.getName() + " the last individual race of the season?";
        int lastIndEventSelection = JOptionPane.showConfirmDialog(null, lastIndEventPromptString, "Important!", JOptionPane.YES_NO_OPTION);
        return lastIndEventSelection == JOptionPane.YES_OPTION;
    }

    private static List<ClassResultExtended> getClassesForAgeCategory(ResultList resultList, NolAgeCategory nolAgeCategory) {

        String[] validEliteClasses;
        String[] validNonEliteClasses;

        switch (nolAgeCategory) {
            case Senior:
                validEliteClasses = VALID_ELITE_CLASSES;
                validNonEliteClasses = VALID_NONELITE_CLASSES;
                break;
            default:
                validEliteClasses = VALID_JUNIOR_ELITE_CLASSES;
                validNonEliteClasses = VALID_JUNIOR_NONELITE_CLASSES;
        }

        ArrayList<ClassResultExtended> classResults;

        // Run through and decide if we have Elite (E) classes
        classResults = lookForClasses(resultList, validEliteClasses, nolAgeCategory);

        // Return now as we're not interested in non-elite classes if there was elite classes
        if (!classResults.isEmpty()) {

            // Before we return - look for A and B finals and merge if necessary            
            boolean usingAandBfinals = isUsingAandBfinals(classResults);

            // Merge Results for A and B finals if they exist (actually append B final to A final)
            if (usingAandBfinals) {
                classResults = mergeAandBfinals(classResults);
                // Set Team resut type here too
                for (ClassResultExtended classResult : classResults) {
                    classResult.setTeamResultType(TeamResultType.Placings);
                }
            }            

            return classResults;
        }

        // If classResults is still empty then look for A Classes
        classResults = lookForClasses(resultList, validNonEliteClasses, nolAgeCategory);

        // For Juniors we may need to merge 18 and 20
        classResults = mergeJuniorClasses(classResults);

        // TODO what if its still empty?    
        return classResults;
    }

    private static ArrayList<ClassResultExtended> mergeJuniorClasses(ArrayList<ClassResultExtended> classResults) {
        // Merge the M/W18 and M/W20 classes        

        // Find where the Men and Womens classes are
        ArrayList<Integer> juniorMen = indexOfAllInNolCategory(NolCategory.JuniorMen, classResults);
        ArrayList<Integer> juniorWomen = indexOfAllInNolCategory(NolCategory.JuniorWomen, classResults);

        if (juniorMen.size() == 1 && juniorWomen.size() == 1) {
            return classResults;
        }
        // TODO could we ever have M18 and M20 but only W20 and no W18? probably not but best be able to handle it

        // If we're still here - merge them
        // Just Use the first element in juniorMen/Women and then add the rest of the classes,
        // then rename the first elements        
        // Men
        ClassResultExtended juniorMenClassResult = classResults.get(juniorMen.get(0));
        juniorMenClassResult.setTeamResultType(TeamResultType.Placings);
        for (int ii = 1; ii < juniorMen.size(); ii++) {
            ClassResultExtended nextClassResult = classResults.get(juniorMen.get(ii));
            juniorMenClassResult.getPersonResult().addAll(nextClassResult.getPersonResult());
        }
        juniorMenClassResult = adjustPlacingsForMergedJuniorClasses(juniorMenClassResult);

        // Women
        ClassResultExtended juniorWomenClassResult = classResults.get(juniorWomen.get(0));
        juniorWomenClassResult.setTeamResultType(TeamResultType.Placings);
        for (int ii = 1; ii < juniorWomen.size(); ii++) {
            ClassResultExtended nextClassResult = classResults.get(juniorWomen.get(ii));
            juniorWomenClassResult.getPersonResult().addAll(nextClassResult.getPersonResult());
        }
        juniorWomenClassResult = adjustPlacingsForMergedJuniorClasses(juniorWomenClassResult);

        ArrayList<ClassResultExtended> mergedClasses = new ArrayList<>();
        mergedClasses.add(juniorMenClassResult);
        mergedClasses.add(juniorWomenClassResult);

        return mergedClasses;
    }

    static ClassResultExtended adjustPlacingsForMergedJuniorClasses(ClassResultExtended classResult) {
        // We need to adjust placings as per OA rules: winners of 18 and 20 are equal 1st, 2nd places become equal 3rd, etc
        for (PersonResult personResult : classResult.getPersonResult()) {
            if (!personResult.getResult().isEmpty()) {
                if (personResult.getResult().get(0).getPosition() != null) {
                    int originalPosition = personResult.getResult().get(0).getPosition().intValue();
                    int updatedPosition = originalPosition * 2 - 1;
                    personResult.getResult().get(0).setPosition(BigInteger.valueOf(updatedPosition));
                }
            }
        }

        return classResult;
    }

    static ArrayList<Integer> indexOfAllInNolCategory(NolCategory nolCategory, ArrayList<ClassResultExtended> list) {

        ArrayList<Integer> indexList = new ArrayList<>();

        for (int ii = 0; ii < list.size(); ii++) {
            if (list.get(ii).getNolCategory().equals(nolCategory)) {
                indexList.add(ii);
            }
        }
        return indexList;
    }

    private static ArrayList<ClassResultExtended> lookForClasses(ResultList resultList, String[] classesToLookFor, NolAgeCategory nolAgeCategory) {

        ArrayList<ClassResultExtended> selectedClasses = new ArrayList<>();

        // Run through and decide if we have any of the classes in the list
        for (ClassResult classResult : resultList.getClassResult()) {

            String className = classResult.getClazz().getName();

            for (String string : classesToLookFor) {

                if (className.contains(string)) {

                    // This is a class we're interested in 
                    ClassResultExtended newClassResult = new ClassResultExtended(classResult);
                    //newClassResult = (ClassResultExtended) classResult;
                    if (className.contains("M")) {
                        switch (nolAgeCategory) {
                            case Junior:
                                newClassResult.setNolCategory(NolCategory.JuniorMen);
                                break;
                            default:
                                newClassResult.setNolCategory(NolCategory.SeniorMen);
                        }
                    } else {
                        switch (nolAgeCategory) {
                            case Junior:
                                newClassResult.setNolCategory(NolCategory.JuniorWomen);
                                break;
                            default:
                                newClassResult.setNolCategory(NolCategory.SeniorWomen);
                        }
                    }

                    // Set default team score calc method
                    newClassResult.setTeamResultType(TeamResultType.RaceTimes);

                    selectedClasses.add(newClassResult);
                }
            }
        }

        return selectedClasses;
    }

    private static boolean isUsingAandBfinals(ArrayList<ClassResultExtended> resultList) {

        // TODO create a list of possibilities here, eg: M21E-A, M21E A Final, M21E A-Final etc
        // TODO make this a bit more generic and extend to C (and D) finals
        // Decide if there are Elite A and B classes
        boolean usingEAclasses = false;
        boolean usingEBclasses = false;

        // Run through and decide if we have Elite (E) or A classes
        for (ClassResultExtended classResult : resultList) {
            String className = classResult.getClazz().getName();
            if (className.contains("EA")) {
                usingEAclasses = true;
            }
            if (className.contains("EB")) {
                usingEBclasses = true;
            }
        }

        return (usingEAclasses && usingEBclasses);
    }
    
    private static boolean getIsMixedRelay(ArrayList<ClassResultExtended> resultList){
        for (ClassResultExtended classResult : resultList) {
            if (classResult.getClazz().getName().toLowerCase().contains("mixed"))
                return true;
        }
        return false;
    }

    private static ArrayList<ClassResultExtended> trimResultList(ResultList resultList, boolean isRelay) {

        /* 
        This is an important method, here we'll trim off any non-NOL classes
        and make some decisions (what NOL category, how t0 calculate team 
        scores etc)
         */
        // TODO We need to treat junior and senior classes differently: we may have the case
        // where we have M/W21E and M/W20A + M/W18A.
        // First go through and decide which classes to keep
        // Seniors
        List<ClassResultExtended> seniorClassList = getClassesForAgeCategory(resultList, NolAgeCategory.Senior);

        // Juniors
        List<ClassResultExtended> juniorClassList = getClassesForAgeCategory(resultList, NolAgeCategory.Junior);

        ArrayList<ClassResultExtended> validClassList = new ArrayList<>();
        validClassList.addAll(seniorClassList);
        validClassList.addAll(juniorClassList);
        
        // Override the Team Result Type if this is a relay
        if (isRelay) {
            for (ClassResultExtended classResult : validClassList) {
                classResult.setTeamResultType(TeamResultType.Relay);
            }
        }

        return validClassList;

        // OLD METHOD FROM HERE
        // Remove all class results that are not NOL classes
        //boolean usingEliteClasses = isUsingEliteClasses(resultList);
        //List<ClassResult> classResults = resultList.getClassResult();
        //classResults.removeIf((ClassResult classResult) -> !isValidClass(classResult.getClazz().getName(), usingEliteClasses));
        //return resultList;
    }

    private static ArrayList<ClassResultExtended> mergeAandBfinals(ArrayList<ClassResultExtended> classResultList) {

        // TODO - this could be done more cleverly
        // Grab the numbers in the A Finals
        // Use a Map to keep track of how many runners in each classes A Final
        Map<NolCategory, Integer> sizeOfAfinals = new HashMap<>();
        for (NolCategory nolCategory : NolCategory.values()) {
            sizeOfAfinals.put(nolCategory, 0);
        }

        // Use a Map to keep track of where the A finals are in the result list
        Map<NolCategory, Integer> locationOfAfinals = new HashMap<>();

        for (ClassResult classResult : classResultList) {

            String className = classResult.getClazz().getName();

            //if (isValidClass(className, true)) {
            NolCategory nolCategory = getNolCategory(className);
            if (className.contains("A")) {
                // Assuming Only A finals have an "A" in the class name!
                int numberOfRunnersInThisFinal = classResult.getPersonResult().size();
                sizeOfAfinals.put(nolCategory, numberOfRunnersInThisFinal);
            }

            //}
        }

        // No just go through the B finals and add the number of A final runners to each RersonResult placing
        for (ClassResult classResult : classResultList) {

            String className = classResult.getClazz().getName();

            //if (isValidClass(className, true)) {
            NolCategory nolCategory = getNolCategory(className);
            if (className.contains("B")) {
                // Assuming Only A finals have an "A" in the class name!
                int numberOfRunnersInThisFinal = sizeOfAfinals.get(nolCategory);

                for (PersonResult personResult : classResult.getPersonResult()) {

                    if (personResult.getResult().get(0).getPosition() != null) {
                        int oldPosition = personResult.getResult().get(0).getPosition().intValue();
                        int newPosition = oldPosition + numberOfRunnersInThisFinal;

                        personResult.getResult().get(0).setPosition(BigInteger.valueOf(newPosition));
                    }
                }

                // Now find Corresponding A Final and add this class to it
                for (ClassResult aFinalClassResult : classResultList) {
                    String aFinalClassName = aFinalClassResult.getClazz().getName();
                    NolCategory aFinalNolCategory = getNolCategory(aFinalClassName);
                    if (aFinalNolCategory.equals(nolCategory) && aFinalClassName.contains("A")) {

                        List<PersonResult> aFinalPersonResults = aFinalClassResult.getPersonResult();
                        for (PersonResult personResult : classResult.getPersonResult()) {
                            aFinalPersonResults.add(personResult);
                        }
                    }
                }

                // change the name of the B class so we don't pick it up later (TODO remove it safely)
                classResult.getClazz().setName("REMOVE");
            }
            //}
        }

        // Finally remove the B class
        classResultList.removeIf((ClassResult classResult) -> classResult.getClazz().getName().contains("REMOVE"));
        //classResults.removeIf((ClassResult classResult) -> !isValidClass(classResult.getClazz().getName(), usingEliteClasses));        
        return classResultList;
    }

    /*
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
    }*/

    private static NolCategory getNolCategory(String className) {
        // Decide what NOL Category this result was in
        NolCategory nolCategory;
        
        if (className.toLowerCase().contains("mixed")) {
            if (className.toLowerCase().contains("junior")) {
                return NolScoreCalculator.NolCategory.JuniorMixed;
            }
            else {
                return NolScoreCalculator.NolCategory.SeniorMixed;
            }
        }

        if (className.contains("W")) {
            if (className.contains("21")) {
                nolCategory = NolScoreCalculator.NolCategory.SeniorWomen;
            } else {
                nolCategory = NolScoreCalculator.NolCategory.JuniorWomen;
            }

        } else if (className.contains("21")) {
            nolCategory = NolScoreCalculator.NolCategory.SeniorMen;
        } else {
            nolCategory = NolScoreCalculator.NolCategory.JuniorMen;
        }

        return nolCategory;
    }

    private static String getOutputDirectory() {
        String osName = System.getProperty("os.name");
        String homeDir = System.getProperty("user.home");
        
        if (osName.contains("Mac OS")) {            
            File selectedPath = null;
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
            FileDialog fd = new FileDialog(new Frame(), "Choose a file", FileDialog.LOAD);
            fd.setDirectory(homeDir);
            fd.setVisible(true);
            String filename = fd.getDirectory();
            selectedPath = new File(filename);
            if (filename == null) {
                InformationDialog.infoBox("No directory selected, press OK to exit.", "Warning");
                return "";
            } else {
                return selectedPath.toString();
            }            
        } else {
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

            } else {
                InformationDialog.infoBox("No directory selected, press OK to exit.", "Warning");
                return "";
            }
        }
    }

    private static Map<String, String> createNolOrganisationsMap() {
        Map<String, String> myMap = new HashMap<>();
        myMap.put("628", "VIC Victoria");
        myMap.put("629", "CBR Cockatoos");
        myMap.put("630", "NSW Stingers");
        myMap.put("631", "QLD Cyclones");
        myMap.put("632", "SA Arrows");
        myMap.put("633", "TAS Foresters");
        myMap.put("634", "WA Nomads");
        //myMap.put("0", "No Team");
        return myMap;
    }

    public static Map<String, String> nolOrganisationLongShortNamesMap() {
        Map<String, String> myMap = new HashMap<>();
        myMap.put("VIC Victoria", "VN V");
        myMap.put("CBR Cockatoos", "CC A");
        myMap.put("NSW Stingers", "ST N");
        myMap.put("QLD Cyclones", "QC Q");
        myMap.put("SA Arrows", "SW S");
        myMap.put("TAS Foresters", "TF T");
        myMap.put("WA Nomads", "WN W");
        //myMap.put("0", "No Team");
        return myMap;
    }

    public static Map<String, String> nolOrganisationShortLongNamesMap() {
        Map<String, String> myMap = new HashMap<>();
        myMap.put("VN V", "VIC Victoria");
        myMap.put("CC A", "CBR Cockatoos");
        myMap.put("ST N", "NSW Stingers");
        myMap.put("QC Q", "QLD Cyclones");
        myMap.put("SW S", "SA Arrows");
        myMap.put("TF T", "TAS Foresters");
        myMap.put("WN W", "WA Nomads");
        //myMap.put("0", "No Team");
        return myMap;
    }

    private static ArrayList<Entity>[] createNolSeasonTeams() {
        // This should create Senior and Junior Men and Women teams
        // for each of the State Teams defined in the nolOrganisations map
        ArrayList<Entity>[] nolSeasonTeams = new ArrayList[NolCategory.values().length];
        // Initialise!
        for (int i = 0; i < NolCategory.values().length; i++) {
            nolSeasonTeams[i] = new ArrayList<>();
        }

        Iterator it = nolOrganisations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            Id id = new Id();
            id.setValue((String) pair.getKey());
            Organisation organisation = new Organisation();
            organisation.setId(id);
            organisation.setName((String) pair.getValue());

            for (NolCategory nolCategory : NolCategory.values()) {
                Entity nolTeam = new Entity(organisation, nolCategory);

                int test = nolCategory.ordinal();

                nolSeasonTeams[nolCategory.ordinal()].add(nolTeam);
            }
        }

        return nolSeasonTeams;
    }

    private static ArrayList<Result> createEmptyNolTeamResults(NolCategory nolCategory, Id eventId, TeamResultType teamResultType) {

        ArrayList<Result> nolTeamResults = new ArrayList<>();
        
        nolTeamResultsIndexes.clear();
        
        int index = 0;

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
            
            nolTeamResultsIndexes.put(id.getValue(), index);
            index++;
        }

        return nolTeamResults;
    }
    
    private static Map<Id, Result> createEmptyNolTeamResultsMap(NolCategory nolCategory, Id eventId, TeamResultType teamResultType) {
        
        Map<Id, Result> nolTeamResultsMap = new HashMap<>();
        
        for (Map.Entry pair : nolOrganisations.entrySet()) {

            // Create an empty Result for each team (organisation)
            Id id = new Id();
            id.setValue((String) pair.getKey());
            Organisation organisation = new Organisation();
            organisation.setId(id);
            organisation.setName((String) pair.getValue());
            boolean isTeamResult = true; // TODO need this in Result constructor

            Result result = new Result(eventId, organisation, nolCategory, teamResultType);

            nolTeamResultsMap.put(id, result);
        }

        return nolTeamResultsMap;
    }

    private static Organisation testingOnlyTranslateOrganisationId(TeamResult teamResult) {

        // When testing on 2016 results
        Organisation newOrganisation = new Organisation();
        Id id = new Id();
        String teamName = teamResult.getName().toLowerCase();

        String x = "";
        if (teamName.contains("vic")) { //(shortName.endsWith("V")) {
            x = "628";
        } else if (teamName.contains("act") || teamName.contains("cbr") || teamName.contains("cockatoos")) {
            x = "629";
        } else if (teamName.contains("nsw") || teamName.contains("stingers")) {
            x = "630";
        } else if (teamName.contains("qld") || teamName.contains("cyclones")) {
            x = "631";
        } else if (teamName.contains("sa") || teamName.contains("arrows")) {
            x = "632";
        } else if (teamName.contains("tas") || teamName.contains("foresters")) {
            x = "633";
        } else if (teamName.contains("wa") || teamName.contains("nomads")) {
            x = "634";
        } else {
            x = "0";
        }

        id.setValue(x);
        newOrganisation.setId(id);
        newOrganisation.setName(nolOrganisations.get(x));
        newOrganisation.setShortName(nolOrganisationLongShortNamesMap().get(nolOrganisations.get(x)));

        return newOrganisation;
    }
    
    public static class DateLabelFormatter extends AbstractFormatter {

    private String datePattern = "yyyy-MM-dd";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }

        return "";
    }

}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// Eventor API key: 780b352c5c1e4c718e4ad35b48e77397
package nolscorecalculator;

import IofXml30.java.ClassResult;
import IofXml30.java.EventList;
import IofXml30.java.Id;
import IofXml30.java.ObjectFactory;
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

/**
 *
 * @author shep
 */
public class NolScoreCalculator {

    public static final String CREATOR = "Sheptron Industries";

    public enum NolCategory {
        SeniorMen, SeniorWomen, JuniorMen, JuniorWomen
    };
    private static final String[] VALID_ELITE_CLASSES = {"M21E", "W21E", "M17-20E", "M-20E", "W17-20E", "W-20E"};
    private static final String[] VALID_NONELITE_CLASSES = {"M21A", "W21A", "M20A", "W20A"};

    /**
     * @param args the command line arguments
     * @throws java.net.MalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException, IOException, JAXBException, SAXException, ParserConfigurationException {
        {
            // TODO get dates to/from

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

            int selection = jp.showConfirmDialog(null, new JScrollPane(list), "", JOptionPane.OK_CANCEL_OPTION);

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

                        for (PersonResult personResult : classResult.getPersonResult()) {

                            // To deal with runners that compete in Junior and Senior we 
                            // split an athlete into a Junior 
                            // Ignore someone if they didn't start - otherwise non-OK status will get ZERO points
                            if (personResult.getResult().get(0).getStatus() == ResultStatus.DID_NOT_START) {
                                continue;
                            }

                            // Create NOL Athlete and Result from the IOF PersonResult
                            NolAthlete nolAthlete = new NolAthlete(personResult, className);
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
                        }

                    }

                }

            }

            // Calculate Total Scores
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
            
            // Just write Senior Men for now...
            resultsPrinter.writeResults(seniorMenResults);

            resultsPrinter.finaliseTable();

            String outFilename = "/home/shep/Desktop/testing.html";
            StringToFile.write(outFilename, resultsPrinter.htmlResults);

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

}

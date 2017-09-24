/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.DateAndOptionalTime;
import IofXml30.java.Event;
import java.util.Comparator;

/**
 *
 * @author shep
 * 
 * This is to compare Events in an EventList downloaded from Eventor
 * and sort by date, but putting events with names containing "NOL"
 * at the top...
 * 
 */
public class NolEventCompare implements Comparator<Event> {
    
    @Override
    public int compare(Event e1, Event e2) {
        
        // Get rid of MTBO
        boolean event1isMtbo = e1.getName().toLowerCase().contains("mtb");
        
        boolean event2isMtbo = e2.getName().toLowerCase().contains("mtb");
        
        if (event1isMtbo && !event2isMtbo){
            return 1;
        }
        else if(!event1isMtbo && event2isMtbo) {
            return -1;
        }
        
        // Now look for NOL or National League
        boolean event1isNol = e1.getName().toLowerCase().contains("nol") || e1.getName().toLowerCase().contains("national league");
        
        boolean event2isNol = e2.getName().toLowerCase().contains("nol") || e2.getName().toLowerCase().contains("national league");
        
        if (event1isNol && !event2isNol){
            return -1;
        }
        else if(!event1isNol && event2isNol) {
            return 1;
        }
        
        // Now look for National Championships
        boolean event1isNatChampionship = e1.getName().toLowerCase().contains("australian") && e1.getName().toLowerCase().contains("championship");
        
        boolean event2isNatChampionship = e2.getName().toLowerCase().contains("australian") && e2.getName().toLowerCase().contains("championship");
        
        if (event1isNatChampionship && !event2isNatChampionship){
            return -1;
        }
        else if(!event1isNatChampionship && event2isNatChampionship) {
            return 1;
        }
        
        // Now look for any other championships
        boolean event1isChampionship = e1.getName().toLowerCase().contains("championship");
        
        boolean event2isChampionship = e2.getName().toLowerCase().contains("championship");
        
        if (event1isChampionship && !event2isChampionship){
            return -1;
        }
        else if(!event1isChampionship && event2isChampionship) {
            return 1;
        }
        // Otherwise leave as is
        else return 0;
        
        /*DateAndOptionalTime e1Time =  e1.getStartTime();
        DateAndOptionalTime e2Time =  e2.getStartTime();
        
        // If we can't determine DAY then say they're equal
        if (e1Time.getDate() == null || e2Time.getDate() == null) return 0;        
        if (!e1Time.getDate().isValid() || !e2Time.getDate().isValid()) return 0;  

        int dayComparison = e1Time.getDate().toGregorianCalendar().compareTo(e2Time.getDate().toGregorianCalendar());
                
        if (dayComparison != 0) return dayComparison;
        
        // If we're here then the two events are on the same day
        // If we can't determine TIME then say they're equal
        if (e1Time.getTime() == null || e2Time.getTime() == null) return 0;
        if (!e1Time.getTime().isValid() || !e2Time.getTime().isValid()) return 0;
        
        int timeOfDayComparison = e1Time.getTime().toGregorianCalendar().compareTo(e2Time.getTime().toGregorianCalendar());
        
        return timeOfDayComparison;*/
    }
}

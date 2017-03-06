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
 */
public class EventCompare implements Comparator<Event>  {
    
    @Override
    public int compare(Event e1, Event e2) {
        
        DateAndOptionalTime e1Time =  e1.getStartTime();
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
        
        return timeOfDayComparison;
    }
    
}
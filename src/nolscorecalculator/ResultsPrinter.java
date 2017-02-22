/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.Id;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author shep
 */
public class ResultsPrinter {

    public String htmlResults;
    private ArrayList<Id> events;

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

}

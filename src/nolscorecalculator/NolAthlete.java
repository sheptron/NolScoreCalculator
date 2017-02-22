/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.Id;
import IofXml30.java.PersonResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



/**
 *
 * @author shep
 */
public class NolAthlete {

    /**
     *
     */
    
    
    public ArrayList<NolResult> results;
    public String name = "";
    String firstName = "";
    String surname = "";
    public int yearOfBirth = 0;
    public String controlCard = "0";
    public Id id;                      // id and/or controlCard could be used for checking duplicates
    public String club = "";                // TODO
    public String sex = "";
    public int totalScore = 0;
    public NolScoreCalculator.NolCategory nolCategory;
    
  /*
    NolAthlete (int _yearOfBirth, String _controlCard, String _sex, String firstName, String lastName, Id _id, String _club) {
        yearOfBirth = _yearOfBirth;
        controlCard = _controlCard;                 
        results = new ArrayList<>();
        sex = _sex;
        name = firstName + " " + lastName;
        id = _id;
        club = _club;
    }
    */
    NolAthlete (PersonResult personResult, String className) {
        
        yearOfBirth = personResult.getPerson().getBirthDate().getYear();
        controlCard = personResult.getResult().get(0).getControlCard().get(0).getValue();                 
        results = new ArrayList<>();
        sex = personResult.getPerson().getSex();
        firstName = personResult.getPerson().getName().getGiven();
        surname = personResult.getPerson().getName().getFamily();
        name = firstName + " " + surname;
        id = personResult.getPerson().getId().get(0);
        club = personResult.getOrganisation().getShortName();  
        //organisation = personResult.getOrganisation();
        
        // Decide what NOL Category this result was in
        if (className.contains("W")){
            if (className.contains("21")) nolCategory = NolScoreCalculator.NolCategory.SeniorWomen;
            else nolCategory = NolScoreCalculator.NolCategory.JuniorWomen;
            
        }
        else {
            if (className.contains("21")) nolCategory = NolScoreCalculator.NolCategory.SeniorMen;
            else nolCategory = NolScoreCalculator.NolCategory.JuniorMen;
        }
    }
    
    public String getSex(){
        return sex;
    }
    
    public void addResult(NolResult result){
        results.add(result);       
    }
     
    public int updateTotalScore(int numberOfRaces){
        // We might not always want best 9 results, after 5 races we might want
        // to post the cumulative results with best 3 races counting.
        
        // Sorts results by score and return the sum of the highest numberOfRaces.
        Collections.sort(results, new Comparator<NolResult>() {
            @Override
            public int compare(NolResult r1, NolResult r2) {
                return r2.getScore() - r1.getScore();
            }
        });
        
        totalScore = 0;
        for (int i=0; i<results.size(); i++){
            if (i >= numberOfRaces) break;
            
            totalScore += results.get(i).getScore();
        }
        
        return totalScore;
    }
         
    @Override
    public boolean equals(Object obj) {
        
        // Athlete objects are equal if the names are the same - check YOB as well?
        
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NolAthlete athlete = (NolAthlete) obj;
        
        // Names Identical
        boolean surnameMatch = this.surname.equalsIgnoreCase(athlete.surname);
        boolean fullNameMatch = this.name.equalsIgnoreCase(athlete.name);  
        // Eventor ID Number (athletes without an ID will be 0)
        boolean idMatch = ( this.id.equals(athlete.id) && !this.id.getValue().equals("") );
        boolean yobMatch = (this.yearOfBirth == athlete.yearOfBirth) && (this.yearOfBirth != 0);
        boolean nolCategoryMatch = (this.getNolCategory() == athlete.getNolCategory());
        
        if (!nolCategoryMatch) return false; // If they're in a different NOL category then consider them different people even if their Eventor ID is the same
        else return (idMatch) || (fullNameMatch && yobMatch); // Log
    }
    /*public class athleteScoreComparator implements Comparator<Athlete> {
    
        @Override
        public int compare(Athlete athlete1, Athlete athlete2) {
            // TODO need to work out the index, not just 0
            return (athlete1.results.get(0).handicappedSpeed > athlete2.results.get(0).handicappedSpeed) ? 1 : 0;
        }  
    }*/

    public NolScoreCalculator.NolCategory getNolCategory() {
        return nolCategory;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
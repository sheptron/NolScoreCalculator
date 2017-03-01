/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.Id;
import IofXml30.java.Organisation;
import IofXml30.java.PersonResult;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author shep
 */
public class NolTeamResult {
    
    public double raceTime = 0; // Total race time (of all counting runners)
    public int score;
    public int placing;
    public Id id;
    private boolean status;
    
    private static final int [] TEAM_SCORES = {9, 7, 5, 4, 3, 2, 1};
    private static final int RELAY_MULTIPLIER = 2; // Team scores in a relay are TEAM_SCORES x RELAY_MULTIPLIER
    public static final int RUNNERS_TO_COUNT = 3;
    
    public Organisation organisation;
    public NolScoreCalculator.NolCategory nolCategory;
    
    public ArrayList<Double> raceTimes;
    public ArrayList<String> athleteNames;  // Names corresponding to the times in raceTimes            
    public int numberOfIndividualResults = 0;
    public boolean isRelay = false;

    public boolean isIsRelay() {
        return isRelay;
    }

    public void setIsRelay(boolean isRelay) {
        this.isRelay = isRelay;
    }   

    public NolTeamResult(Id id) {
        this.id = id;  
        this.raceTimes = new ArrayList<>();
        this.athleteNames = new ArrayList<>();
    }

    public NolTeamResult(Organisation organisation, NolScoreCalculator.NolCategory nolCategory) {
        this.organisation = organisation;
        this.nolCategory = nolCategory;
        
        this.raceTimes = new ArrayList<>();
        this.athleteNames = new ArrayList<>();
    }

    //public String getOrganisationIdValue() {
    //    return organisation.getId().getValue();
    //}
    
    
    
    public void addIndividualResult(PersonResult personResult){
        
        this.raceTimes.add(personResult.getResult().get(0).getTime());
        
        String athleteName = personResult.getPerson().getName().getGiven() + " " + personResult.getPerson().getName().getFamily();
        this.athleteNames.add(athleteName);
        
        numberOfIndividualResults++;

        // Update the team time 
        // Sort times
        // TODO - should we keep Names and Times aligned so we can report who's times counted??
        Collections.sort(this.raceTimes);
        
        this.raceTime = 0;
        for (int i=0; i< Math.min(this.raceTimes.size(),RUNNERS_TO_COUNT); i++){
            this.raceTime += this.raceTimes.get(i);
        }
        
    }
    
    public void calculateScore(){
        int score = 0;
         
        if (this.placing <= TEAM_SCORES.length){
            this.score = TEAM_SCORES[this.placing-1];
        }

        if (this.isRelay) score = 2*score;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public int getNumberOfIndividualResults() {
        return numberOfIndividualResults;
    }

    public double getRaceTime() {
        return raceTime;
    }    

    public ArrayList<Double> getRaceTimes() {
        return raceTimes;
    }

    public void setRaceTimes(ArrayList<Double> raceTimes) {
        this.raceTimes = raceTimes;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPlacing() {
        return placing;
    }

    public void setPlacing(int placing) {
        this.placing = placing;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NolTeamResult other = (NolTeamResult) obj;
        if (!this.getOrganisation().getId().getValue().equals(other.getOrganisation().getId().getValue())){
            return false;
        }
        /*if (!this.getId().getValue().equals(other.getId().getValue())) {
            return false;
        }*/
        return true;
    }
    
}

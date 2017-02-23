/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.Id;
import IofXml30.java.PersonResult;
import java.util.ArrayList;

/**
 *
 * @author shep
 */
public class NolTeamResult {
    
    private static final int [] TEAM_SCORES = {9, 7, 5, 4, 3, 2, 1};
    private static final int RELAY_MULTIPLIER = 2; // Team scores in a relay are TEAM_SCORES x RELAY_MULTIPLIER
    
    public ArrayList<Double> raceTimes;
    public ArrayList<String> athleteNames;  // Names corresponding to the times in raceTimes
    public int score;
    public int placing;
    private boolean status;
    public Id id;
    public int numberOfIndividualResults = 0;

    public NolTeamResult(Id id) {
        this.id = id;  
        this.raceTimes = new ArrayList<>();
        this.athleteNames = new ArrayList<>();
    }
    
    public void addIndividualResult(PersonResult personResult){
        
        this.raceTimes.add(personResult.getResult().get(0).getTime());
        
        String athleteName = personResult.getPerson().getName().getGiven() + " " + personResult.getPerson().getName().getFamily();
        this.athleteNames.add(athleteName);
        
        numberOfIndividualResults++;                
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
        if (!this.getId().getValue().equals(other.getId().getValue())) {
            return false;
        }
        return true;
    }
    
}

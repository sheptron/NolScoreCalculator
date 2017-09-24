/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.Id;
import IofXml30.java.Organisation;
import IofXml30.java.PersonResult;
import IofXml30.java.ResultStatus;
import IofXml30.java.TeamMemberResult;
import IofXml30.java.TeamResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 *
 * @author shep
 */
public final class Result {
    
    /*
    We can calculate team scores using sum of race times, NOL scores or race placing/position
    We also need to consider relays
    */
    public enum TeamResultType{
        RaceTimes, NolScores, Placings, Relay
    };
    
    public double raceTime = 0; // Total race time (of all counting runners)
    public int score = 0;
    public int placing;
    public Id id;
    public boolean status;
    private boolean isTeamResult = false;
    private boolean isFinalRaceOfSeason = false;
    public TeamResultType teamResultType;
    
    public static final int [] TEAM_SCORES = {9, 7, 5, 4, 3, 2, 1};
    public static final int RELAY_MULTIPLIER = 2; // Team scores in a relay are TEAM_SCORES x RELAY_MULTIPLIER
    public static final int RUNNERS_TO_COUNT = 3;
    
    private static final int [] INDIVIDUAL_SCORES = {30, 27, 24, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
    
    private static final int [] INDIVIDUAL_SCORES_FINAL_RACE = {33, 30, 27, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
    
    public Organisation organisation;
    public NolScoreCalculator.NolCategory nolCategory;
    
    public ArrayList<Double> raceTimes;
    public ArrayList<String> athleteNames;      // Names corresponding to the times in raceTimes            
    public int numberOfIndividualResults = 0;
    public int bestIndividualPlacing = Integer.MAX_VALUE;
    //public boolean isRelay = false;

    //public boolean isIsRelay() {
    //    return isRelay;
    //}

    //public void setIsRelay(boolean isRelay) {
    //    this.isRelay = isRelay;
    //}   

    public Result(Id id) {
        this.id = id;  
        this.raceTimes = new ArrayList<>();
        this.athleteNames = new ArrayList<>();
    }

    public Result(Id eventId, Organisation organisation, NolScoreCalculator.NolCategory nolCategory, TeamResultType teamResultType) {
        
        // Use this Constructor for teams
        this.id = eventId;
        this.organisation = organisation;
        this.nolCategory = nolCategory;
        this.isTeamResult = true;
        this.teamResultType = teamResultType;
        
        this.raceTimes = new ArrayList<>();
        this.athleteNames = new ArrayList<>();
    }

    public Result(PersonResult personResult, Id _id) {       
       
        // TODO constructor for an individual result
        this.id =_id;
        this.isTeamResult = false;
        
        this.raceTimes = new ArrayList<>();
        this.athleteNames = new ArrayList<>();
        
        addIndividualResult(personResult);
        
        this.status = (personResult.getResult().get(0).getStatus() == ResultStatus.OK);

        if (this.status) {
            this.placing = personResult.getResult().get(0).getPosition().intValue();
            calculateScore();
        }
        else {
            this.placing = 0;
            this.score = 0;
        }      
        
    }
    
    public void addIndividualResult(PersonResult personResult){
        
        // Don't add DNFs and DSQs
        if (personResult.getResult().get(0).getStatus() != ResultStatus.OK){
            return;
        }
            
        this.raceTimes.add(personResult.getResult().get(0).getTime());
        
        String athleteName = personResult.getPerson().getName().getGiven() + " " + personResult.getPerson().getName().getFamily();
        this.athleteNames.add(athleteName);
        
        numberOfIndividualResults++;
        
        if (personResult.getResult().get(0).getPosition().intValue() < this.bestIndividualPlacing){
            this.bestIndividualPlacing = personResult.getResult().get(0).getPosition().intValue();
        }

        // Update the team time 
        // Sort times
        // TODO - should we keep Names and Times aligned so we can report who's times counted??
        Collections.sort(this.raceTimes);
        
        this.raceTime = 0;
        for (int i=0; i< Math.min(this.raceTimes.size(),RUNNERS_TO_COUNT); i++){
            
            this.raceTime += this.raceTimes.get(i);
            
        }                             
    }
    
    public void addIndividualResult(PersonResult personResult, TeamResultType teamResultType){
        
        // Don't add DNFs and DSQs
        if (personResult.getResult().get(0).getStatus() != ResultStatus.OK){
            return;
        }
        
        switch(teamResultType){
            case RaceTimes:
                this.raceTimes.add(personResult.getResult().get(0).getTime());
                break;
            case Placings:
                this.raceTimes.add(personResult.getResult().get(0).getPosition().doubleValue());
                break;
            default:
                // Race Times
                this.raceTimes.add(personResult.getResult().get(0).getTime());            
        }       
                
        String athleteName = personResult.getPerson().getName().getGiven() + " " + personResult.getPerson().getName().getFamily();
        this.athleteNames.add(athleteName);
        
        numberOfIndividualResults++;
        
        if (personResult.getResult().get(0).getPosition().intValue() < this.bestIndividualPlacing){
            this.bestIndividualPlacing = personResult.getResult().get(0).getPosition().intValue();
        }

        // Update the team time 
        // Sort times
        // TODO - should we keep Names and Times aligned so we can report who's times counted??
        Collections.sort(this.raceTimes);
        
        this.raceTime = 0;
        for (int i=0; i< Math.min(this.raceTimes.size(),RUNNERS_TO_COUNT); i++){
            
            this.raceTime += this.raceTimes.get(i);
            
        }                             
    }
    
    public void addIndividualResult(Result result){
        // TODO think about merging these two addIndividualResult methods
        // Don't add DNFs and DSQs
        if (!result.status){
            return;
        }
        
        this.raceTimes.add((double)result.getScore());
        
        // TODO use athlete names in Result objects so we can add them here
        String athleteName = "TO DO"; 
        this.athleteNames.add(athleteName);
        
        numberOfIndividualResults++;
        
        if (result.getPlacing() < this.bestIndividualPlacing){
            this.bestIndividualPlacing = result.getPlacing();
        }

        // Update the team time 
        // Sort SCORES (decreasing)
        Collections.sort(this.raceTimes, Collections.reverseOrder());
        
        this.raceTime = 0;
        for (int i=0; i< Math.min(this.raceTimes.size(),RUNNERS_TO_COUNT); i++){
            
            this.raceTime += this.raceTimes.get(i);
            
        }                             
    }
    
    public void addRelayResult(TeamResult teamResult, int numberOfLegs){               
               
        // Don't add DNFs and DSQs
        for (TeamMemberResult teamMemberResult : teamResult.getTeamMemberResult()) {
                       
            if (teamMemberResult.getResult().get(0).getOverallResult().getStatus() != ResultStatus.OK){            
                return;
            }
        
        }        
        
        // Add result, first need to find the last leg runner
        for (TeamMemberResult teamMemberResult : teamResult.getTeamMemberResult()) {
                        
            if (teamMemberResult.getResult().get(0).getLeg().intValue() == numberOfLegs) {
                
                // We only want to count the fastest (relay) team if there are 2+ relay teams running for the same NOL team
                // raceTime is 0.0 to start with so check for this
                double newRaceTime = teamMemberResult.getResult().get(0).getOverallResult().getTime();
                if (this.raceTime > newRaceTime || this.raceTime == 0) {                         
                    this.raceTime = newRaceTime;
                    this.numberOfIndividualResults = 1;
                }
            }            
        }                                             
    }
    
    public void calculateScore(){
                
        // TODO Final Race + 3 and points down to 28
        
        int[] scores;
        if (this.isTeamResult) {
            scores = TEAM_SCORES;
        }
        else if (this.isFinalRaceOfSeason) {
            scores = INDIVIDUAL_SCORES_FINAL_RACE;
        }
        else {   
            scores = INDIVIDUAL_SCORES;
        }
         
        if (this.placing <= scores.length && this.placing > 0){
            this.score = scores[this.placing-1];
        }

        if (this.teamResultType == TeamResultType.Relay) this.score = 2*this.score;
        
        // Teams with no finishers score 0 points
        if (this.isTeamResult){
            if (this.numberOfIndividualResults == 0){
                this.score = 0;
            }
        } 
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
    
    public void setRaceTime(double raceTime) {
        this.raceTime = raceTime;
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

    public int getBestIndividualPlacing() {
        return bestIndividualPlacing;
    }

    public boolean isIsFinalRaceOfSeason() {
        return isFinalRaceOfSeason;
    }

    public void setIsFinalRaceOfSeason(boolean isFinalRaceOfSeason) {
        this.isFinalRaceOfSeason = isFinalRaceOfSeason;
    }       

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.id);
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
        final Result other = (Result) obj;
        if (!this.getId().getValue().equals(other.getId().getValue())) {
            return false;
        }
        return true;
    }
}

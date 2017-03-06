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
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author shep
 */
public final class Result {
    
    public double raceTime = 0; // Total race time (of all counting runners)
    public int score = 0;
    public int placing;
    public Id id;
    public boolean status;
    private boolean isTeamResult = false;
    
    private static final int [] TEAM_SCORES = {9, 7, 5, 4, 3, 2, 1};
    private static final int RELAY_MULTIPLIER = 2; // Team scores in a relay are TEAM_SCORES x RELAY_MULTIPLIER
    public static final int RUNNERS_TO_COUNT = 3;
    
    private static final int [] INDIVIDUAL_SCORES = {30, 27, 24, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
    
    public Organisation organisation;
    public NolScoreCalculator.NolCategory nolCategory;
    
    public ArrayList<Double> raceTimes;
    public ArrayList<String> athleteNames;      // Names corresponding to the times in raceTimes            
    public int numberOfIndividualResults = 0;
    public boolean isRelay = false;

    public boolean isIsRelay() {
        return isRelay;
    }

    public void setIsRelay(boolean isRelay) {
        this.isRelay = isRelay;
    }   

    public Result(Id id) {
        this.id = id;  
        this.raceTimes = new ArrayList<>();
        this.athleteNames = new ArrayList<>();
    }

    public Result(Id eventId, Organisation organisation, NolScoreCalculator.NolCategory nolCategory, boolean isTeamResult) {
        
        // Constructor for teams
        this.id = eventId;
        this.organisation = organisation;
        this.nolCategory = nolCategory;
        this.isTeamResult = isTeamResult;
        
        this.raceTimes = new ArrayList<>();
        this.athleteNames = new ArrayList<>();
    }

    public Result(PersonResult personResult, Id _id) {       
       
        // TODO constructor for an individual result
        id =_id;
        this.isTeamResult = false;
        
        this.raceTimes = new ArrayList<>();
        this.athleteNames = new ArrayList<>();
        
        //raceTime = personResult.getResult().get(0).getTime();
        addIndividualResult(personResult);
        
        this.status = (personResult.getResult().get(0).getStatus() == ResultStatus.OK);

        if (this.status) {
            this.placing = personResult.getResult().get(0).getPosition().intValue();
            calculateScore();
            //score = calculatePointScore(INDIVIDUAL_SCORES, placing);
        }
        else {
            this.placing = 0;
            this.score = 0;
        }      
        
    }

    //public String getOrganisationIdValue() {
    //    return organisation.getId().getValue();
    //}
    
    public void addIndividualResult(PersonResult personResult){
        
        // Don't add DNFs and DSQs
        if (personResult.getResult().get(0).getStatus() != ResultStatus.OK){
            return;
        }
            
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
        
        // TODO Final Race + 3 and points down to 28
        
        int[] scores;
        if (isTeamResult) scores = TEAM_SCORES;
        else scores = INDIVIDUAL_SCORES;
         
        if (this.placing <= scores.length){
            this.score = scores[this.placing-1];
        }

        if (this.isRelay) this.score = 2*this.score;
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
        final Result other = (Result) obj;
        if (!this.getId().getValue().equals(other.getId().getValue())) {
            return false;
        }
        return true;
    }
    
//    private static int calculatePointScore(int[] SCORE_TABLE, int placing){
//        int score = 0;
//        
//        // TODO Final Race + 3 and points down to 28
//         
//        if (placing <= SCORE_TABLE.length){
//            score = SCORE_TABLE[placing-1];
//        }
//        
//        return score;
//    }
}
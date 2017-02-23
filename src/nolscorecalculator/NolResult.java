/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.Id;
import IofXml30.java.PersonResult;
import IofXml30.java.ResultStatus;
import java.util.Objects;

/**
 *
 * @author shep
 */
public class NolResult {
    
    private static final int [] INDIVIDUAL_SCORES = {30, 27, 24, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

    public Id getId() {
        return id;
    }
    
    /*
    Teams with an equal total time are placed according to the results of the highest placed competitors in each team;
    */
    
    public double raceTime;
    public int score;
    public int placing;
    private boolean status;
    public Id id;

    public NolResult(PersonResult personResult, Id _id) {       
       
        // TODO constructor for a team result
        id =_id;
        
        raceTime = personResult.getResult().get(0).getTime();
        
        status = (personResult.getResult().get(0).getStatus() == ResultStatus.OK);

        if (status) {
            placing = personResult.getResult().get(0).getPosition().intValue();
            score = calculatePointScore(INDIVIDUAL_SCORES, placing);
        }
        else {
            placing = 0;
            score = 0;
        }      
        
    }

    public double getRaceTime() {
        return raceTime;
    }

    public void setRaceTime(double raceTime) {
        this.raceTime = raceTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    private static int calculatePointScore(int[] SCORE_TABLE, int placing){
        int score = 0;
        
        // TODO Final Race + 3 and points down to 28
         
        if (placing <= SCORE_TABLE.length){
            score = SCORE_TABLE[placing-1];
        }
        
        return score;
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
        final NolResult other = (NolResult) obj;
        if (!this.getId().getValue().equals(other.getId().getValue())) {
            return false;
        }
        return true;
    }
    
}

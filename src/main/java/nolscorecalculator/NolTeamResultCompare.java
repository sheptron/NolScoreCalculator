/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import java.util.Comparator;

/**
 *
 * @author shep
 */
public class NolTeamResultCompare implements Comparator<Result> {

    @Override
    public int compare(Result o1, Result o2) {
        
        // -1 if o1 comes before o2, 
        // +1 if 01 comes after o2
        // 0 if o1 and o2 are equal
        
        // Always put teams of 3 runners ahead of teams with 2, which go ahead of teams of 1 
        // Fudge this by adding on 100^(3+1-numRunners) minutes, so teams with only 2 runners will have total time 10000secs plus their actual time
        
        int fudge1 = Result.RUNNERS_TO_COUNT + 1 - Math.min(Result.RUNNERS_TO_COUNT, o1.getNumberOfIndividualResults());
        int fudge2 = Result.RUNNERS_TO_COUNT + 1 - Math.min(Result.RUNNERS_TO_COUNT, o2.getNumberOfIndividualResults());
        
        double o1Value = o1.getRaceTime() + Math.pow(1000.0,(double) fudge1);
        
        double o2Value = o2.getRaceTime() + Math.pow(1000.0,(double) fudge2);
        
        if (o1Value < o2Value){
            return -1;
        }
        else if (o1Value > o2Value){
            return 1;
        }
        else {
            // Teams with equal time are placed according to the placing of the highest placed competitor
            if (o1.getBestIndividualPlacing() < o2.getBestIndividualPlacing()){
                return -1;
            }
            else if (o1.getBestIndividualPlacing() > o2.getBestIndividualPlacing()){
                return 1;
            }
            else {
                return 0;
            }
        }
    }
    
}
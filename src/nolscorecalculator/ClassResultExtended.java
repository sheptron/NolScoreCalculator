/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.ClassResult;
import nolscorecalculator.NolScoreCalculator.NolCategory;
import nolscorecalculator.Result.TeamResultType;

/**
 *
 * @author shep
 */
public class ClassResultExtended extends ClassResult{
    
    /*
    
    Class to add some more information to IOF XML CLassResult
    We want NOL Category and the method we need to use to determine team results
    
    */
    public NolCategory nolCategory;
    public TeamResultType teamResultType;   

    public ClassResultExtended() {
    }
    
    public ClassResultExtended(ClassResult classResult){
        // Copy across everything we need
        this.clazz = classResult.getClazz();
        this.course = classResult.getCourse();
        this.personResult = classResult.getPersonResult();
        this.teamResult = classResult.getTeamResult();
        this.extensions = classResult.getExtensions();
        this.modifyTime = classResult.getModifyTime();
        this.timeResolution = classResult.getTimeResolution();
    }

    public NolCategory getNolCategory() {
        return nolCategory;
    }

    public void setNolCategory(NolCategory nolCategory) {
        this.nolCategory = nolCategory;
    }

    public TeamResultType getTeamResultType() {
        return teamResultType;
    }

    public void setTeamResultType(TeamResultType teamResultType) {
        this.teamResultType = teamResultType;
    }
}

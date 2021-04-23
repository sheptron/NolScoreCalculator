/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.ClassResult;
import IofXml30.java.Course;
import IofXml30.java.SimpleRaceCourse;
import nolscorecalculator.NolScoreCalculator.NolCategory;
import nolscorecalculator.Result.TeamResultType;

import java.util.List;

/**
 *
 * @author shep
 */
public class ClassResultExtended extends ClassResult {
    
    /*
    
    Class to add some more information to IOF XML CLassResult
    We want NOL Category and the method we need to use to determine team results
    
    */
    public NolCategory nolCategory;
    public TeamResultType teamResultType;

    public boolean subJunior;

    public ClassResultExtended() {
        subJunior = false;
    }

    public ClassResultExtended(ClassResultExtended classResult){
        // Copy across everything we need
        this.clazz = classResult.getClazz();
        this.course = classResult.getCourse();
        this.personResult = classResult.getPersonResult();
        this.teamResult = classResult.getTeamResult();
        this.extensions = classResult.getExtensions();
        this.modifyTime = classResult.getModifyTime();
        this.timeResolution = classResult.getTimeResolution();
        this.teamResultType = classResult.getTeamResultType();
        this.nolCategory = classResult.getNolCategory();
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

    public void setCourse(List<SimpleRaceCourse> course) { this.course = (List<SimpleRaceCourse>) course; }

    public boolean isSubJunior() { return subJunior; }

    public void setSubJunior(boolean subJunior) { this.subJunior = subJunior; }
}

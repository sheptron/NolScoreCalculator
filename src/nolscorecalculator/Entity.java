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
import java.util.Objects;

/**
 *
 * @author shep
 * 
* <OrganisationId>628</OrganisationId>
<Name languageId="sv">Vic Nuggets</Name>
* 
* <OrganisationId>629</OrganisationId>
<Name languageId="sv">ACT Cockatoos</Name>
* 
* <OrganisationId>630</OrganisationId>
<Name languageId="sv">NSW Stingers</Name>
* 
* <OrganisationId>631</OrganisationId>
<Name languageId="sv">Qld Cyclones</Name>
* 
* <OrganisationId>632</OrganisationId>
<Name languageId="sv">SA Arrows</Name>
* 
* <OrganisationId>633</OrganisationId>
<Name languageId="sv">Tas Foresters</Name>
* 
* <OrganisationId>634</OrganisationId>
<Name languageId="sv">WA Nomads</Name>
* 
* <OrganisationId>635</OrganisationId>
<Name languageId="sv">Bushrangers</Name>
* 
*/
public class Entity {
    
    public Id id;
    public NolScoreCalculator.NolCategory nolCategory;
    public String name = "";    
    public Organisation organisation;
    public int totalScore = 0;
    
    public ArrayList<Result> results;
    
    public String teamName;
    String firstName = "";
    String surname = "";
    public int yearOfBirth = 0;
    public String controlCard = "0";
                          
    public String club = "";                // TODO
    public String sex = "";

    public NolScoreCalculator.NolTeamName nolTeamName;   
    
    public static final int MAX_NUMBER_OF_RACES_TO_COUNT = 9;
    
    public boolean isTeam = false;

    Entity (PersonResult personResult, NolScoreCalculator.NolCategory _nolCategory) {
        
        // Constructor for Individuals               
        results = new ArrayList<>();
        sex = personResult.getPerson().getSex();
        firstName = personResult.getPerson().getName().getGiven();
        surname = personResult.getPerson().getName().getFamily();
        name = firstName + " " + surname;
        if (!personResult.getPerson().getId().isEmpty()){
            id = personResult.getPerson().getId().get(0);
        }        
        else {
            id = new Id();
            id.setValue("");
        }
        if (personResult.getOrganisation() != null){                            
            club = personResult.getOrganisation().getShortName();
            teamName = personResult.getOrganisation().getName();
            organisation = personResult.getOrganisation();
        }
        else {
            setEmptyOrganisation();
        }
        nolCategory = _nolCategory;   
        this.isTeam = false;
    }
    
    public Entity(Organisation organisation, NolScoreCalculator.NolCategory nolCategory) {
        
        // Constructor for Teams
        this.organisation = organisation;
        this.id = organisation.getId();
        this.name = organisation.getName();
        this.nolCategory = nolCategory;
        
        this.isTeam = true;
        
        this.results = new ArrayList<>();
        
        // Decide What the team name and category is based on the id        
    }
    
    private void setEmptyOrganisation(){
        
        organisation = new Organisation();        
        organisation.setName("");
        organisation.setShortName("");
    }
      
    public int getSortableTeamScore(Id mostRecentEventId){
                        
        // Teams on same final total score are ranked according to score in the final event
        // Fudge! Add most recent race result to total score 
        // We need to amplify totalScore so this only separates teams on the same totalScore         
        int amplifier = Result.TEAM_SCORES[0]*Result.RELAY_MULTIPLIER*5;
        int mostRecentRaceIndex = this.results.indexOf(new Result(mostRecentEventId));
        
        if (mostRecentRaceIndex == -1){
            return amplifier*totalScore;
        }
        else {
            return amplifier*totalScore + results.get(mostRecentRaceIndex).getScore();
        }
    }

    public int getTotalScore() {
        return totalScore;
    }        

    public String getTeamName() {
        return teamName;
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
    
    public String getSex(){
        return sex;
    }

    public void addResult(Result result) {
        this.results.add(result);

        // Update Total Score
        this.totalScore = 0;
        for (Result res : this.results) {
            this.totalScore += res.getScore();
        }

    }
     
    public int updateTotalScore(int numberOfEvents){
        // We might not always want best 9 results, after 5 races we might want
        // to post the cumulative results with best 3 races counting.
        
        int numberOfRaces = getNumberOfRacesToCount(numberOfEvents);
        
        // Sorts results by score and return the sum of the highest numberOfRaces.
        Collections.sort(this.results, (Result r1, Result r2) -> r2.getScore() - r1.getScore());
        
        this.totalScore = 0;
        for (int i=0; i<this.results.size(); i++){
            if (i >= numberOfRaces) break;
            
            this.totalScore += this.results.get(i).getScore();
        }
        
        return this.totalScore;
    }
    
    private int getNumberOfRacesToCount(int numberOfEvents){
        
        // Junior teams final total scores is best 50% or 50% plus one
        // Senior teams count ALL races
        // TODO - test this!
        if (this.isTeam) {
            if (this.nolCategory==NolScoreCalculator.NolCategory.SeniorMen || this.nolCategory==NolScoreCalculator.NolCategory.SeniorWomen){
                return numberOfEvents;
            }
        }
        
        int numberOfRaceToCount;
        // Count an extra race early on in the season
        if (numberOfEvents < 8) {
            numberOfRaceToCount = (int) Math.ceil((double) numberOfEvents / 2.0) + 1;
        } else {
            numberOfRaceToCount = (int) Math.ceil((double) numberOfEvents / 2.0);
        }
        
        if (numberOfRaceToCount > MAX_NUMBER_OF_RACES_TO_COUNT) {
            numberOfRaceToCount = MAX_NUMBER_OF_RACES_TO_COUNT;
        }
        
        return numberOfRaceToCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NolScoreCalculator.NolCategory getNolCategory() {
        return nolCategory;
    }

    public void setNolCategory(NolScoreCalculator.NolCategory nolCategory) {
        this.nolCategory = nolCategory;
    }
    
    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.id);
        hash = 17 * hash + Objects.hashCode(this.nolCategory);
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
        final Entity other = (Entity) obj;
        if (!this.id.getValue().equals(other.id.getValue())) {
            return false;
        }
        if (this.nolCategory != other.nolCategory) {
            return false;
        }
        if (this.id.getValue().isEmpty()){
            return false;
        }
        return true;
    }    
//    @Override
//    public boolean equals(Object obj) {
//        
//        // Athlete objects are equal if the names are the same - check YOB as well?
//        
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        NolAthlete athlete = (NolAthlete) obj;
//        
//        // Names Identical
//        boolean surnameMatch = this.surname.equalsIgnoreCase(athlete.surname);
//        boolean fullNameMatch = this.name.equalsIgnoreCase(athlete.name);  
//        // Eventor ID Number (athletes without an ID will be 0)
//        boolean idMatch = ( this.id.equals(athlete.id) && !this.id.getValue().equals("") );
//        boolean yobMatch = (this.yearOfBirth == athlete.yearOfBirth) && (this.yearOfBirth != 0);
//        boolean nolCategoryMatch = (this.getNolCategory() == athlete.getNolCategory());
//        
//        if (!nolCategoryMatch) return false; // If they're in a different NOL category then consider them different people even if their Eventor ID is the same
//        else return (idMatch) || (fullNameMatch && yobMatch); // Log
//    }
}
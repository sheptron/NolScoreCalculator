/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import IofXml30.java.Id;
import IofXml30.java.Organisation;
import java.util.ArrayList;
import java.util.Objects;
import nolscorecalculator.NolScoreCalculator.NolCategory;
import nolscorecalculator.NolScoreCalculator.NolTeamName;

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
public class NolTeam {
    
    public Id id;                                       // In NolAthete
    public NolCategory nolCategory;
    public String name;
    public Organisation organisation;
    public ArrayList<NolTeamResult> nolTeamResults;
    public int totalScore = 0;
    
    public NolTeamName nolTeamName;    
    
     
    public NolTeam(Organisation organisation, NolCategory nolCategory) {
        
        this.organisation = organisation;
        this.id = organisation.getId();
        this.name = organisation.getName();
        this.nolCategory = nolCategory;
        
        this.nolTeamResults = new ArrayList<>();
        
        // Decide What the team name and category is based on the id
        
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public ArrayList<NolTeamResult> getResults() {
        return nolTeamResults;
    }

    public void setResults(ArrayList<NolTeamResult> nolTeamResults) {
        this.nolTeamResults = nolTeamResults;
    }
    
    public void addResult(NolTeamResult nolTeamResult){
        this.nolTeamResults.add(nolTeamResult);
        
        // Update Total Score
        this.totalScore = 0;
        for (NolTeamResult result : this.nolTeamResults){
            this.totalScore += result.getScore();
        }
        
    }

    public String getName() {
        return name;
    }
    
    public int getTotalScore() {
        return totalScore;
    }

    public NolCategory getNolCategory() {
        return nolCategory;
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
        final NolTeam other = (NolTeam) obj;
        if (!this.id.getValue().equals(other.id.getValue())) {
            return false;
        }
        if (this.nolCategory != other.nolCategory) {
            return false;
        }
        return true;
    }    
}

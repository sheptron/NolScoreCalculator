/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NolXml10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author shep
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NolEvent", propOrder = {
    "raceNumber",
    "name"
})

public class NolEvent {
    
    
// TODO Event ID
    
    @XmlElement(name = "RaceNumber", required = true)
    protected String raceNumber;

    public String getRaceNumber() {
        return raceNumber;
    }

    public void setRaceNumber(String raceNumber) {
        this.raceNumber = raceNumber;
    }
    
    @XmlElement(name = "Name")
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}

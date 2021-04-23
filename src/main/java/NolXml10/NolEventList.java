/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NolXml10;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author shep
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NolEventList", propOrder = {
    "event"
})


public class NolEventList {  

    @XmlElement(name = "Event", required = true)
    protected List<NolEvent> event;

    public List<NolEvent> getEvent() {
         if (event == null) {
            event = new ArrayList<>();
        }
        return this.event;
    }

    public void setEvent(List<NolEvent> event) {
        this.event = event;
    }



  
}


//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.10.07 at 08:57:59 PM AEDT 
//


package IofXml30.java;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * 
 *         Container element that is used to add custom elements from other schemas.
 *       
 * 
 * <p>Java class for Extensions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Extensions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Extensions", namespace="eventor", propOrder = {
    "eventRaceId"
//"any"
})
public class Extensions {

    /*@XmlAnyElement(lax = true)
    protected List<Object> any;
    
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }*/
   
    @XmlElement(name = "EventRaceId", namespace="eventor")
    protected EventRaceId eventRaceId;
    
    public void setEventId(EventRaceId value){
        this.eventRaceId = value;
    }
    
    public EventRaceId getEventRaceId(){
        if (this.eventRaceId == null) {
            this.eventRaceId = new EventRaceId();
        }
        return this.eventRaceId;
    }
}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.12.03 at 09:33:02 PM AEDT 
//


package EventorApi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="eventClassId" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="numberOfEntries" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="numberOfStarts" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "ClassCompetitorCount")
public class ClassCompetitorCount {

    @XmlAttribute(name = "eventClassId")
    @XmlSchemaType(name = "anySimpleType")
    protected String eventClassId;
    @XmlAttribute(name = "numberOfEntries")
    @XmlSchemaType(name = "anySimpleType")
    protected String numberOfEntries;
    @XmlAttribute(name = "numberOfStarts")
    @XmlSchemaType(name = "anySimpleType")
    protected String numberOfStarts;

    /**
     * Gets the value of the eventClassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventClassId() {
        return eventClassId;
    }

    /**
     * Sets the value of the eventClassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventClassId(String value) {
        this.eventClassId = value;
    }

    /**
     * Gets the value of the numberOfEntries property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberOfEntries() {
        return numberOfEntries;
    }

    /**
     * Sets the value of the numberOfEntries property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberOfEntries(String value) {
        this.numberOfEntries = value;
    }

    /**
     * Gets the value of the numberOfStarts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberOfStarts() {
        return numberOfStarts;
    }

    /**
     * Sets the value of the numberOfStarts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberOfStarts(String value) {
        this.numberOfStarts = value;
    }

}
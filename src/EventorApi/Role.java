//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.12.03 at 09:33:02 PM AEDT 
//


package EventorApi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{}OrganisationId"/>
 *           &lt;element ref="{}Organisation"/>
 *           &lt;element ref="{}EventId"/>
 *           &lt;element ref="{}Event"/>
 *         &lt;/choice>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{}RoleTypeId"/>
 *           &lt;element ref="{}RoleType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "organisationId",
    "organisation",
    "eventId",
    "event",
    "roleTypeId",
    "roleType"
})
@XmlRootElement(name = "Role")
public class Role {

    @XmlElement(name = "OrganisationId")
    protected OrganisationId organisationId;
    @XmlElement(name = "Organisation")
    protected Organisation organisation;
    @XmlElement(name = "EventId")
    protected EventId eventId;
    @XmlElement(name = "Event")
    protected Event event;
    @XmlElement(name = "RoleTypeId")
    protected RoleTypeId roleTypeId;
    @XmlElement(name = "RoleType")
    protected RoleType roleType;

    /**
     * Gets the value of the organisationId property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationId }
     *     
     */
    public OrganisationId getOrganisationId() {
        return organisationId;
    }

    /**
     * Sets the value of the organisationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationId }
     *     
     */
    public void setOrganisationId(OrganisationId value) {
        this.organisationId = value;
    }

    /**
     * Gets the value of the organisation property.
     * 
     * @return
     *     possible object is
     *     {@link Organisation }
     *     
     */
    public Organisation getOrganisation() {
        return organisation;
    }

    /**
     * Sets the value of the organisation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Organisation }
     *     
     */
    public void setOrganisation(Organisation value) {
        this.organisation = value;
    }

    /**
     * Gets the value of the eventId property.
     * 
     * @return
     *     possible object is
     *     {@link EventId }
     *     
     */
    public EventId getEventId() {
        return eventId;
    }

    /**
     * Sets the value of the eventId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventId }
     *     
     */
    public void setEventId(EventId value) {
        this.eventId = value;
    }

    /**
     * Gets the value of the event property.
     * 
     * @return
     *     possible object is
     *     {@link Event }
     *     
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets the value of the event property.
     * 
     * @param value
     *     allowed object is
     *     {@link Event }
     *     
     */
    public void setEvent(Event value) {
        this.event = value;
    }

    /**
     * Gets the value of the roleTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link RoleTypeId }
     *     
     */
    public RoleTypeId getRoleTypeId() {
        return roleTypeId;
    }

    /**
     * Sets the value of the roleTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoleTypeId }
     *     
     */
    public void setRoleTypeId(RoleTypeId value) {
        this.roleTypeId = value;
    }

    /**
     * Gets the value of the roleType property.
     * 
     * @return
     *     possible object is
     *     {@link RoleType }
     *     
     */
    public RoleType getRoleType() {
        return roleType;
    }

    /**
     * Sets the value of the roleType property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoleType }
     *     
     */
    public void setRoleType(RoleType value) {
        this.roleType = value;
    }

}
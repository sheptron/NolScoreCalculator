//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.12.03 at 09:33:02 PM AEDT 
//


package EventorApi;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{}CompetitorId"/>
 *         &lt;choice>
 *           &lt;element ref="{}PersonId"/>
 *           &lt;element ref="{}Person"/>
 *         &lt;/choice>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{}OrganisationId"/>
 *           &lt;element ref="{}Organisation"/>
 *         &lt;/choice>
 *         &lt;element ref="{}PreSelectedClass" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}CCard" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{}DisciplineId"/>
 *           &lt;element ref="{}Discipline"/>
 *         &lt;/choice>
 *         &lt;element ref="{}ModifyDate" minOccurs="0"/>
 *         &lt;element ref="{}ModifiedBy" minOccurs="0"/>
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
    "competitorId",
    "personId",
    "person",
    "organisationId",
    "organisation",
    "preSelectedClass",
    "cCard",
    "disciplineId",
    "discipline",
    "modifyDate",
    "modifiedBy"
})
@XmlRootElement(name = "Competitor")
public class Competitor {

    @XmlElement(name = "CompetitorId", required = true)
    protected CompetitorId competitorId;
    @XmlElement(name = "PersonId")
    protected PersonId personId;
    @XmlElement(name = "Person")
    protected Person person;
    @XmlElement(name = "OrganisationId")
    protected OrganisationId organisationId;
    @XmlElement(name = "Organisation")
    protected Organisation organisation;
    @XmlElement(name = "PreSelectedClass")
    protected List<PreSelectedClass> preSelectedClass;
    @XmlElement(name = "CCard")
    protected List<CCard> cCard;
    @XmlElement(name = "DisciplineId")
    protected DisciplineId disciplineId;
    @XmlElement(name = "Discipline")
    protected Discipline discipline;
    @XmlElement(name = "ModifyDate")
    protected ModifyDate modifyDate;
    @XmlElement(name = "ModifiedBy")
    protected ModifiedBy modifiedBy;

    /**
     * Gets the value of the competitorId property.
     * 
     * @return
     *     possible object is
     *     {@link CompetitorId }
     *     
     */
    public CompetitorId getCompetitorId() {
        return competitorId;
    }

    /**
     * Sets the value of the competitorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompetitorId }
     *     
     */
    public void setCompetitorId(CompetitorId value) {
        this.competitorId = value;
    }

    /**
     * Gets the value of the personId property.
     * 
     * @return
     *     possible object is
     *     {@link PersonId }
     *     
     */
    public PersonId getPersonId() {
        return personId;
    }

    /**
     * Sets the value of the personId property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonId }
     *     
     */
    public void setPersonId(PersonId value) {
        this.personId = value;
    }

    /**
     * Gets the value of the person property.
     * 
     * @return
     *     possible object is
     *     {@link Person }
     *     
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     * 
     * @param value
     *     allowed object is
     *     {@link Person }
     *     
     */
    public void setPerson(Person value) {
        this.person = value;
    }

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
     * Gets the value of the preSelectedClass property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the preSelectedClass property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPreSelectedClass().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PreSelectedClass }
     * 
     * 
     */
    public List<PreSelectedClass> getPreSelectedClass() {
        if (preSelectedClass == null) {
            preSelectedClass = new ArrayList<PreSelectedClass>();
        }
        return this.preSelectedClass;
    }

    /**
     * Gets the value of the cCard property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cCard property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCCard().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CCard }
     * 
     * 
     */
    public List<CCard> getCCard() {
        if (cCard == null) {
            cCard = new ArrayList<CCard>();
        }
        return this.cCard;
    }

    /**
     * Gets the value of the disciplineId property.
     * 
     * @return
     *     possible object is
     *     {@link DisciplineId }
     *     
     */
    public DisciplineId getDisciplineId() {
        return disciplineId;
    }

    /**
     * Sets the value of the disciplineId property.
     * 
     * @param value
     *     allowed object is
     *     {@link DisciplineId }
     *     
     */
    public void setDisciplineId(DisciplineId value) {
        this.disciplineId = value;
    }

    /**
     * Gets the value of the discipline property.
     * 
     * @return
     *     possible object is
     *     {@link Discipline }
     *     
     */
    public Discipline getDiscipline() {
        return discipline;
    }

    /**
     * Sets the value of the discipline property.
     * 
     * @param value
     *     allowed object is
     *     {@link Discipline }
     *     
     */
    public void setDiscipline(Discipline value) {
        this.discipline = value;
    }

    /**
     * Gets the value of the modifyDate property.
     * 
     * @return
     *     possible object is
     *     {@link ModifyDate }
     *     
     */
    public ModifyDate getModifyDate() {
        return modifyDate;
    }

    /**
     * Sets the value of the modifyDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModifyDate }
     *     
     */
    public void setModifyDate(ModifyDate value) {
        this.modifyDate = value;
    }

    /**
     * Gets the value of the modifiedBy property.
     * 
     * @return
     *     possible object is
     *     {@link ModifiedBy }
     *     
     */
    public ModifiedBy getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the value of the modifiedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModifiedBy }
     *     
     */
    public void setModifiedBy(ModifiedBy value) {
        this.modifiedBy = value;
    }

}

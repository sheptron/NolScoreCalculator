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
 *         &lt;element name="ResultListUrl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SplitTimeListUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "resultListUrl",
    "splitTimeListUrl"
})
@XmlRootElement(name = "ImportResultListResult")
public class ImportResultListResult {

    @XmlElement(name = "ResultListUrl", required = true)
    protected String resultListUrl;
    @XmlElement(name = "SplitTimeListUrl")
    protected String splitTimeListUrl;

    /**
     * Gets the value of the resultListUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultListUrl() {
        return resultListUrl;
    }

    /**
     * Sets the value of the resultListUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultListUrl(String value) {
        this.resultListUrl = value;
    }

    /**
     * Gets the value of the splitTimeListUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSplitTimeListUrl() {
        return splitTimeListUrl;
    }

    /**
     * Sets the value of the splitTimeListUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSplitTimeListUrl(String value) {
        this.splitTimeListUrl = value;
    }

}

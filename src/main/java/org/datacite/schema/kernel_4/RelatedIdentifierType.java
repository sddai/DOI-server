//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.06.06 at 03:43:03 PM CEST 
//
package org.datacite.schema.kernel_4;

import fr.cnes.doi.utils.Requirement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for relatedIdentifierType.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * <
 * pre>
 * &lt;simpleType name="relatedIdentifierType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ARK"/&gt;
 *     &lt;enumeration value="arXiv"/&gt;
 *     &lt;enumeration value="bibcode"/&gt;
 *     &lt;enumeration value="DOI"/&gt;
 *     &lt;enumeration value="EAN13"/&gt;
 *     &lt;enumeration value="EISSN"/&gt;
 *     &lt;enumeration value="Handle"/&gt;
 *     &lt;enumeration value="IGSN"/&gt;
 *     &lt;enumeration value="ISBN"/&gt;
 *     &lt;enumeration value="ISSN"/&gt;
 *     &lt;enumeration value="ISTC"/&gt;
 *     &lt;enumeration value="LISSN"/&gt;
 *     &lt;enumeration value="LSID"/&gt;
 *     &lt;enumeration value="PMID"/&gt;
 *     &lt;enumeration value="PURL"/&gt;
 *     &lt;enumeration value="UPC"/&gt;
 *     &lt;enumeration value="URL"/&gt;
 *     &lt;enumeration value="URN"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@Requirement(
        reqId = "DOI_SRV_010",
        reqName = "Création de métadonnées"
)
@Requirement(
        reqId = "DOI_SRV_040",
        reqName = "Mise à jour des métadonnées d'un DOI"
)
@Requirement(
        reqId = "DOI_ARCHI_050",
        reqName = "Vérification du schéma de métadonnées"
)
@XmlType(name = "relatedIdentifierType")
@XmlEnum
public enum RelatedIdentifierType {

    /**
     *
     */
    ARK("ARK"),
    /**
     *
     */
    @XmlEnumValue("arXiv")
    AR_XIV("arXiv"),
    /**
     *
     */
    @XmlEnumValue("bibcode")
    BIBCODE("bibcode"),
    /**
     *
     */
    DOI("DOI"),
    /**
     *
     */
    @XmlEnumValue("EAN13")
    EAN_13("EAN13"),
    /**
     *
     */
    EISSN("EISSN"),
    /**
     *
     */
    @XmlEnumValue("Handle")
    HANDLE("Handle"),
    /**
     *
     */
    IGSN("IGSN"),
    /**
     *
     */
    ISBN("ISBN"),
    /**
     *
     */
    ISSN("ISSN"),
    /**
     *
     */
    ISTC("ISTC"),
    /**
     *
     */
    LISSN("LISSN"),
    /**
     *
     */
    LSID("LSID"),
    /**
     *
     */
    PMID("PMID"),
    /**
     *
     */
    PURL("PURL"),
    /**
     *
     */
    UPC("UPC"),
    /**
     *
     */
    URL("URL"),
    /**
     *
     */
    URN("URN");
    private final String value;

    RelatedIdentifierType(String v) {
        value = v;
    }

    /**
     *
     * @return
     */
    public String value() {
        return value;
    }

    /**
     *
     * @param v
     * @return
     */
    public static RelatedIdentifierType fromValue(String v) {
        for (RelatedIdentifierType c : RelatedIdentifierType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

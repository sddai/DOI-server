//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.06.06 at 03:43:03 PM CEST 
//
package org.datacite.schema.kernel_4;

import fr.cnes.doi.utils.spec.Requirement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for descriptionType.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;simpleType name="descriptionType"&gt; &lt;restriction
 * base="{http://www.w3.org/2001/XMLSchema}string"&gt; &lt;enumeration value="Abstract"/&gt;
 * &lt;enumeration value="Methods"/&gt; &lt;enumeration value="SeriesInformation"/&gt;
 * &lt;enumeration value="TableOfContents"/&gt; &lt;enumeration value="TechnicalInfo"/&gt;
 * &lt;enumeration value="Other"/&gt; &lt;/restriction&gt; &lt;/simpleType&gt;
 * </pre>
 *
 */
@Requirement(reqId = Requirement.DOI_SRV_010, reqName = Requirement.DOI_SRV_010_NAME)
@Requirement(reqId = Requirement.DOI_SRV_040, reqName = Requirement.DOI_SRV_040_NAME)
@Requirement(reqId = Requirement.DOI_INTER_060, reqName = Requirement.DOI_INTER_060_NAME)
@XmlType(name = "descriptionType")
@XmlEnum
public enum DescriptionType {

    /**
     * A brief description of the resource and the context in which the resource was created.
     * Recommended for discovery. Use "<br>" to indicate a line break for improved rendering of
     * multiple paragraphs, but otherwise no html markup.
     * <p>
     * Example: http://data.datacite.org/10.1594/PANGAEA.771774
     */
    @XmlEnumValue("Abstract")
    ABSTRACT("Abstract"),
    /**
     * The methodology employed for the study or research. Recommended for discovery.
     * <p>
     * For example, see section "Sampling, Processing and Quality Control Methods" in the following
     * dataset record: https://knb.ecoinformatics.org/#view/doi:10.5063/F1DZ067F
     */
    @XmlEnumValue("Methods")
    METHODS("Methods"),
    /**
     * Information about a repeating series, such as volume, issue, number. For use with grey
     * literature. If providing an ISSN, use property 12 (RelatedIdentifier),
     * relatedIdentifierType=ISSN. For dataset series, use property 12 (RelatedIdentifier) and
     * describe the relationships with isPartOf or HasPart.
     * <p>
     * Example: http://data.datacite.org/10.4229/23RDEUPVSEC2008‐5CO.8.3
     */
    @XmlEnumValue("SeriesInformation")
    SERIES_INFORMATION("SeriesInformation"),
    /**
     * A listing of the Table of Contents. Use "<br>" to indicate a line break for improved
     * rendering of multiple paragraphs, but otherwise no html markup.
     * <p>
     * Example: http://data.datacite.org/10.5678/LCRS/FOR816.CIT.1031
     */
    @XmlEnumValue("TableOfContents")
    TABLE_OF_CONTENTS("TableOfContents"),
    /**
     * Detailed information that may be associated with design, implementation, operation, use,
     * and/or maintenance of a process or system. For software description, this may include a
     * readme.txt, and necessary environmental information such as hardware, operational software,
     * applications/programs, and versions. For other uses, this can include specific and detailed
     * information as necessary and appropriate.
     */
    @XmlEnumValue("TechnicalInfo")
    TECHNICAL_INFO("TechnicalInfo"),
    /**
     * Other description information that does not fit into an existing category. Use for any other
     * description type.
     */
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    DescriptionType(String v) {
        value = v;
    }

    /**
     * Returns the value.
     * @return the value
     */
    public String value() {
        return value;
    }

    /**
     * Returns the description type from the value.
     * @param v value
     * @return the description type
     */
    public static DescriptionType fromValue(String v) {
        for (DescriptionType c : DescriptionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

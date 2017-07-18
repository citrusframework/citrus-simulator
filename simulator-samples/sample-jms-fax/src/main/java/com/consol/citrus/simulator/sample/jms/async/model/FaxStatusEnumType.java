//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.07.18 um 08:48:56 AM CEST 
//


package com.consol.citrus.simulator.sample.jms.async.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für FaxStatusEnumType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="FaxStatusEnumType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="QUEUED"/&gt;
 *     &lt;enumeration value="SENDING"/&gt;
 *     &lt;enumeration value="SUCCESS"/&gt;
 *     &lt;enumeration value="CANCELLED"/&gt;
 *     &lt;enumeration value="ERROR"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FaxStatusEnumType")
@XmlEnum
public enum FaxStatusEnumType {

    QUEUED,
    SENDING,
    SUCCESS,
    CANCELLED,
    ERROR;

    public String value() {
        return name();
    }

    public static FaxStatusEnumType fromValue(String v) {
        return valueOf(v);
    }

}

//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.07.18 um 08:48:56 AM CEST 
//


package com.consol.citrus.simulator.sample.jms.async.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.consol.citrus.simulator.sample.model.xml.fax package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SendFaxRequest_QNAME = new QName("http://citrusframework.org/schemas/fax", "SendFaxRequest");
    private final static QName _SendFaxResponse_QNAME = new QName("http://citrusframework.org/schemas/fax", "SendFaxResponse");
    private final static QName _SendFaxMessage_QNAME = new QName("http://citrusframework.org/schemas/fax", "SendFaxMessage");
    private final static QName _GetStatusResponse_QNAME = new QName("http://citrusframework.org/schemas/fax", "GetStatusResponse");
    private final static QName _StatusUpdateMessage_QNAME = new QName("http://citrusframework.org/schemas/fax", "StatusUpdateMessage");
    private final static QName _CancelFaxRequest_QNAME = new QName("http://citrusframework.org/schemas/fax", "CancelFaxRequest");
    private final static QName _CancelFaxResponse_QNAME = new QName("http://citrusframework.org/schemas/fax", "CancelFaxResponse");
    private final static QName _CancelFaxMessage_QNAME = new QName("http://citrusframework.org/schemas/fax", "CancelFaxMessage");
    private final static QName _GetTransmissionReportResponse_QNAME = new QName("http://citrusframework.org/schemas/fax", "GetTransmissionReportResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.consol.citrus.simulator.sample.model.xml.fax
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendFaxType }
     * 
     */
    public SendFaxType createSendFaxType() {
        return new SendFaxType();
    }

    /**
     * Create an instance of {@link ResponseType }
     * 
     */
    public ResponseType createResponseType() {
        return new ResponseType();
    }

    /**
     * Create an instance of {@link GetStatusRequest }
     * 
     */
    public GetStatusRequest createGetStatusRequest() {
        return new GetStatusRequest();
    }

    /**
     * Create an instance of {@link FaxStatusType }
     * 
     */
    public FaxStatusType createFaxStatusType() {
        return new FaxStatusType();
    }

    /**
     * Create an instance of {@link CancelFaxType }
     * 
     */
    public CancelFaxType createCancelFaxType() {
        return new CancelFaxType();
    }

    /**
     * Create an instance of {@link GetTransmissionReportRequest }
     * 
     */
    public GetTransmissionReportRequest createGetTransmissionReportRequest() {
        return new GetTransmissionReportRequest();
    }

    /**
     * Create an instance of {@link TransmissionReportType }
     * 
     */
    public TransmissionReportType createTransmissionReportType() {
        return new TransmissionReportType();
    }

    /**
     * Create an instance of {@link FaxType }
     * 
     */
    public FaxType createFaxType() {
        return new FaxType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendFaxType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://citrusframework.org/schemas/fax", name = "SendFaxRequest")
    public JAXBElement<SendFaxType> createSendFaxRequest(SendFaxType value) {
        return new JAXBElement<SendFaxType>(_SendFaxRequest_QNAME, SendFaxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://citrusframework.org/schemas/fax", name = "SendFaxResponse")
    public JAXBElement<ResponseType> createSendFaxResponse(ResponseType value) {
        return new JAXBElement<ResponseType>(_SendFaxResponse_QNAME, ResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendFaxType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://citrusframework.org/schemas/fax", name = "SendFaxMessage")
    public JAXBElement<SendFaxType> createSendFaxMessage(SendFaxType value) {
        return new JAXBElement<SendFaxType>(_SendFaxMessage_QNAME, SendFaxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FaxStatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://citrusframework.org/schemas/fax", name = "GetStatusResponse")
    public JAXBElement<FaxStatusType> createGetStatusResponse(FaxStatusType value) {
        return new JAXBElement<FaxStatusType>(_GetStatusResponse_QNAME, FaxStatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FaxStatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://citrusframework.org/schemas/fax", name = "StatusUpdateMessage")
    public JAXBElement<FaxStatusType> createStatusUpdateMessage(FaxStatusType value) {
        return new JAXBElement<FaxStatusType>(_StatusUpdateMessage_QNAME, FaxStatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelFaxType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://citrusframework.org/schemas/fax", name = "CancelFaxRequest")
    public JAXBElement<CancelFaxType> createCancelFaxRequest(CancelFaxType value) {
        return new JAXBElement<CancelFaxType>(_CancelFaxRequest_QNAME, CancelFaxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://citrusframework.org/schemas/fax", name = "CancelFaxResponse")
    public JAXBElement<ResponseType> createCancelFaxResponse(ResponseType value) {
        return new JAXBElement<ResponseType>(_CancelFaxResponse_QNAME, ResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelFaxType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://citrusframework.org/schemas/fax", name = "CancelFaxMessage")
    public JAXBElement<CancelFaxType> createCancelFaxMessage(CancelFaxType value) {
        return new JAXBElement<CancelFaxType>(_CancelFaxMessage_QNAME, CancelFaxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransmissionReportType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://citrusframework.org/schemas/fax", name = "GetTransmissionReportResponse")
    public JAXBElement<TransmissionReportType> createGetTransmissionReportResponse(TransmissionReportType value) {
        return new JAXBElement<TransmissionReportType>(_GetTransmissionReportResponse_QNAME, TransmissionReportType.class, null, value);
    }

}

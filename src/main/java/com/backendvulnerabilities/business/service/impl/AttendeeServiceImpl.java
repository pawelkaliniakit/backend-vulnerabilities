package com.backendvulnerabilities.business.service.impl;

import com.backendvulnerabilities.business.dto.AttendeeTransformation;
import com.backendvulnerabilities.business.entity.Attendee;
import com.backendvulnerabilities.business.repository.AttendeeRepository;
import com.backendvulnerabilities.business.service.AttendeeService;
import com.backendvulnerabilities.business.service.ValidationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class AttendeeServiceImpl implements AttendeeService {

    private AttendeeRepository attendeeRepository;

    private ValidationService validationService;

    @Override
    public List<Attendee> findAll() {
        return attendeeRepository.findAll();
    }

    @Override
    public void add(Attendee attendee) {
        validationService.validate(attendee);
        attendeeRepository.save(attendee);
    }

    @Override
    public void deleteAll() {
        attendeeRepository.deleteAll();
    }

    @Override
    public void addFromTransformation(AttendeeTransformation attendeeTransformation)
            throws IOException, SAXException, ParserConfigurationException, TransformerException, JAXBException {
        InputStream xslFileStream = new ByteArrayInputStream(attendeeTransformation.getXslt().getBytes());
        Document xmlDoc = createDocumentFromXML(attendeeTransformation.getXml());
        Transformer transformer = createSchemaTransformer(xslFileStream);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(xmlDoc), new StreamResult(os));
        String xmlAfterTransformation =  new String(os.toByteArray(), Charset.forName("UTF-8"));
        final Attendee attendee = unmarshall(xmlAfterTransformation);
        validationService.validate(attendee);
        attendeeRepository.save(attendee);
    }

    @Override
    public void addFromMultipartFile(MultipartFile file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(file.getBytes()));
        System.out.println(file.getBytes());
        final Object obj = ois.readObject();
        System.out.println(obj.getClass());
        final Attendee attendee = (Attendee) obj;
        validationService.validate(attendee);
        attendeeRepository.save(attendee);
    }

    private Attendee unmarshall(String xmlAfterTransformation) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(Attendee.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(xmlAfterTransformation);
        Attendee attendee = (Attendee) unmarshaller.unmarshal(reader);
        return attendee;
    }

    private Transformer createSchemaTransformer(InputStream xslStream) throws TransformerConfigurationException {
        TransformerFactory transformerFactory =
                TransformerFactory.newInstance("org.apache.xalan.processor.TransformerFactoryImpl", null);
        return transformerFactory.newTransformer(new StreamSource(xslStream));
    }

    private Document createDocumentFromXML(String xmlAsString)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setXIncludeAware(true);
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        InputStream is = new ByteArrayInputStream(xmlAsString.getBytes());
        return builder.parse(is);
    }
}

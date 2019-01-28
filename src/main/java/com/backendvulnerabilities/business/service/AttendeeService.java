package com.backendvulnerabilities.business.service;

import com.backendvulnerabilities.business.dto.AttendeeTransformation;
import com.backendvulnerabilities.business.entity.Attendee;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

@Service
public interface AttendeeService {

    List<Attendee> findAll();

    void add(Attendee attendee);

    void addFromTransformation(AttendeeTransformation attendeeTransformation)
            throws IOException, SAXException, ParserConfigurationException, TransformerException, JAXBException;

    void addFromMultipartFile(MultipartFile multipartFile) throws IOException, ClassNotFoundException;
}

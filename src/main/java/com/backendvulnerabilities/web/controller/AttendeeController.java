/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package com.backendvulnerabilities.web.controller;

import com.backendvulnerabilities.business.dto.AttendeeTransformation;
import com.backendvulnerabilities.business.entity.Attendee;
import com.backendvulnerabilities.business.service.AttendeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;
import org.xml.sax.SAXException;

import javax.management.Attribute;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;


@Controller
public class AttendeeController {

    @Autowired
    private AttendeeService attendeeService;

    @ModelAttribute("allAttendees")
    public List<Attendee> populateAttendees() {
        return this.attendeeService.findAll();
    }


    @RequestMapping({"/", "/attendeemng"})
    public String showAttendees(final Attendee attendee, @RequestParam(required = false) String greeting, ModelMap modelMap) {
        attendee.setAdded(Calendar.getInstance().getTime());
        modelMap.put("greeting", greeting);
        return "attendeemng";
    }


    @RequestMapping(value = "/attendeemng", params = {"save"})
    public String saveAttendee(final Attendee attendee, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "attendeemng";
        }
        this.attendeeService.add(attendee);
        model.clear();
        return "redirect:/attendeemng";
    }

    @RequestMapping(value = "/attendeemng", params = {"delete"})
    public String deleteAllAttendees(){
        attendeeService.deleteAll();
        return "redirect:/attendeemng";
    }

    @GetMapping("/upload-attendee")
    public String showUploadForm(Model model) throws IOException {
        return "upload";
    }

    @PostMapping("/upload-attendee")
    public String uploadAttendeeBinary(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
            throws IOException, ClassNotFoundException {
        attendeeService.addFromMultipartFile(file);
        return "redirect:/attendeemng";
    }

    @PostMapping(value = "/upload-attendee-xml", consumes = MediaType.TEXT_XML_VALUE)
    public String uploadAttendeeXML(@RequestBody Attendee attendee)
            throws IOException, ClassNotFoundException {
        attendeeService.add(attendee);
        return "redirect:/attendeemng";
    }

    @GetMapping("upload-attendee-xslt")
    public String uploadAttendeeXMLWithXSLTForm(final AttendeeTransformation attendeeTransformation) {
        return "attendeemng-xslt";
    }

    @MessageMapping("/attendeemng")
    @SendTo("/topic/results")
    public Attendee addAttendeeWebSocket(Attendee attendee, HttpServletRequest request) throws Exception {
        attendeeService.add(attendee);
        return attendee;
    }

    @PostMapping(value = "/upload-attendee-xslt")
    public String uploadAttendeeXMLWithXSLT(final AttendeeTransformation attendeeTransformation)
            throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException,
            TransformerConfigurationException, TransformerException, JAXBException {
        attendeeService.addFromTransformation(attendeeTransformation);
        return "redirect:/attendeemng";
    }



}

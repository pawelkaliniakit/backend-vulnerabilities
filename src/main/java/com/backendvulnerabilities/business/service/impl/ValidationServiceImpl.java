package com.backendvulnerabilities.business.service.impl;

import com.backendvulnerabilities.business.entity.Attendee;
import com.backendvulnerabilities.business.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Service
public class ValidationServiceImpl implements ValidationService {

    private Logger LOG = LoggerFactory.getLogger(ValidationServiceImpl.class);

    private String evilRegex = "^([a-zA-Z0-9])(([\\-.]|[_]+)?([a-zA-Z0-9]+))*(@){1}[a-z0-9]+[.]{1}(([a-z]{2,3})|" +
            "([a-z]{2,3}[.]{1}[a-z]{2,3}))$";

    private Pattern pattern = Pattern.compile(evilRegex);

    @Override
    public void validate(Attendee attendee) {
        LOG.info("Before email validation");
        boolean isValid  = true;
        long start = System.currentTimeMillis();
        if(!StringUtils.isEmpty(attendee.getEmail()) && !pattern.matcher(attendee.getEmail()).matches()){
            isValid = false;
        }
        long end = System.currentTimeMillis();
        LOG.info(String.format("Validation finished after: %d second(s)", (end - start)/1000));
        if(!isValid){
            throw new IllegalArgumentException("Email is not valid");
        }
    }
}

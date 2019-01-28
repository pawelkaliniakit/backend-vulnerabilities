package com.backendvulnerabilities.business.service;

import com.backendvulnerabilities.business.entity.Attendee;

public interface ValidationService {

    void validate(Attendee email);
}

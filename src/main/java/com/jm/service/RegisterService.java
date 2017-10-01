package com.jm.service;

import com.jm.domain.PhilipsHueBridge;
import com.jm.domain.PhilipsHueBridgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    @Autowired
    private PhilipsHueBridgeRepository philipsHueBridgeRepository;

    public PhilipsHueBridge saveConnection(PhilipsHueBridge bridge) {
        return philipsHueBridgeRepository.save(bridge);
    }
}

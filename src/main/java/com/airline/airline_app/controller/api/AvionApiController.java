package com.airline.airline_app.controller.api;

import com.airline.airline_app.entity.AvionClasseConfiguration;
import com.airline.airline_app.service.AvionClasseConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avions")
@RequiredArgsConstructor
public class AvionApiController {
    
    private final AvionClasseConfigurationService configurationService;
    
    @GetMapping("/{id}/configurations")
    public List<AvionClasseConfiguration> getConfigurations(@PathVariable Long id) {
        return configurationService.findByAvionId(id);
    }
}

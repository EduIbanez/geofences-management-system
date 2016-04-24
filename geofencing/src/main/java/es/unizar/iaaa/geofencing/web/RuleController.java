package es.unizar.iaaa.geofencing.web;

import es.unizar.iaaa.geofencing.domain.Rule;
import es.unizar.iaaa.geofencing.repository.RuleRepository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class RuleController {

    @Autowired
    private RuleRepository ruleRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleController.class);

    /**
     * This method creates a new rule. The rule types are ENTERING, LEAVING and INSIDE.
     * If the rule type is null, not specified or unknown, it will be ENTERING type by default.
     *
     * @param rule data of the rule
     * @return the rule created
     */
    @RequestMapping(path = "/api/rules", method = RequestMethod.POST)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Rule created",
                    responseHeaders = @ResponseHeader(name = "Location", description = "Location",
                            response = URI.class), response = Rule.class)})
    public ResponseEntity<Rule> createRule(@RequestBody final Rule rule) {
        LOGGER.info("Requested /api/rules POST method");
        rule.setId(null);
        Rule ruleCreated = ruleRepository.save(rule);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(rule.getId()).toUri());
        return new ResponseEntity<>(ruleCreated, httpHeaders, HttpStatus.CREATED);
    }

}
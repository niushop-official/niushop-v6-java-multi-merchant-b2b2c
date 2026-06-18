package com.niu.core.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "upload")
public class UploadConfig {
    private Map<String, Rule> rules;

    public Map<String, Rule> getRules() {
        return rules;
    }

    public void setRules(Map<String, Rule> rules) {
        this.rules = rules;
    }

    @Data
    public static class Rule {
        private List<String> ext;
        private Long size;
        private Map<String, Map<String, Integer>> thumb_type;
        // getters / setters
    }
}

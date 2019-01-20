package com.gitlab.jactor.rises.web.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class HomePageDto {

    private final List<String> paragraphs;
    private final List<Technology> technologies;

    public HomePageDto(List<String> paragraphs, List<Technology> technologies) {
        this.paragraphs = paragraphs;
        this.technologies = technologies;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public List<Technology> getTechnologies() {
        return technologies;
    }

    public static class Technology {
        private final String message;
        private final String tech;
        private final String url;

        public Technology(String tech, String url, String message) {
            this.tech = tech;
            this.url = url;
            this.message = message;
        }

        public @Override String toString() {
            return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(getTech()).append(getUrl()).append(getMessage()).toString();
        }

        public String getMessage() {
            return message;
        }

        public String getTech() {
            return tech;
        }

        public String getUrl() {
            return url;
        }
    }
}

package com.gitlab.jactor.rises.web.controller;

import com.gitlab.jactor.rises.web.JactorWeb;
import com.gitlab.jactor.rises.web.dto.HomePageDto;
import com.gitlab.jactor.rises.web.i18n.MyMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {JactorWeb.class})
@PropertySource("classpath:application.properties")
@DisplayName("The HomeController")
class HomeControllerTest {

    private MockMvc mockMvc;
    private @Autowired MyMessages myMessages;
    private @Value("${spring.mvc.view.prefix}") String prefix;
    private @Value("${spring.mvc.view.suffix}") String suffix;

    @BeforeEach void mockMvcWithViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix(prefix);
        internalResourceViewResolver.setSuffix(suffix);

        mockMvc = standaloneSetup(new HomeController(myMessages))
                .setViewResolvers(internalResourceViewResolver)
                .build();
    }

    @DisplayName("should create a homepage dto with my messages")
    @Test void shouldCreateHomepageDtoWithMyMessages() throws Exception {
        Map<String, Object> model = mockMvc.perform(
                get("/home")).andExpect(status().isOk()
        ).andReturn().getModelAndView().getModel();

        assertAll(
                () -> assertThat(model.get("homepage")).as("homepage").isNotNull(),
                () -> {
                    HomePageDto homePageDto = (HomePageDto) model.get("homepage");
                    assertAll(
                            () -> assertThat(homePageDto.getParagraphs()).as("paragraphs").hasSize(3),
                            () -> assertThat(homePageDto.getTechnologies()).as("technologies").hasSize(8)
                    );
                }
        );
    }
}
package com.gitlab.jactor.rises.web.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MyMessages {

    private final MessageSource messageSource;

    public @Autowired MyMessages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String fetchMessage(String code) {
        return messageSource.getMessage(code, new Object[]{}, LocaleContextHolder.getLocale());
    }
}

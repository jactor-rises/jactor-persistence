package com.gitlab.jactor.rises.web.mvc;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class LanguageManager {
    public static final String IS_ENGLISH = "isEnglish";
    public static final String IS_NORWEGIAN = "isNorwegian";
    public static final String IS_THAI = "isThai";
    public static final String LANG = "lang";

    public void putAllLanguageAttributes(Map<String, Object> model) {
        Locale localeForCurrentThread = LocaleContextHolder.getLocale();

        boolean isEnglish = !isLanguage(localeForCurrentThread, "no") && !isLanguage(localeForCurrentThread, "th");

        model.put(IS_ENGLISH, isEnglish);
        model.put(IS_NORWEGIAN, isLanguage(localeForCurrentThread, "no"));
        model.put(IS_THAI, isLanguage(localeForCurrentThread, "th"));

        model.put(LANG, isEnglish ? "en" : localeForCurrentThread.getLanguage());
    }

    private boolean isLanguage(Locale locale, String language) {
        return locale.equals(new Locale(language));
    }
}

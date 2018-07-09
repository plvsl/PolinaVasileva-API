package tests;

import core.YandexSpellerApi;
import core.YandexSpellerResponse;
import enums.Formats;
import enums.Languages;
import enums.Options;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static core.YandexSpellerApi.YANDEX_SPELLER_API_URI;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class TestYandexSpellerJSON {
    private final String EXAMPLE = "mailerr";
    private final String[] EXAMPLE_ARR = new String[]{"mailer", "mail err", "mail error"};
    //SOME TESTS FAILING DUE TO BUGS

    @Test
    public void sendValidGetRequestWithSuggestions() {
        YandexSpellerApi.with()
                .text(EXAMPLE)
                .callApi()
                .then()
                .specification(YandexSpellerApi
                        .successResponse()
                        .body(Matchers
                                .stringContainsInOrder(Arrays.asList(EXAMPLE_ARR))));
    }

    @Test
    public void sendValidGetRequestWithSuggestionsWithSettingLanguage() {
        YandexSpellerApi.with()
                .language(Languages.EN)
                .text(EXAMPLE)
                .callApi()
                .then()
                .specification(YandexSpellerApi
                        .successResponse()
                        .body(Matchers
                                .stringContainsInOrder(Arrays.asList(EXAMPLE_ARR))));
    }

    @Test
    public void sendInvalidGetRequestWithSuggestionsWithSettingInvalidLanguage() {
        YandexSpellerApi.with()
                .language(Languages.NON_VALID)
                .text(EXAMPLE)
                .callApi()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void sendValidGetRequestWithSuggestionsWithSettingNotCorrectLanguage() {
        YandexSpellerApi.with()
                .language(Languages.RU)
                .text(EXAMPLE)
                .callApi()
                .then()
                .specification(YandexSpellerApi
                        .successResponse()
                        .body(Matchers.equalTo("[]")));
    }

    @Test
    public void sendValidGetRequestWithSuggestionsWithSettingAllOptionsSetDefault() {
        YandexSpellerApi.with()
                .language(Languages.EN)
                .options(Options.DEFAULT)
                .text(EXAMPLE)
                .format(Formats.PLAIN)
                .callApi()
                .then()
                .specification(YandexSpellerApi
                        .successResponse()
                        .body(Matchers
                                .stringContainsInOrder(Arrays.asList(EXAMPLE_ARR))));
    }

    @Test
    public void sendValidGetRequestWithSuggestionsWithSettingAllOptions() {
        YandexSpellerApi.with()
                .language(Languages.EN)
                .options(Options.IGNORE_DIGITS, Options.IGNORE_URLS, Options.FIND_REPEAT_WORDS, Options.IGNORE_CAPITALIZATION)
                .text("maIleR ma32ilerrMA32ilerrhttps://github.com")
                .format(Formats.HTML)
                .callApi()
                .then()
                .specification(YandexSpellerApi
                        .successResponse()
                        .body(Matchers
                                .stringContainsInOrder(Arrays.asList(EXAMPLE_ARR))));
    }

    @Test
    public void sendValidRequestWithUrlWithoutOptions() {
        YandexSpellerApi
                .with()
                .text("this url" + YANDEX_SPELLER_API_URI + "should havesuggestions")
                .callApi().then().body(
                        Matchers.allOf(
                            Matchers.containsString("urlhttps"),
                            Matchers.containsString("url https"),
                            Matchers.containsString("spellservice"),
                            Matchers.containsString("spell service"),
                            Matchers.containsString("checkTextshould"),
                            Matchers.containsString("check Text should"),
                            Matchers.containsString("havesuggestions"),
                            Matchers.containsString("have suggestions")));
    }

    @Test
    public void sendValidGetRequestWithSuggestionsWithSettingIgnoreDigitOption() {
        YandexSpellerApi.with()
                .language(Languages.EN)
                .options(Options.IGNORE_DIGITS)
                .text("mail31err")
                .callApi()
                .then()
                .specification(YandexSpellerApi
                        .successResponse()
                        .body(Matchers.equalTo("[]")));
    }

    @Test
    public void sendValidGetRequestWithSuggestionsWithSettingIgnoreCaseOption() {
        YandexSpellerApi.with()
                .language(Languages.EN)
                .options(Options.IGNORE_CAPITALIZATION)
                .text("moscowa")
                .callApi()
                .then()
                .specification(YandexSpellerApi
                        .successResponse()
                        .body(Matchers
                                .stringContainsInOrder(Arrays.asList("moscow", "moscow a", "moscow's"))));
    }

    @Test
    public void sendValidGetRequestWithSuggestionsAboutCapitalCaseWithoutSettingIgnoreCaseOption() {
        YandexSpellerApi.with()
                .language(Languages.RU)
                .text("москва")
                .callApi()
                .then()
                .specification(YandexSpellerApi
                        .successResponse())
                .body(Matchers
                        .containsString("Москва"));
    }
}

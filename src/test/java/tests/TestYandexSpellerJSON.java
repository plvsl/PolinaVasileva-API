package tests;

import core.YandexSpellerApi;
import core.YandexSpellerResponse;
import enums.Formats;
import enums.Languages;
import enums.Options;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static core.YandexSpellerApi.YANDEX_SPELLER_API_URI;
import static enums.Parameters.LANGUAGE;
import static enums.Parameters.TEXT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class TestYandexSpellerJSON {
    private final String EXAMPLE = "mailerr";
    private final String[] EXAMPLE_ARR = new String[]{"mailer", "mail err", "mail error"};

    //------------------------------------WITHOUT BUILDER---------------------------------------------
    //SOME TESTS FAILING DUE TO BUGS

    @Test
    public void sendValidGetRequestWithSuggestions() {
        RestAssured
                .given()
                .param(TEXT.parameter, EXAMPLE)
                .when()
                .get(YANDEX_SPELLER_API_URI)
                .prettyPeek()
                .then()
                .specification(YandexSpellerApi.successResponse())
                .assertThat()
                .body(Matchers
                        .stringContainsInOrder(Arrays.asList(EXAMPLE_ARR)));
    }

    @Test
    public void sendValidGetRequestWithSuggestionsWithSettingLanguage() {
        RestAssured
                .given()
                .param(TEXT.parameter, EXAMPLE)
                .param(LANGUAGE.parameter, Languages.EN)
                .when()
                .get(YANDEX_SPELLER_API_URI)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(Matchers
                        .stringContainsInOrder(Arrays.asList(EXAMPLE_ARR)));
    }

    @Test
    public void sendInvalidGetRequestWithSuggestionsWithSettingInvalidLanguage() {
        RestAssured
                .given()
                .param(TEXT.parameter, EXAMPLE)
                .param(LANGUAGE.parameter, "ch")
                .when()
                .get(YANDEX_SPELLER_API_URI)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void sendValidGetRequestWithSuggestionsWithSettingNotCorrectLanguage() {
        RestAssured
                .given()
                .param(TEXT.parameter, EXAMPLE)
                .param(LANGUAGE.parameter, Languages.RU)
                .when()
                .get(YANDEX_SPELLER_API_URI)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(Matchers.equalTo("[]"));
    }

    //------------------------------------WITH BUILDER---------------------------------------------

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
        List<YandexSpellerResponse> response =
                YandexSpellerApi
                        .getYandexSpellerResponse(
                                YandexSpellerApi
                                        .with()
                                        .text("this url" + YANDEX_SPELLER_API_URI + "should havesuggestions")
                                        .callApi());
        assertThat(response.size(), equalTo(4));
        assertThat(response.get(0).s.size(), equalTo(1));
        assertThat(response.get(1).s.size(), equalTo(4));
        assertThat(response.get(2).s.size(), equalTo(1));
        assertThat(response.get(3).s.size(), equalTo(1));
        assertThat(response.get(0).word, equalTo("urlhttps"));
        assertThat(response.get(0).s.get(0), equalTo("url https"));
        assertThat(response.get(1).word, equalTo("spellservice"));
        assertThat(response.get(1).s.get(1), equalTo("spell service"));
        assertThat(response.get(2).word, equalTo("checkTextshould"));
        assertThat(response.get(2).s.get(0), equalTo("check Text should"));
        assertThat(response.get(3).word, equalTo("havesuggestions"));
        assertThat(response.get(3).s.get(0), equalTo("have suggestions"));
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
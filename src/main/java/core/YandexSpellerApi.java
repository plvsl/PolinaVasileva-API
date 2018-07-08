package core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.Formats;
import enums.Languages;
import enums.Options;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static enums.Parameters.*;
import static org.hamcrest.Matchers.lessThan;

public class YandexSpellerApi {
    private Map<String, String> params = new HashMap<>();
    public static final String YANDEX_SPELLER_API_URI = "https://speller.yandex.net/services/spellservice.json/checkText";

    private YandexSpellerApi() {
    }

    public static class ApiBuilder {
        YandexSpellerApi spellerApi;

        private ApiBuilder(YandexSpellerApi gcApi) {
            spellerApi = gcApi;
        }

        public ApiBuilder text(String text) {
            spellerApi.params.put(TEXT.parameter, text);
            return this;
        }

        public ApiBuilder options(Options... options) {
            int resultOptions = 0;
            for (Options option : options) {
                resultOptions += option.option;
            }
            String optionsS = String.valueOf(resultOptions);
            spellerApi.params.put(OPTIONS.parameter, optionsS);
            return this;
        }

        public ApiBuilder language(Languages language) {
            spellerApi.params.put(LANGUAGE.parameter, language.languageLocale);
            return this;
        }

        public ApiBuilder format(Formats format) {
            spellerApi.params.put(FORMAT.parameter, format.format);
            return this;
        }

        public Response callApi() {
            return RestAssured.with()
                    .queryParams(spellerApi.params)
                    .log().all()
                    .get(YANDEX_SPELLER_API_URI).prettyPeek();
        }
    }

    public static ApiBuilder with() {
        YandexSpellerApi api = new YandexSpellerApi();
        return new ApiBuilder(api);
    }

    public static List<YandexSpellerResponse> getYandexSpellerResponse(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<List<YandexSpellerResponse>>() {
        }.getType());
    }

    public static ResponseSpecification successResponse() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectHeader("Connection", "keep-alive")
                .expectResponseTime(lessThan(20000L))
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }
}

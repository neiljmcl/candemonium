import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class CandemoniumServerTest {
    @Rule
    public WireMockRule candemonium = new WireMockRule(wireMockConfig().port(9092));

    private CadmiumResponseBuilder aCadmiumRequest;
    private CadmiumResponseBuilder aCadmiumResponse;

    @Before
    public void setup() {
        aCadmiumRequest = new CadmiumResponseBuilder()
                .withRegistration("ML04SXT")
                .withFeatures("Warp drive");
        aCadmiumResponse = new CadmiumResponseBuilder();
        candemonium.stubFor(post(anyUrl())
                .willReturn(
                        aResponse()
                        .withStatus(200)
                        .withBody(aCadmiumResponse.build())));
    }


    @Test
    public void cadmiumRequest_exactJsonMatch() throws Exception {
        Unirest.post("http://localhost:9092/")
                .body(aCadmiumRequest.build())
                .asJson();
        candemonium.verify(postRequestedFor(urlEqualTo("/"))
                .withRequestBody(equalToJson(aCadmiumRequest
                        .withRegistration("ML04SXT")
                        .withFeatures("Warp drive")
                        .build())));
    }

    @Test
    public void cadmiumRequest_jsonPathMatch() throws Exception {
        Unirest.post("http://localhost:9092/")
                .body(aCadmiumRequest.build())
                .asJson();
        candemonium.verify(postRequestedFor(urlEqualTo("/"))
                .withRequestBody(matchingJsonPath("$[?(@.registration == \"ML04SXT\")]"))
                .withRequestBody(matchingJsonPath("$.features"))
        );
    }

    @Test
    public void cadmiumRequest_withCorrectRegistration_passesTheTest() throws Exception {
        Unirest.post("http://localhost:9092/")
                .body(aCadmiumRequest.build())
                .asJson();
        candemonium.verify(postRequestedFor(urlEqualTo("/"))
                .withRequestBody(new CadmiumRequestPatternBuilder()
                        .withRegistration("ML04SXT")
                        .build()));
    }

    @Test(expected = AssertionError.class)
    public void cadmiumRequest_withIncorrectRegistration_throwsAssertionError() throws Exception {
        Unirest.post("http://localhost:9092/")
                .body(aCadmiumRequest.build())
                .asJson();
        candemonium.verify(postRequestedFor(urlEqualTo("/"))
                .withRequestBody(new CadmiumRequestPatternBuilder()
                        .withRegistration("ML05SXT")
                        .build()));
    }

    @Test
    public void cadmiumRequest_withCorrectFeatures_verifiesRequest() throws Exception {
        Unirest.post("http://localhost:9092/")
                .body(aCadmiumRequest.build())
                .asJson();
        candemonium.verify(postRequestedFor(urlEqualTo("/"))
                .withRequestBody(new CadmiumRequestPatternBuilder()
                        .withRegistration("ML04SXT")
                        .withFeatures("Warp drive")
                        .build()));
    }

    @Test(expected = AssertionError.class)
    public void cadmiumResponse_withIncorrectFeatures_throwsAssertionError() throws Exception {
        Unirest.post("http://localhost:9092/")
                .body(aCadmiumRequest.build())
                .asJson();
        candemonium.verify(postRequestedFor(urlEqualTo("/"))
                .withRequestBody(new CadmiumRequestPatternBuilder()
                        .withRegistration("ML04SXT")
                        .withFeatures("transphasic torpedos")
                        .build()));
    }
}

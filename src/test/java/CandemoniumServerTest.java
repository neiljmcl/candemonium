import com.github.tomakehurst.wiremock.junit.WireMockRule;
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
    public void cadmiumResponse_fuzzyMatchOnRegistration() throws Exception {
        HttpResponse<JsonNode> response = Unirest.post("http://localhost:9092/")
                .body(aCadmiumRequest.build())
                .asJson();
        candemonium.verify(postRequestedFor(urlEqualTo("/"))
                .withRequestBody(containing("ML04SXT")));
    }

    @Test
    public void cadmiumResponse_exactJsonMatch() throws Exception {
        HttpResponse<JsonNode> response = Unirest.post("http://localhost:9092/")
                .body(aCadmiumRequest.build())
                .asJson();
        candemonium.verify(postRequestedFor(urlEqualTo("/"))
                .withRequestBody(equalToJson(aCadmiumRequest
                        .withRegistration("ML04SXT")
                        .withFeatures("Warp drive")
                        .build())));
    }

    @Test
    public void cadmiumResponse_jsonPathMatch() throws Exception {
        HttpResponse<JsonNode> response = Unirest.post("http://localhost:9092/")
                .body(aCadmiumRequest.build())
                .asJson();
        candemonium.verify(postRequestedFor(urlEqualTo("/"))
                .withRequestBody(matchingJsonPath("$.registration")));
    }

    @Ignore
    @Test
    public void cadmiumResponse_someKindOfJsonMatcher() throws Exception {
        HttpResponse<JsonNode> response = Unirest.post("http://localhost:9092/")
                .body(aCadmiumRequest.build())
                .asJson();
        ContractMatcher anIncomingResponse = new ContractMatcher();
        candemonium.verify(postRequestedFor(urlEqualTo("/"))
                .withRequestBody(JsonValueMatcher.matches(anIncomingResponse
                        .withRegistration("ML04SZT"))));
    }
}

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by neiljmcl on 23/07/2016.
 */
public class CandemoniumServerTest {
    @Rule
    public WireMockRule candemonium = new WireMockRule(wireMockConfig().port(9092));

    @Before
    public void setup() {
        candemonium.stubFor(get(urlEqualTo("/"))
                .willReturn(
                        aResponse()
                        .withStatus(200)
                        .withBody("bananas")
                ));
    }

    @Test
    public void status() throws Exception {
        HttpResponse<String> response = Unirest.get("http://localhost:9092/").asString();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void body() throws Exception {
        HttpResponse<String> response = Unirest.get("http://localhost:9092/").asString();
        assertThat(response.getBody()).isEqualTo("bananas");
    }
}

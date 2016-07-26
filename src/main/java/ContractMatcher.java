import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by neiljmcl on 24/07/2016.
 */
public class ContractMatcher {

    private String registration;

    public ContractMatcher withRegistration(String registration) {
        this.registration = registration;
        return this;
    }

    public static StringValuePattern matchingRegistration(String value) {
        return null;
    }

    public void match(String json) {
        String registration = JsonPath.parse(json).read("$.registration");


        assertThat(registration).isEqualTo(this.registration);
    }

    private static class MyMatcher extends StringValuePattern {
        private final String jsonPath;

        public MyMatcher(String expectedValue, String jsonPath) {
            super(expectedValue);
            this.jsonPath = jsonPath;
        }

        public MatchResult match(String json) {
            JsonPath.parse(json).read(jsonPath);
            return null;
        }
    }
}

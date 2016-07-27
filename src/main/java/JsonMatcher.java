import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.jayway.jsonpath.JsonPath;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;
import static org.assertj.core.api.Assertions.assertThat;


public class JsonMatcher extends StringValuePattern {
    public JsonMatcher(String expectedValue) {
        super(expectedValue);
    }

    public MatchResult match(String value) {
        return MatchResult.of(this.isJsonPathMatch(value));
    }

    private boolean isJsonPathMatch(String value) {
        try {
            String registration = JsonPath.parse(value).read("$.registration");
            assertThat(registration).isEqualTo("ML04SXT");
        } catch (AssertionError e) {
            notifier().info(String.format("Match failed: %s%n", e.getMessage()));
            return false;
        } catch (Exception e) {
            String error;
            if (e.getMessage().equalsIgnoreCase("invalid path")) {
                error = "the JSON path didn't match the document structure";
            } else if (e.getMessage().equalsIgnoreCase("invalid container object")) {
                error = "the JSON document couldn't be parsed";
            } else {
                error = "of error '" + e.getMessage() + "'";
            }

            String message = String.format(
                    "Warning: JSON path expression '%s' failed to match document '%s' because %s",
                    expectedValue, value, error);
            notifier().info(message);
            return false;
        }

        return true;
    }
}


import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.jayway.jsonpath.JsonPath;
import org.assertj.core.api.Condition;
import org.assertj.core.api.SoftAssertions;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;
import static org.assertj.core.api.Assertions.assertThat;

public class CadmiumRequestPatternBuilder {
    SoftAssertions softly;
    public CadmiumRequestPatternBuilder() {
        softly = new SoftAssertions();
    }

    public StringValuePattern build() {
        return new CadmiumRequestMatch("Whose woods these are I think I know");
    }
    public CadmiumRequestPatternBuilder withRegistration(String registration) {
        // "$.registration", registration,
        // Function jsonString -> actualValue; expectedValue;
        // function(jsonString) -> Condition



        return this;
    }

    public CadmiumRequestPatternBuilder withFeatures(String... features) {
        return this;
    }


    private class CadmiumRequestMatch extends StringValuePattern {

        public CadmiumRequestMatch(String expectedValue) {
            // This is what will be in the failed match message.
            super(expectedValue);
        }

        public MatchResult match(String value) {
            return MatchResult.of(doJsonPathsMatch(value));
        }

        private boolean doJsonPathsMatch(String value) {
            try {
                String registration = JsonPath.parse(value).read("$.registration");
                softly.assertThat(registration).isEqualTo("ML04SXT");

                softly.assertAll();
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
}

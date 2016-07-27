import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.assertj.core.api.Condition;
import org.assertj.core.api.SoftAssertions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;
import static org.assertj.core.api.Assertions.assertThat;

public class CadmiumRequestPatternBuilder {
    private final List<Condition<DocumentContext>> conditions;
    public CadmiumRequestPatternBuilder() {
        conditions = new ArrayList<>();
    }

    public StringValuePattern build() {
        return new CadmiumRequestMatch(conditions);
    }
    public CadmiumRequestPatternBuilder withRegistration(final String registration) {
        conditions.add(new Condition<>(ctx -> {
            return registration.equals(ctx.read("$.registration"));
        }, "Failed to find registration: " + registration));

        return this;
    }


    public CadmiumRequestPatternBuilder withFeatures(String... features) {
        return this;
    }


    private class CadmiumRequestMatch extends StringValuePattern {
        private final List<Condition<DocumentContext>> conditions;

        public CadmiumRequestMatch(List<Condition<DocumentContext>> conditions) {
            super(conditions.stream().map(c -> c.toString()).collect(Collectors.joining(" ")));
            this.conditions = conditions;
        }

        public MatchResult match(String value) {
            return MatchResult.of(validateFields(value));
        }

        private boolean validateFields(String json) {
            try {
                DocumentContext documentContext = JsonPath.parse(json);
                SoftAssertions softly = new SoftAssertions();
                conditions.forEach(c -> softly.assertThat(documentContext).is(c));
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
                        expectedValue, json, error);
                notifier().info(message);
                return false;
            }
            return true;
        }
    }
}

import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;
import static org.assertj.core.api.Assertions.assertThat;

public class CadmiumRequestPatternBuilder {
    private final List<LazyAssertion> conditions;
    public CadmiumRequestPatternBuilder() {
        conditions = new ArrayList<>();
    }

    public StringValuePattern build() {
        return new CadmiumRequestMatch(conditions);
    }
    public CadmiumRequestPatternBuilder withRegistration(final String registration) {
        Consumer<DocumentContext> assertion = ctx -> {
            assertThat((String) ctx.read("$.registration")).isEqualTo(registration);
        };
        String message = String.format("Expecting registration: <%s>", registration);

        conditions.add(new LazyAssertion(assertion, message));
        return this;
    }

    public CadmiumRequestPatternBuilder withFeatures(String... features) {
        Consumer<DocumentContext> assertion = ctx -> {
            assertThat((List<String>) ctx.read("$.features")).containsExactly(features);
        };
        String message = String.format("Expecting features containing all of: <%s>",
                Arrays.asList(features).stream().collect(Collectors.joining(", ")));

        conditions.add(new LazyAssertion(assertion, message));
        return this;
    }

    private static String expectedDifferences(List<LazyAssertion> lazyAssertions) {
        return lazyAssertions.stream()
                .map(a -> a.toString())
                .collect(Collectors.joining(System.getProperty("line.separator")));
    }

    private class CadmiumRequestMatch extends StringValuePattern {
        private final List<LazyAssertion> lazyAssertions;
        public CadmiumRequestMatch(List<LazyAssertion> lazyAssertions) {
            super(expectedDifferences(lazyAssertions));
            this.lazyAssertions = lazyAssertions;
        }

        public MatchResult match(String value) {
            return MatchResult.of(validateFields(value));
        }
        private boolean validateFields(String json) {
            try {
                DocumentContext documentContext = JsonPath.parse(json);
                lazyAssertions.forEach(c -> c.apply(documentContext));
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

    private class LazyAssertion {
        private final Consumer<DocumentContext> anAssertion;
        private final String message;

        public LazyAssertion(Consumer<DocumentContext> anAssertion, String message) {
            this.anAssertion = anAssertion;
            this.message = message;
        }
        public void apply(DocumentContext documentContext) {
            this.anAssertion.accept(documentContext);
        }

        @Override
        public String toString() {
            return message;
        }
    }

}

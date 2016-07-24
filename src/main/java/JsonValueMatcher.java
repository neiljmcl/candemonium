import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;

/**
 * Created by neiljmcl on 24/07/2016.
 */
public class JsonValueMatcher extends StringValuePattern {

    public JsonValueMatcher(String expectedValue) {
        super(expectedValue);
    }

    public MatchResult match(String value) {
        return null;
    }

    public static JsonValueMatcher matches(String expectedValue) {
        return new JsonValueMatcher(expectedValue);
    }
}

import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;


/**
 * Created by neiljmcl on 24/07/2016.
 */
public class JsonValueMatcher extends StringValuePattern {

    private final ContractMatcher contractMatcher;

    public JsonValueMatcher(ContractMatcher contractMatcher) {
        super("I don't care");
        this.contractMatcher = contractMatcher;
    }

    public MatchResult match(String value) {
        try {
            contractMatcher.match(value);
        } catch (AssertionError e) {
            return MatchResult.of(false);
        }
        return MatchResult.of(true);
    }

    public static JsonValueMatcher matches(ContractMatcher contractMatcher) {
        return new JsonValueMatcher(contractMatcher);
    }
}

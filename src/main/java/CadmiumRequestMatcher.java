import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RequestMatcher;

public class CadmiumRequestMatcher extends RequestMatcher {
    public MatchResult match(Request value) {
        return MatchResult.of(false);
    }


    public String getName() {
        return "cadmium-request-matcher";
    }
}

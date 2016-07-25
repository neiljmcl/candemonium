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

    public void match(String json) {
        String registration = JsonPath.parse(json).read("$.registration");


        assertThat(registration).isEqualTo(this.registration);
    }
}

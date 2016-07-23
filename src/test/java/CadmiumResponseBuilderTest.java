import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CadmiumResponseBuilderTest {
    private CadmiumResponseBuilder builder;
    @Before
    public void setup() {
        builder = new CadmiumResponseBuilder();
    }

    @Test
    public void withNothing_returnsEmptyJsonDocument() {
        assertThat(builder.build()).isEqualTo("{}");
    }

    @Test
    public void withRegistrationOnly() {
        assertThat(builder.withRegistration("ML04SXT").build()).isEqualTo("{\"registration\":\"ML04SXT\"}");
    }

}

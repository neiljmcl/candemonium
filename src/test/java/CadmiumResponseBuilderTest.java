import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class CadmiumResponseBuilderTest {
    private CadmiumResponseBuilder builder;
    @Before
    public void setup() {
        builder = new CadmiumResponseBuilder();
    }

    @Test
    public void withNothing_returnsEmptyJsonDocument() throws Exception {
        JSONAssert.assertEquals(resourceAsString("empty_request.json"), builder.build(), true);
    }

    @Test
    public void withRegistrationOnly() throws Exception {
        JSONAssert.assertEquals(resourceAsString("with_registration_only.json"), builder.withRegistration("ML04SXT").build(), true);
    }

    @Test
    public void withFeaturesOnly() throws Exception {
        JSONAssert.assertEquals(resourceAsString("with_features_only.json"),
                builder.withFeatures("steering wheel", "deuterium reactor", "warp drive").build(), true);
    }

    private String resourceAsString(String name) {

        try {
            URL resource = getClass().getResource(name);
            if (resource == null) {
                throw new FileNotFoundException(String.format("No resource found named '%s'", name));
            }

            return FileUtils.readFileToString(new File(resource.getFile()), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

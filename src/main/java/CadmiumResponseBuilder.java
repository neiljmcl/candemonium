import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by neiljmcl on 23/07/2016.
 */
public class CadmiumResponseBuilder {
    private final ObjectNode rootNode;
    ObjectMapper objectMapper;
    public CadmiumResponseBuilder() {
        this.objectMapper = new ObjectMapper();
        rootNode = objectMapper.createObjectNode();
    }

    public CadmiumResponseBuilder withRegistration(String registration) {
        rootNode.put("registration", registration);
        return this;
    }

    public String build() {
        try {
            return objectMapper.writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.printf("%s%n", new CadmiumResponseBuilder().build());
    }
}

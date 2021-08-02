package input;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
public class CoffeeMachineJsonInput {
    private Machine machine;

    public static CoffeeMachineJsonInput from(String json) throws IOException {
        return new ObjectMapper().readValue(json, CoffeeMachineJsonInput.class);
    }
}

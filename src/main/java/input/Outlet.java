package input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Outlet {

    @JsonProperty("count_n")
    private int count;
}

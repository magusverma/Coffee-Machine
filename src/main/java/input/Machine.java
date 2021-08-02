package input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class Machine {
    private Outlet outlets;

    @JsonProperty("total_items_quantity")
    private HashMap<String, Integer> ingredientQuantityMap;

    @JsonProperty("beverages")
    private HashMap<String, HashMap<String, Integer>> beverages;
}

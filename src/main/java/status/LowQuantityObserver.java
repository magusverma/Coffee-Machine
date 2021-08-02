package status;

import java.util.ArrayList;
import java.util.List;

public class LowQuantityObserver implements IngredientObserver{
    int QUANTITY_THRESHOLD = 10;
    public List<String> logs = new ArrayList<>();

    @Override
    public void notify(String ingredient, int quantity) {
        if(quantity < QUANTITY_THRESHOLD) {
            logs.add("Ingredient "+ingredient+" Running low in quantity");
            System.out.println(logs.get(logs.size() - 1));
        }
    }
}

package ingredient_manager;

import com.google.common.base.Optional;
import status.IngredientObserver;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface IngredientManager {
    /**
     * @param ingredients
     *   Atomically Block ingredients for the passed quantity if everything is possible
     * @return Pair of Boolean and String
     *   Boolean: Block Step was success/failure (true/false).
     *   String: Log Line stating which exact ingredient and for what reason wasn't available
     */
    abstract AbstractMap.SimpleEntry<Boolean, Optional<String>> blockIngredients(HashMap<String, Integer> ingredients);

    void registerObserver(IngredientObserver observer);

    void reportIngredientQuantityUpdate(String ingredient, int quantity);
}

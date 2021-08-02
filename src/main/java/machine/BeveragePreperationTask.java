package machine;

import com.google.common.base.Optional;
import ingredient_manager.IngredientManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maker.DrinkMaker;

import java.util.AbstractMap;
import java.util.HashMap;

@Getter
@AllArgsConstructor
public class BeveragePreperationTask implements Runnable {
    private IngredientManager ingredientManager;
    private DrinkMaker drinkMaker;
    private String name;
    private HashMap<String, Integer> ingredients;

    @Override
    public void run() {
        AbstractMap.SimpleEntry<Boolean, Optional<String>> ingredientStatus = ingredientManager.blockIngredients(ingredients);
        if (ingredientStatus.getKey()) {
            try {
                drinkMaker.prepare(name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            drinkMaker.renderError(name + " cannot be prepared because "+ingredientStatus.getValue().get());
        }
    }
}
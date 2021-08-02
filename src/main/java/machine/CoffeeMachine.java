package machine;

import ingredient_manager.IngredientManager;
import maker.DrinkMaker;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoffeeMachine {
    private IngredientManager ingredientManager;
    private DrinkMaker drinkMaker;
    private ExecutorService executor;

    public CoffeeMachine(final IngredientManager ingredientManager,
                         final DrinkMaker drinkMaker,
                         int taps) throws IOException {
        this.ingredientManager = ingredientManager;
        this.drinkMaker = drinkMaker;
        this.executor = Executors.newFixedThreadPool(taps);
    }

    public void makeDrink(String name, HashMap<String, Integer> ingredients) {
        BeveragePreperationTask task = new BeveragePreperationTask(ingredientManager, drinkMaker, name, ingredients);
        executor.execute(task);
    }
}

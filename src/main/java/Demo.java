import ingredient_manager.ParallelIngredientManager;
import input.CoffeeMachineJsonInput;
import machine.CoffeeMachine;
import maker.DrinkMakerSysOut;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Demo {
    public static void main(String[] args) throws IOException {
        CoffeeMachineJsonInput input = getCoffeeMachineJsonInput("input.json");

        ParallelIngredientManager ingredientManager = new ParallelIngredientManager();
        for(Map.Entry<String, Integer> ingredient : input.getMachine().getIngredientQuantityMap().entrySet()) {
            ingredientManager.addIngredient(ingredient.getKey(), ingredient.getValue());
        }

        CoffeeMachine coffeeMachine = new CoffeeMachine(ingredientManager,
                new DrinkMakerSysOut(), input.getMachine().getOutlets().getCount());

        for(Map.Entry<String, HashMap<String, Integer>> beverage : input.getMachine().getBeverages().entrySet()) {
            coffeeMachine.makeDrink(beverage.getKey(), beverage.getValue());
        }
    }

    private static CoffeeMachineJsonInput getCoffeeMachineJsonInput(String filePath) throws IOException {
        String fullFilePath = CoffeeMachineJsonInput.class.getClassLoader().getResource(filePath).getFile();
        File file = new File(fullFilePath);
        String jsonInput = FileUtils.readFileToString(file, "UTF-8");
        CoffeeMachineJsonInput input = CoffeeMachineJsonInput.from(jsonInput);
        return input;
    }
}

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ingredient_manager.ParallelIngredientManager;
import input.CoffeeMachineJsonInput;
import machine.CoffeeMachine;
import maker.DrinkNameInterceptor;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import status.IngredientObserver;
import status.LowQuantityObserver;

public class CoffeeMachineTest {
    @org.junit.Test
    public void testInputRead() throws Exception {
        String jsonInput = readFileContent("input.json");
        System.out.println("Read Content:"+ jsonInput);
        CoffeeMachineJsonInput input = CoffeeMachineJsonInput.from(jsonInput);
        Assert.assertEquals(Optional.of(3), Optional.ofNullable(input.getMachine().getOutlets().getCount()));
        Assert.assertEquals(Optional.of(500), Optional.ofNullable(input.getMachine().getIngredientQuantityMap().get("hot_water")));
        Assert.assertEquals(Optional.of(100), java.util.Optional.ofNullable(input.getMachine().getBeverages().get("hot_coffee").get("hot_water")));
        System.out.println("Input Read Succesfully");
    }

    private String readFileContent(String filePath) throws IOException {
        String fullFilePath = CoffeeMachineJsonInput.class.getClassLoader().getResource(filePath).getFile();
        System.out.println("Reading From File : "+ fullFilePath);
        File file = new File(fullFilePath);
        String jsonInput = FileUtils.readFileToString(file, "UTF-8");
        return jsonInput;
    }

    @org.junit.Test
    public void testParallelProcessing() throws Exception {
        boolean success = false;
        for (int tryRange = 0; tryRange < 3; tryRange++) {
            CoffeeMachineJsonInput input = CoffeeMachineJsonInput.from(readFileContent("parallel_processable_input.json"));
            ParallelIngredientManager ingredientManager = new ParallelIngredientManager();
            for(Map.Entry<String, Integer> ingredient : input.getMachine().getIngredientQuantityMap().entrySet()) {
                ingredientManager.addIngredient(ingredient.getKey(), ingredient.getValue());
            }
            DrinkNameInterceptor drinkNameInterceptor = new DrinkNameInterceptor();
            CoffeeMachine coffeeMachine = new CoffeeMachine(ingredientManager, drinkNameInterceptor, input.getMachine().getOutlets().getCount());
            for(Map.Entry<String, HashMap<String, Integer>> beverage : input.getMachine().getBeverages().entrySet()) {
                coffeeMachine.makeDrink(beverage.getKey(), beverage.getValue());
            }
            Thread.sleep(120);
            if(drinkNameInterceptor.vendHistory.size() == 2) {
                success = true;
                break;
            }    
        }
        Assert.assertTrue(success);
    }

    @org.junit.Test
    public void testDeadlockHandling() throws Exception {
        CoffeeMachineJsonInput input = CoffeeMachineJsonInput.from(readFileContent("potential_deadlock_input.json"));
        ParallelIngredientManager ingredientManager = new ParallelIngredientManager();
        for(Map.Entry<String, Integer> ingredient : input.getMachine().getIngredientQuantityMap().entrySet()) {
            ingredientManager.addIngredient(ingredient.getKey(), ingredient.getValue());
        }
        DrinkNameInterceptor drinkNameInterceptor = new DrinkNameInterceptor();
        CoffeeMachine coffeeMachine = new CoffeeMachine(ingredientManager, drinkNameInterceptor, input.getMachine().getOutlets().getCount());
        for(Map.Entry<String, HashMap<String, Integer>> beverage : input.getMachine().getBeverages().entrySet()) {
            coffeeMachine.makeDrink(beverage.getKey(), beverage.getValue());
        }
        Thread.sleep(180);
        Assert.assertEquals(1, drinkNameInterceptor.vendHistory.size());
        Thread.sleep(180);
        Assert.assertEquals(1, drinkNameInterceptor.errorMessages.size());
        Assert.assertEquals(true,
                drinkNameInterceptor.errorMessages.contains("Y cannot be prepared because A is not sufficient") ||
                drinkNameInterceptor.errorMessages.contains("X cannot be prepared because A is not sufficient"));
    }

    @org.junit.Test
    public void testLowQuantity() throws Exception {
        CoffeeMachineJsonInput input = CoffeeMachineJsonInput.from(readFileContent("potential_deadlock_input.json"));
        ParallelIngredientManager ingredientManager = new ParallelIngredientManager();
        for(Map.Entry<String, Integer> ingredient : input.getMachine().getIngredientQuantityMap().entrySet()) {
            ingredientManager.addIngredient(ingredient.getKey(), ingredient.getValue());
        }
        DrinkNameInterceptor drinkNameInterceptor = new DrinkNameInterceptor();
        CoffeeMachine coffeeMachine = new CoffeeMachine(ingredientManager, drinkNameInterceptor, input.getMachine().getOutlets().getCount());
        for(Map.Entry<String, HashMap<String, Integer>> beverage : input.getMachine().getBeverages().entrySet()) {
            coffeeMachine.makeDrink(beverage.getKey(), beverage.getValue());
        }
        Thread.sleep(300);
        Assert.assertEquals(1, drinkNameInterceptor.errorMessages.size());
        Assert.assertEquals(true,
                drinkNameInterceptor.errorMessages.contains("Y cannot be prepared because A is not sufficient") ||
                        drinkNameInterceptor.errorMessages.contains("X cannot be prepared because A is not sufficient"));
    }

    @org.junit.Test
    public void testQuantityObserver() throws Exception {
        CoffeeMachineJsonInput input = CoffeeMachineJsonInput.from(readFileContent("potential_deadlock_input.json"));
        ParallelIngredientManager ingredientManager = new ParallelIngredientManager();
        for(Map.Entry<String, Integer> ingredient : input.getMachine().getIngredientQuantityMap().entrySet()) {
            ingredientManager.addIngredient(ingredient.getKey(), ingredient.getValue());
        }
        LowQuantityObserver observer = new LowQuantityObserver();
        ingredientManager.registerObserver(observer);
        DrinkNameInterceptor drinkNameInterceptor = new DrinkNameInterceptor();
        CoffeeMachine coffeeMachine = new CoffeeMachine(ingredientManager, drinkNameInterceptor, input.getMachine().getOutlets().getCount());
        for(Map.Entry<String, HashMap<String, Integer>> beverage : input.getMachine().getBeverages().entrySet()) {
            coffeeMachine.makeDrink(beverage.getKey(), beverage.getValue());
        }
        Thread.sleep(300);
        Assert.assertEquals(2, observer.logs.size());
        Assert.assertEquals(true, observer.logs.contains("Ingredient A Running low in quantity"));
        Assert.assertEquals(true, observer.logs.contains("Ingredient B Running low in quantity"));
    }


}

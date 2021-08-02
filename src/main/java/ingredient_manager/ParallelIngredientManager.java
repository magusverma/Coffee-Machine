package ingredient_manager;

import com.google.common.base.Optional;
import ingredient_manager.IngredientManager;
import status.IngredientObserver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ParallelIngredientManager implements IngredientManager {
    private ConcurrentHashMap<String, ReentrantLock> inventoryLocks = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();
    private List<IngredientObserver> observers = new ArrayList<>();

    public void addIngredient(String ingredient, int quantity) {
        inventory.putIfAbsent(ingredient, new AtomicInteger());
        inventoryLocks.putIfAbsent(ingredient, new ReentrantLock());
        inventory.get(ingredient).addAndGet(quantity);
        reportIngredientQuantityUpdate(ingredient, inventory.get(ingredient).get());
    }

    @Override
    public AbstractMap.SimpleEntry<Boolean, Optional<String>> blockIngredients(HashMap<String, Integer> ingredients) {
        for(String ingredient : ingredients.keySet()) {
            if (!inventory.containsKey(ingredient)) {
                return new AbstractMap.SimpleEntry<>(Boolean.FALSE, Optional.of(ingredient + " is not available"));
            }
        }
        List<String> sortedKeys = new ArrayList<>(ingredients.size());
        sortedKeys.addAll(ingredients.keySet());
        Collections.sort(sortedKeys);
        List<String> acquiredLocks = new ArrayList<>();
        for (String ingredient : sortedKeys) {
            inventoryLocks.get(ingredient).lock();
            acquiredLocks.add(ingredient);
            if (inventory.get(ingredient).get() < ingredients.get(ingredient)) {
                unlock(acquiredLocks);
                return new AbstractMap.SimpleEntry<>(Boolean.FALSE, Optional.of(ingredient + " is not sufficient"));
            }
        }
        AbstractMap.SimpleEntry<Boolean, Optional<String>> response = deductQuantities(ingredients);
        unlock(acquiredLocks);
        return response;
    }

    @Override
    public void registerObserver(IngredientObserver observer) {
        observers.add(observer);
    }

    @Override
    public void reportIngredientQuantityUpdate(String ingredient, int quantity) {
        for(IngredientObserver observer : observers) {
            observer.notify(ingredient, quantity);
        }
    }

    private void unlock(List<String> acquiredLocks) {
        for (String ingredient : acquiredLocks) {
            inventoryLocks.get(ingredient).unlock();
        }
    }

    private AbstractMap.SimpleEntry<Boolean, Optional<String>> deductQuantities(HashMap<String, Integer> ingredients) {
        for(Map.Entry<String, Integer> ingredient : ingredients.entrySet()) {
            inventory.get(ingredient.getKey()).addAndGet(-1*ingredient.getValue());
            reportIngredientQuantityUpdate(ingredient.getKey(), inventory.get(ingredient.getKey()).get());
        }
        return new AbstractMap.SimpleEntry<>(Boolean.TRUE, Optional.absent());
    }

}

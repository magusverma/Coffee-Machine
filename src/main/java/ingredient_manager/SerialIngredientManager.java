package ingredient_manager;

import com.google.common.base.Optional;
import lombok.NoArgsConstructor;
import status.IngredientObserver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public class SerialIngredientManager implements IngredientManager {
    private List<IngredientObserver> observers = new ArrayList<>();

    public ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    public void addIngredient(String ingredient, int quantity) {
        inventory.putIfAbsent(ingredient, new AtomicInteger());
        inventory.get(ingredient).addAndGet(quantity);
    }

    public synchronized AbstractMap.SimpleEntry<Boolean, Optional<String>> blockIngredients(HashMap<String, Integer> ingredients) {
        for(Map.Entry<String, Integer> ingredient : ingredients.entrySet()) {
            if (!inventory.containsKey(ingredient.getKey())) {
                return new AbstractMap.SimpleEntry<>(Boolean.FALSE, Optional.of(ingredient.getKey()+" is not available"));
            }
            if (inventory.get(ingredient.getKey()).get() < ingredient.getValue()) {
                return new AbstractMap.SimpleEntry<>(Boolean.FALSE, Optional.of(ingredient.getKey()+" is not sufficient"));
            }
        }
        for(Map.Entry<String, Integer> ingredient : ingredients.entrySet()) {
            inventory.getOrDefault(ingredient.getKey(), new AtomicInteger()).addAndGet(-1*ingredient.getValue());
        }
        return new AbstractMap.SimpleEntry<>(Boolean.TRUE, Optional.absent());
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
}

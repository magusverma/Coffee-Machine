package maker;

import maker.DrinkMaker;

import java.util.HashSet;
import java.util.Set;

public class DrinkNameInterceptor implements DrinkMaker {
    public Set<String> vendHistory = new HashSet<>();
    public Set<String> errorMessages = new HashSet<>();

    @Override
    public void prepare(String name) throws InterruptedException {
        Thread.sleep(100);
        vendHistory.add(name);
    }

    @Override
    public void renderError(String s) {
        errorMessages.add(s);
    }
}

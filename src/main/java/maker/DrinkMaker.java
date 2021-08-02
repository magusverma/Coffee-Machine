package maker;

public interface DrinkMaker {
    public void prepare(String name) throws InterruptedException;

    public void renderError(String s);
}

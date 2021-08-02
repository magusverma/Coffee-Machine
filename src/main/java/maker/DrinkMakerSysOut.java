package maker;

import maker.DrinkMaker;

public class DrinkMakerSysOut implements DrinkMaker {
    @Override
    public void prepare(String name) {
        System.out.println(name + " is prepared");
    }

    @Override
    public void renderError(String s) {
        System.out.println(s);
    }
}

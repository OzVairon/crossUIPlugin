package com.ozv.crossUI;

/**
 * Created by ozvairon on 20.05.17.
 */
public class TestClass {

    public static void main(String[] args) {
        new TestClass().someMethod();
    }

    void someMethod() {
        String res = System.getProperty("user.home");

        System.out.println(res);
    }

}

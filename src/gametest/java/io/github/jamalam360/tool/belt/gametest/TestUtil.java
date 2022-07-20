package io.github.jamalam360.tool.belt.gametest;

import net.minecraft.client.option.KeyBind;
import net.minecraft.test.GameTestException;

/**
 * @author Jamalam
 */
public class TestUtil {
    public static void pressKeyBind(KeyBind keyBind) {
        // Key will always be default on gametest.
        KeyBind.onKeyPressed(keyBind.getDefaultKey());
    }

    public static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new GameTestException("Expected: " + expected + ", actual: " + actual);
        }
    }
}

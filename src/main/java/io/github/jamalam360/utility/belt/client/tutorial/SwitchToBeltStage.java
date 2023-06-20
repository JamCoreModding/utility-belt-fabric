package io.github.jamalam360.utility.belt.client.tutorial;

import io.github.jamalam360.tutorial.lib.stage.Stage;
import net.minecraft.client.toast.TutorialToast;

public class SwitchToBeltStage extends Stage {

    private final Type type;

    public SwitchToBeltStage(Type type, TutorialToast toast) {
        super(toast);
        this.type = type;
    }

    public boolean shouldTrigger(Type type) {
        return this.type == type;
    }

    public enum Type {
        HOLD,
        TOGGLE,
        GUI,
        INSERT_PICKAXE,
    }
}

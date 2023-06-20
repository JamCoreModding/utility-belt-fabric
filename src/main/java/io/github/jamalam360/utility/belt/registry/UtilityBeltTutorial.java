package io.github.jamalam360.utility.belt.registry;

import io.github.jamalam360.tutorial.lib.CustomTutorialToast;
import io.github.jamalam360.tutorial.lib.Tutorial;
import io.github.jamalam360.tutorial.lib.TutorialLib;
import io.github.jamalam360.tutorial.lib.stage.DelayedStage;
import io.github.jamalam360.tutorial.lib.stage.EquipItemStage;
import io.github.jamalam360.tutorial.lib.stage.ObtainItemStage;
import io.github.jamalam360.utility.belt.UtilityBeltInit;
import io.github.jamalam360.utility.belt.client.tutorial.MineBlockUsingPickaxeInBeltStage;
import io.github.jamalam360.utility.belt.client.tutorial.SwitchToBeltStage;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class UtilityBeltTutorial {

    public static final Identifier TEXTURE = UtilityBeltInit.idOf("textures/tutorial.png");
    //TODO: textures then we should be done ish?
    // Should probably do a full survival playtest too
    public static final Tutorial TUTORIAL = new Tutorial(
          new ObtainItemStage(
                ItemRegistry.UTILITY_BELT,
                new CustomTutorialToast(
                      TEXTURE,
                      0,
                      0,
                      Text.translatable("tutorial.utilitybelt.1.title"),
                      Text.translatable("tutorial.utilitybelt.1.description")
                )
          ),
          new EquipItemStage(
                ItemRegistry.UTILITY_BELT,
                new CustomTutorialToast(
                      TEXTURE,
                      20,
                      0,
                      Text.translatable("tutorial.utilitybelt.2.title"),
                      Text.translatable("tutorial.utilitybelt.2.description")
                )
          ),
          new SwitchToBeltStage(
                SwitchToBeltStage.Type.TOGGLE,
                new CustomTutorialToast(
                      TEXTURE,
                      40,
                      0,
                      Text.translatable("tutorial.utilitybelt.3.title"),
                      Text.translatable("tutorial.utilitybelt.3.description")
                )
          ),
          new SwitchToBeltStage(
                SwitchToBeltStage.Type.HOLD,
                new CustomTutorialToast(
                      TEXTURE,
                      60,
                      0,
                      Text.translatable("tutorial.utilitybelt.4.title"),
                      Text.translatable("tutorial.utilitybelt.4.description")
                )
          ),
          new SwitchToBeltStage(
                SwitchToBeltStage.Type.GUI,
                new CustomTutorialToast(
                      TEXTURE,
                      80,
                      0,
                      Text.translatable("tutorial.utilitybelt.5.title"),
                      Text.translatable("tutorial.utilitybelt.5.description")
                )
          ),
          new SwitchToBeltStage(
                SwitchToBeltStage.Type.INSERT_PICKAXE,
                new CustomTutorialToast(
                      TEXTURE,
                      100,
                      0,
                      Text.translatable("tutorial.utilitybelt.6.title"),
                      Text.translatable("tutorial.utilitybelt.6.description")
                )
          ),
          new MineBlockUsingPickaxeInBeltStage(
                new CustomTutorialToast(
                      TEXTURE,
                      120,
                      0,
                      Text.translatable("tutorial.utilitybelt.7.title")
                )
          ),
          new DelayedStage(
                new CustomTutorialToast(
                      TEXTURE,
                      140,
                      0,
                      Text.translatable("tutorial.utilitybelt.8.title"),
                      Text.translatable("tutorial.utilitybelt.8.description")
                ),
                200
          )
    );

    public static void registerTutorial() {
        Registry.register(TutorialLib.TUTORIAL_REGISTRY, UtilityBeltInit.idOf("tutorial"), TUTORIAL);
    }
}

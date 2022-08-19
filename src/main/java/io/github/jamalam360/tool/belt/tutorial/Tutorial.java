package io.github.jamalam360.tool.belt.tutorial;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.Toast;
import net.minecraft.text.Text;
import net.minecraft.text.component.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jamalam
 */
public class Tutorial {
    public static boolean active = false;
    private static List<Toast> toasts;
    private static int toastIndex;
    private static int ticksSinceLastToastSent;

    public static void startTutorial() {
        active = true;
        toasts = getToasts();
        toastIndex = 0;
        ticksSinceLastToastSent = 60;
    }

    public static void tick(MinecraftClient client) {
        if (active) {
            if (ticksSinceLastToastSent > 140) {
                ticksSinceLastToastSent = 0;
                if (toastIndex < toasts.size()) {
                    client.getToastManager().add(toasts.get(toastIndex));
                    toastIndex++;
                } else {
                    active = false;
                    toasts = null;
                }
            }

            ticksSinceLastToastSent++;
        }
    }

    private static List<Toast> getToasts() {
        List<Toast> toasts = new ArrayList<>();
        String titleBaseKey = "text.toolbelt.tutorial.title.";
        String descriptionBaseKey = "text.toolbelt.tutorial.description.";
        int current = 1;

        while (I18n.hasTranslation(titleBaseKey + current)) {
            String description = "";

            if (I18n.hasTranslation(descriptionBaseKey + current)) {
                description = descriptionBaseKey + current;
            }

            toasts.add(new ToolBeltTutorialToast(Text.translatable(titleBaseKey + current), description.equals("") ? null : Text.translatable(description)));
            current++;
        }

        return toasts;
    }
}

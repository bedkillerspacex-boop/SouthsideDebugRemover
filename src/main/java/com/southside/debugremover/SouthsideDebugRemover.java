package com.southside.debugremover;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SouthsideDebugRemover implements ClientModInitializer {
    public static final String MOD_ID = "southside_debug_remover";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding toggleBlockerKey;
    private static KeyBinding toggleDebugKey;
    private static KeyBinding openConfigGuiKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing SouthsideDebugRemover Client...");
        
        // Load configuration
        SDRConfig.load();

        // Register keybindings
        toggleBlockerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.southside_debug_remover.toggle_blocker",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            "category.southside_debug_remover"
        ));

        toggleDebugKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.southside_debug_remover.toggle_debug",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F9,
            "category.southside_debug_remover"
        ));

        openConfigGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.southside_debug_remover.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.southside_debug_remover"
        ));

        // Register tick event to handle keypresses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (toggleBlockerKey.wasPressed()) {
                SDRConfig.blockerEnabled = !SDRConfig.blockerEnabled;
                SDRConfig.save();
                client.player.sendMessage(Text.literal("§6[SDR]§e 拦截功能已" + (SDRConfig.blockerEnabled ? "§a开启" : "§c关闭")), false);
            }

            while (toggleDebugKey.wasPressed()) {
                SDRConfig.debugEnabled = !SDRConfig.debugEnabled;
                SDRConfig.save();
                client.player.sendMessage(Text.literal("§6[SDR]§e 拦截提示已" + (SDRConfig.debugEnabled ? "§a开启" : "§c关闭")), false);
            }

            while (openConfigGuiKey.wasPressed()) {
                client.setScreen(new SDRConfigScreen(null));
            }
        });
    }
}

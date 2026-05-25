package com.southside.debugremover.mixin;

import com.southside.debugremover.SDRConfig;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Shadow
    public abstract void addMessage(Text message);

    private static final ThreadLocal<Boolean> SDR_PROCESSING = ThreadLocal.withInitial(() -> false);

    @Inject(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onAddMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
        // Prevent infinite reentrancy
        if (SDR_PROCESSING.get()) {
            return;
        }

        // Only run interception if blocker is enabled
        if (!SDRConfig.blockerEnabled) {
            return;
        }

        String plainText = message.getString();
        
        // Check if the message matches any of the filter rules
        boolean matchesFilter = false;
        for (String rule : SDRConfig.getFilterRulesLines()) {
            if (rule != null && !rule.trim().isEmpty() && plainText.startsWith(rule)) {
                matchesFilter = true;
                break;
            }
        }

        if (matchesFilter) {
            // Check whitelist lines (allow dynamic whitelist items)
            for (String rule : SDRConfig.getWhitelistLines()) {
                if (rule != null && !rule.trim().isEmpty() && plainText.contains(rule)) {
                    return; // Whitelisted, bypass the blocker
                }
            }

            ci.cancel();

            // Only print [SDR] log if debug display is enabled
            if (SDRConfig.debugEnabled) {
                SDR_PROCESSING.set(true);
                try {
                    Text notification = Text.literal("§6[SDR]§e 拦截一个调试信息，内容: §f" + plainText);
                    this.addMessage(notification);
                } finally {
                    SDR_PROCESSING.set(false);
                }
            }
        }
    }
}

package com.southside.debugremover;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class SDRConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget prefixField;

    public SDRConfigScreen(Screen parent) {
        super(Text.literal("SDR Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        // Row 1: 启用拦截, 调试提示
        ButtonWidget blockerToggle = ButtonWidget.builder(
            Text.literal("启用拦截: " + (SDRConfig.blockerEnabled ? "开" : "关")),
            button -> {
                SDRConfig.blockerEnabled = !SDRConfig.blockerEnabled;
                button.setMessage(Text.literal("启用拦截: " + (SDRConfig.blockerEnabled ? "开" : "关")));
            }
        ).dimensions(centerX - 155, 110, 150, 20).build();
        this.addDrawableChild(blockerToggle);

        ButtonWidget debugToggle = ButtonWidget.builder(
            Text.literal("调试提示: " + (SDRConfig.debugEnabled ? "开" : "关")),
            button -> {
                SDRConfig.debugEnabled = !SDRConfig.debugEnabled;
                button.setMessage(Text.literal("调试提示: " + (SDRConfig.debugEnabled ? "开" : "关")));
            }
        ).dimensions(centerX + 5, 110, 150, 20).build();
        this.addDrawableChild(debugToggle);

        // Row 2: 拦截前缀输入框
        this.prefixField = new TextFieldWidget(
            this.textRenderer,
            centerX - 75, 160, 150, 20,
            Text.literal("拦截前缀")
        );
        this.prefixField.setText(SDRConfig.filterPrefix);
        this.prefixField.setMaxLength(32);
        this.addDrawableChild(this.prefixField);

        // Row 3: 保存 & 取消
        ButtonWidget saveButton = ButtonWidget.builder(
            Text.literal("保存"),
            button -> {
                // Save custom prefix
                SDRConfig.filterPrefix = this.prefixField.getText();

                // Save configurations
                SDRConfig.save();

                if (this.client != null && this.client.player != null) {
                    this.client.player.sendMessage(Text.literal("§6[SDR]§a 配置已成功保存！"), false);
                }
                this.close();
            }
        ).dimensions(centerX - 155, 210, 150, 20).build();
        this.addDrawableChild(saveButton);

        ButtonWidget cancelButton = ButtonWidget.builder(
            Text.literal("取消"),
            button -> this.close()
        ).dimensions(centerX + 5, 210, 150, 20).build();
        this.addDrawableChild(cancelButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;

        // Render Titles
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("SouthsideDebugRemover Settings"), centerX, 60, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("拦截设置与调试过滤"), centerX, 80, 0xAAAAAA);

        // Render Textbox Label
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("拦截前缀 (Filter Prefix):"), centerX, 145, 0xFFFFFF);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}

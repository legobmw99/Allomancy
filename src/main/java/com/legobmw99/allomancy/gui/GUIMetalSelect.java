/**
 * This class was modified from one created by <Vazkii>. The original is
 * distributed as part of the Psi Mod.
 * This code is used under the
 * Psi License: http://psi.vazkii.us/license.php
 * 
 * The code was used as a template for the circular GUI,
 * and was heavily modified
 *
 */
package com.legobmw99.allomancy.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import com.google.common.collect.ImmutableSet;
import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.AllomancyUtils;
import com.legobmw99.allomancy.util.Registry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class GUIMetalSelect extends GuiScreen {

	public static final String[] metalNames = { "Iron", "Steel", "Tin", "Pewter", "Zinc", "Brass", "Copper", "Bronze" };
	public static final String GUI_METAL = "allomancy:textures/gui/metals/sign%d.png";

	private static final ResourceLocation[] metals = new ResourceLocation[] {
			new ResourceLocation(String.format(GUI_METAL, 0)), new ResourceLocation(String.format(GUI_METAL, 1)),
			new ResourceLocation(String.format(GUI_METAL, 2)), new ResourceLocation(String.format(GUI_METAL, 3)),
			new ResourceLocation(String.format(GUI_METAL, 4)), new ResourceLocation(String.format(GUI_METAL, 5)),
			new ResourceLocation(String.format(GUI_METAL, 6)), new ResourceLocation(String.format(GUI_METAL, 7)), };

	int timeIn = AllomancyConfig.animateSelection ? 0 : 10; // Config setting for whether the wheel animates open or instantly appears
	int slotSelected = -1;
	AllomancyCapability cap;
	List<Integer> slots;

	public GUIMetalSelect() {
		EntityPlayerSP player;
		player = Minecraft.getMinecraft().player;
		cap = AllomancyCapability.forPlayer(player);

		slots = new ArrayList();
		for (int i = 0; i < 8; i++) {
			slots.add(i);
		}
	}

	@Override
	public void drawScreen(int mx, int my, float partialTicks) {
		super.drawScreen(mx, my, partialTicks);

		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();

		int x = width / 2;
		int y = height / 2;
		int maxRadius = 80;

		boolean mouseIn = true;
		float angle = mouseAngle(x, y, mx, my);

		int highlight = 5;

		GlStateManager.enableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		int segments = slots.size();
		float totalDeg = 0;
		float degPer = 360F / segments;

		List<int[]> stringPositions = new ArrayList();

		slotSelected = -1;

		for (int seg = 0; seg < segments; seg++) {
			boolean mouseInSector = mouseIn && angle > totalDeg && angle < totalDeg + degPer;
			float radius = Math.max(0F, Math.min((timeIn + partialTicks - seg * 6F / segments) * 40F, maxRadius));

			GL11.glBegin(GL11.GL_TRIANGLE_FAN);

			float gs = 0.3F;

			if (seg % 2 == 1)
				gs += 0.25F;

			gs = cap.getMetalAmounts((seg + 4) % 8) == 0 ? 0 : gs;

			float r = cap.getMetalBurning((seg + 4) % 8) ? 1.0F : gs;
			float g = gs;
			float b = gs;
			float a = 0.6F;
			if (mouseInSector) {
				slotSelected = seg;
			}

			GlStateManager.color(r, g, b, a);
			GL11.glVertex2i(x, y);

			for (float i = degPer; i >= 0; i--) {
				float rad = (float) ((i + totalDeg) / 180F * Math.PI);
				double xp = x + Math.cos(rad) * radius;
				double yp = y + Math.sin(rad) * radius;
				if (i == (int) (degPer / 2)) {
					stringPositions.add(new int[] { seg, (int) xp, (int) yp, mouseInSector ? 'n' : 'r' });
					stringPositions.add(
							new int[] { seg, (int) xp, (int) yp, cap.getMetalAmounts((seg + 4) % 8) == 0 ? '7' : 'f' }); // Mark unused ones as disabled
				}
				GL11.glVertex2d(xp, yp);
			}
			totalDeg += degPer;

			GL11.glVertex2i(x, y);
			GL11.glEnd();

			if (mouseInSector)
				radius -= highlight;
		}
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableTexture2D();

		for (int[] pos : stringPositions) {
			int slot = slots.get(pos[0]);
			int xp = pos[1];
			int yp = pos[2];
			char c = (char) pos[3];

			int xsp = xp - 4;
			int ysp = yp;
			String name = "\u00a7" + c + metalNames[(slot + 4) % 8];
			// add four and mod by eight to get #1 where I want it to be
			int width = fontRenderer.getStringWidth(name);

			double mod = 0.6;
			int xdp = (int) ((xp - x) * mod + x);
			int ydp = (int) ((yp - y) * mod + y);

			if (xsp < x)
				xsp -= width - 8;
			if (ysp < y)
				ysp -= 9;

			fontRenderer.drawStringWithShadow(name, xsp, ysp, 0xFFFFFF);

			mod = 0.8;
			xdp = (int) ((xp - x) * mod + x);
			ydp = (int) ((yp - y) * mod + y);

			mc.renderEngine.bindTexture(metals[(slot + 4) % 8]);
			drawModalRectWithCustomSizedTexture(xdp - 8, ydp - 8, 0, 0, 16, 16, 16, 16);

		}
		float stime = 5F;
		float fract = Math.min(stime, timeIn + partialTicks) / stime;
		float s = 3F * fract;
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		RenderHelper.enableGUIStandardItemLighting();

		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableBlend();
		GlStateManager.disableRescaleNormal();

		GlStateManager.popMatrix();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		toggleSelected();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (!GameSettings.isKeyDown(Registry.burn)) {
			mc.displayGuiScreen(null); // toggleSelected(); //probably not necessary to change it on exit anymore
		}

		ImmutableSet<KeyBinding> set = ImmutableSet.of(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindLeft,
				mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindSneak,
				mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindJump);
		for (KeyBinding k : set)
			KeyBinding.setKeyBindState(k.getKeyCode(), GameSettings.isKeyDown(k));

		timeIn++;
	}

	/**
	 * Toggles the metal the mouse is currently over
	 */
	private void toggleSelected() {
		if (slotSelected != -1) {
			int slot = slots.get(slotSelected);
			slot = (slot + 4) % 8; // Make the slot the one I actually want
			AllomancyUtils.toggleMetalBurn(slot, cap);
			Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("ui.button.click")), 0.1F,
					2.0F);
		}

	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private static float mouseAngle(int x, int y, int mx, int my) {
		Vector2f baseVec = new Vector2f(1F, 0F);
		Vector2f mouseVec = new Vector2f(mx - x, my - y);

		float ang = (float) (Math.acos(Vector2f.dot(baseVec, mouseVec) / (baseVec.length() * mouseVec.length()))
				* (180F / Math.PI));
		return my < y ? 360F - ang : ang;
	}
}
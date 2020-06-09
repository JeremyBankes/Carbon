package com.jeremy.carbon;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class Main implements NativeKeyListener {

	private boolean capturing = false;
	private HashSet<DragWindow> windows = new HashSet<>();

	@Override
	public void nativeKeyPressed(NativeKeyEvent event) {
		if (event.getKeyCode() == NativeKeyEvent.VC_S && event.getModifiers() == 3) {
			setCapturing(true);
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent event) {}

	@Override
	public void nativeKeyTyped(NativeKeyEvent event) {}

	private void openDragWindows() {
		for (GraphicsDevice screen : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			try {
				DragWindow window = new DragWindow(this, screen);
				window.setVisible(true);
				windows.add(window);
			} catch (AWTException exception) {
				exception.printStackTrace();
			}
		}
	}

	private void closeDragWindows() {
		windows.forEach(window -> window.dispose());
		windows.clear();
	}

	public boolean isCapturing() {
		return capturing;
	}

	public void setCapturing(boolean capturing) {
		this.capturing = capturing;
		if (capturing) openDragWindows();
		else closeDragWindows();
	}

	public static void main(String[] args) {
		try {
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.WARNING);
			logger.setUseParentHandlers(false);

			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(new Main());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}

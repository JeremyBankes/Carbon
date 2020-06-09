package com.jeremy.carbon;

import static java.lang.Math.*;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.net.URI;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class DragWindow extends JDialog implements KeyListener, MouseListener, MouseMotionListener, ClipboardOwner {

	private static final long serialVersionUID = 1L;

	private Main main;

	private Robot robot;

	private Point down = new Point();
	private Rectangle selection = new Rectangle(0, 0, 0, 0);

	private Color selectionColor;

	public DragWindow(Main main, GraphicsDevice screen) throws AWTException {
		this.main = main;
		setUndecorated(true);

		setBackground(new Color(0.0f, 0.0f, 0.0f, 0.1f));

		final GraphicsConfiguration configuration = screen.getDefaultConfiguration();
		setBounds(configuration.getBounds());

		robot = new Robot(screen);

		addKeyListener(this);
		getContentPane().addMouseListener(this);
		getContentPane().addMouseMotionListener(this);

		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		selectionColor = new Color(1.0f, 1.0f, 1.0f, 0.60f);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(selectionColor);
		g.fillRect(selection.x, selection.y, selection.width, selection.height);
	}

	private void complete(BufferedImage image) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new TransferableImage(image), this);
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try {
				String carbonUrl = "http://localhost:42802";
				String response = Internet.publishImage(carbonUrl, image);
				String url = null;
				if (response.startsWith("success")) {
					url = response.split(",")[1];
				} else {
					if (response.equals("no-extension")) {
						throw new Exception("No image extension specified.");
					} else if (response.equals("exists")) {
						throw new Exception("An image already exists with that name.");
					}
				}
				Desktop.getDesktop().browse(new URI(carbonUrl + "/" + url));
			} catch (Exception exception) {
				exception.printStackTrace();
				JOptionPane.showMessageDialog(null, exception.getMessage(), "Carbon Client - Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void updateSelection(MouseEvent event) {
		int x = min(event.getX(), down.x);
		int y = min(event.getY(), down.y);
		int width = abs(event.getX() - down.x);
		int height = abs(event.getY() - down.y);
		selection.setBounds(x, y, width, height);
	}

	@Override
	public void keyTyped(KeyEvent event) {}

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) main.setCapturing(false);
	}

	@Override
	public void keyReleased(KeyEvent event) {}

	@Override
	public void mouseDragged(MouseEvent event) {
		updateSelection(event);
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent event) {}

	@Override
	public void mouseClicked(MouseEvent event) {}

	@Override
	public void mousePressed(MouseEvent event) {
		down.setLocation(event.getPoint());
		selection.setLocation(down);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		updateSelection(event);
		main.setCapturing(false);
		BufferedImage image = robot.createScreenCapture(selection);
		complete(image);
	}

	@Override
	public void mouseEntered(MouseEvent event) {}

	@Override
	public void mouseExited(MouseEvent event) {}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}

}

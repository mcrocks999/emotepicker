package to.uk.paul.emotepicker;

import java.awt.AWTException;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

public class EmotePicker {

	public static JFrame frame = new JFrame("EmotePicker by Paul");
	private static int WIDTH = 450;
	private static int HEIGHT = 300;
	private static Image img;

	public static void main(String[] args) {
		GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true); // use false here to switch to hook instead of raw input

		System.out.println("Global keyboard hook successfully started, press [escape] key to shutdown. Connected keyboards:");
		for(Entry<Long,String> keyboard:GlobalKeyboardHook.listKeyboards().entrySet())
			System.out.format("%d: %s\n", keyboard.getKey(), keyboard.getValue());
		
		keyboardHook.addKeyListener(new GlobalKeyAdapter() {
			@Override public void keyPressed(GlobalKeyEvent event) {
				if (event.getVirtualKeyCode()==GlobalKeyEvent.VK_F10) {
					Point mouse = MouseInfo.getPointerInfo().getLocation();
					frame.setLocation(mouse.x, mouse.y - HEIGHT);
					frame.setVisible(true);
				}
				if (event.getVirtualKeyCode()==GlobalKeyEvent.VK_ESCAPE && frame.isVisible())
					frame.setVisible(false);
				if (event.getVirtualKeyCode()==GlobalKeyEvent.VK_INSERT && frame.isVisible())
					System.exit(0);
			}
		});
		
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setOpacity(0.7f);
		frame.setSize(WIDTH, HEIGHT);
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		frame.setLocation(mouse.x, mouse.y - HEIGHT);
		
		JScrollPane contentPane = new JScrollPane();
		contentPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.setViewportView(panel);
		panel.setLayout(new WrapLayout(FlowLayout.LEFT, 0, 0));
		
		for (final File fileEntry : new File("emotes").listFiles()) {
			try {
				BufferedImage image = ImageIO.read(new File("emotes/" + fileEntry.getName()));
				JButton emoteBtn = new JButton(new ImageIcon(image));
				emoteBtn.setBorder(BorderFactory.createEmptyBorder());
				emoteBtn.setContentAreaFilled(false);
				emoteBtn.setSize(32, 32);
				emoteBtn.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) { 
						img = Toolkit.getDefaultToolkit ().createImage("emotes/" + fileEntry.getName());
						send();
					} 
				});
				panel.add(emoteBtn);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}

	protected static void send() {
		if (img == null) return;
		ClipboardImage.write(img);
		frame.setVisible(false);
		try {
	        Robot robot = new Robot();
	        robot.keyPress(KeyEvent.VK_CONTROL);
	        robot.keyPress(KeyEvent.VK_V);
	        robot.keyRelease(KeyEvent.VK_V);
	        robot.keyRelease(KeyEvent.VK_CONTROL);
	        robot.keyPress(KeyEvent.VK_ENTER);
	        robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (AWTException e1) {
	    	e1.printStackTrace();
		}
	}

}


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

public class LocationUtil {
	JFrame frame;
	private int xx, yy;
	private boolean isDraging = false;

	public LocationUtil(JFrame f) {
		this.frame = f;
		
		frame.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
				isDraging = false;
			}
			
			public void mousePressed(MouseEvent e) {
				isDraging = true;
				xx = e.getX();
				yy = e.getY();
			}
			
			public void mouseExited(MouseEvent e) {

			}
			
			public void mouseEntered(MouseEvent e) {

			}
			
			public void mouseClicked(MouseEvent e) {
				
			}
		});

		frame.addMouseMotionListener(new MouseMotionListener() {

			public void mouseMoved(MouseEvent e) {

			}

			public void mouseDragged(MouseEvent e) {
				if (isDraging == true) {
					int left = frame.getLocation().x;
					int top = frame.getLocation().y;
					frame.setLocation(left + e.getX() - xx, top + e.getY() - yy);
				}
			}
		});
	}
}

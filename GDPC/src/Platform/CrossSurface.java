package Platform;

import GUI.GUIMain;
import Supp.Comm;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

/**
 * Klasa opakowujaca dla SurfaceView lub JPanel w zaleznosci od platformy.
 * Ma dostarczac wspolne plotno do rysowania, niezalezne od platformy.
 */
public class CrossSurface extends JPanel implements MouseListener, MouseMotionListener, KeyListener 
{
  private final GUIMain gui;

  public CrossSurface(GUIMain GUI)
  {
    gui = GUI;
		
		setFocusable(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
  }
  
	public void paint()
	{
		repaint();
	}
	
	public void changeCursor(int CursorID)
	{
		switch (CursorID)
		{
			case Comm.CURSOR_DEF:
				setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			break;
				
			case Comm.CURSOR_HAND:
				setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			break;
		
			case Comm.CURSOR_TEXT:
				setCursor (Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			break;
		}
	}

  @Override
  protected void paintComponent(Graphics gr)
  {
    super.paintComponent(gr);
    Graphics2D g = (Graphics2D) gr.create();
		CrossCanvas c = new CrossCanvas(g);
		c.gSetAntialiasing();
    gui.onDraw(c, getWidth(), getHeight());    
    
    g.dispose();
  }

	@Override
	public void mouseClicked(MouseEvent e)
	{
		gui.onMouseClick(e.getX(), e.getY());
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		gui.onMouseDown(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		gui.onMouseUp(e.getX(), e.getY());
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{}

	@Override
	public void mouseExited(MouseEvent e)
	{}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		gui.onMouseDragged(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		gui.onMouseMove(e.getX(), e.getY());
	}

	private int keyCode(int code)
	{
		switch (code)
		{
			case  37: return Comm.VK_LEFT; 
			case  38: return Comm.VK_UP;
			case  39: return Comm.VK_RIGHT;
			case  40: return Comm.VK_DOWN;
			case  10: return Comm.VK_ENTER;
			case  27: return Comm.VK_ESC;			
			case  32: return Comm.VK_SPC;			
			case   8: return Comm.VK_BCKSPC;			
			case 127: return Comm.VK_DEL;			
		}
		
		return code;
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		gui.onKeyTyped(e.getKeyChar());
	}

	@Override
	public void keyPressed(KeyEvent e)
	{	
		gui.onKeyDown(keyCode(e.getKeyCode()));
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		gui.onKeyUp(keyCode(e.getKeyCode()));
	}
  
}

package scot.davidhunter.thepurplegameengine;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

public class ThePurpleGameEngine extends Canvas implements Runnable
{
	private static final long serialVersionUID = -6641423049343361361L;
	
	private static ArrayList<ThePurpleGameEngine> INSTANCES = new ArrayList<ThePurpleGameEngine>();
	
	private Dimension size;
	private String title, windowTitle;
	private boolean running;
	
	private JFrame window;
	private Thread thread;
	
	private int fps;
	
	public ThePurpleGameEngine( int width, String title )
	{
		this( width, width / 16 * 9, title );
	}
	
	public ThePurpleGameEngine( int width, int height, String title )
	{
		this.size = new Dimension( width, height );
		this.setPreferredSize( size );
		this.setMinimumSize( size );
		this.setMaximumSize( size );
		this.title = title;
		this.windowTitle = title;
		
		this.initWindow();
		this.start();
	}
	
	private void initWindow()
	{
		window = new JFrame();
		window.add( this );
		window.pack();
		window.setResizable( false );
		window.setLocationRelativeTo( null );
		window.addWindowListener( new WindowAdapter()
		{
			public void windowClosing( WindowEvent e )
			{
				stop();
			}
		} );
		window.setTitle( windowTitle );
		window.setVisible( true );
		window.requestFocus();
		this.requestFocus();
	}
	
	public void start()
	{
		if ( running )
			return;
		
		running = true;
		thread = new Thread( this );
		thread.start();
		
		ThePurpleGameEngine.INSTANCES.add( this );
	}
	
	public void run()
	{
		int frames = 0;
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerUpdate = 1 / 60.0;
		int updateCount = 0;
		boolean updated = false;
		
		while ( running )
		{
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;
			
			while ( unprocessedSeconds > secondsPerUpdate )
			{
				update();
				unprocessedSeconds -= secondsPerUpdate;
				updated = true;
				updateCount++;
				
				if ( updateCount % 60 == 0 )
				{
					fps = frames;
					previousTime += 1000;
					frames = 0;
				}
			}
		}
		
		if ( updated )
		{
			render();
			frames++;
		}
		
		render();
		frames++;
	}
	
	private void update()
	{
		if ( window != null )
		{
			windowTitle = title + " | " + fps + " FPS";
			window.setTitle( windowTitle );
		}
	}
	
	private void render()
	{
		
	}
	
	public void stop()
	{
		if ( ! running )
			return;
		
		running = false;
		
		try
		{
			thread.join();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		
		ThePurpleGameEngine.INSTANCES.remove( this );
		
		if ( ThePurpleGameEngine.INSTANCES.isEmpty() )
			System.exit( 0 );
	}
	
	public static void main( String[] args )
	{
		new ThePurpleGameEngine( 800, 600, "The Purple Game Engine" );
	}
}

package eu.allowensembles.utility.presentation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eu.allowensembles.utility.controller.Preferences;
import eu.allowensembles.utility.controller.Utility;

public class GraphRepresentationView extends JPanel{

	private Graphics gBuf = null;
    private BufferedImage vm = null;
    private JPanel graphPanel;
    private int x, y;
    private int w, h;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3532979404143883668L;

	GraphRepresentationView(final Utility u, final Preferences prefs){
		DefaultListModel<String> functionsListData = new DefaultListModel<String>();
		functionsListData.addElement("Travel time");
		functionsListData.addElement("Cost");
		functionsListData.addElement("Reliability");
		functionsListData.addElement("Walking distance");
		functionsListData.addElement("Security");
		functionsListData.addElement("Privacy");
		functionsListData.addElement("Number of changes");
		
		final JList<String> functionslist = new JList<String>(functionsListData);
		
		functionslist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		functionslist.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		functionslist.setVisibleRowCount(-1);
		functionslist.setBounds(50, 40, 150, 150);
		functionslist.setBorder(BorderFactory.createTitledBorder("Utility Component"));
		
		add(functionslist);
		graphPanel = new JPanel(){
		private static final long serialVersionUID = 7978056015441964714L;

		public void paint(Graphics g) {
				if (vm == null) {

		            vm = (BufferedImage)createImage(200,200);
		        }
		        gBuf = vm.getGraphics();
		        
		       gBuf.setColor(Color.LIGHT_GRAY );
		      
		        x = 20;
		        y = 20;
		        w = 200;
		        h = 200;
		        
		        for ( int d=0; d<w; d+=w/20 )
		            gBuf.drawLine( x+d, y+0, x+d, y+h );
		        for ( int d=0; d<h; d+=h/20 )
		            gBuf.drawLine( x+0, y+d, x+h, y+d );
		         
		        
		        gBuf.setColor( Color.BLACK );
		  //      gBuf.drawString("Function Graph", 10, 10);
		        gBuf.drawRect( x, y, w-x-2, h-x-2 );
		 //       gBuf.drawLine( x+w/2, y+0, x+w/2, y+h );
		  //      gBuf.drawLine( x+0, y+h/2, x+w, y+h/2 );

		       g.drawImage(vm, 0, 0, this);
	    }};
	    
	    
		graphPanel.setBounds(300, 10, 250, 300);
				
		functionslist.addListSelectionListener(new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			double x , y;
			
			gBuf.clearRect(10, 10, 200, 200);
			switch (functionslist.getSelectedIndex()){
			case 0:
				for (x = 0; x<200; x+=0.01 )
				{
					y = Math.exp((-u.getTimeConst()*x)/prefs.getTmax());
					drawPointExp(x,y);
		        }
				gBuf.setColor(Color.BLACK );
				gBuf.drawString("x",192, 188);
				gBuf.drawString("y",5 ,30);
				gBuf.drawString("0", 5, 195);
				gBuf.drawString("1", 5, 107);
			
				break;
			case 1:
				for (x = 0; x<200; x+=0.01 )
				{
		        	y = Math.exp((-u.getCostConst()*x) / prefs.getCmax());
		        	drawPointExp(x,y);
		        }
				gBuf.setColor(Color.BLACK );
				gBuf.drawString("x",192, 188);
				gBuf.drawString("y",5 ,30);
				gBuf.drawString("0", 5, 195);
				gBuf.drawString("1", 5, 107);
				break;
			case 2:
				for (x = 0; x<1; x+=0.001 )
				{
		        	y = Math.exp(-u.getReliabilityConst()*Math.pow(x/(1-x)-prefs.getCmax(), 2));
		        	drawPoint(x,y);
		        }
				gBuf.setColor(Color.BLACK );
				gBuf.drawString("x",192, 188);
				gBuf.drawString("y",5 ,30);
				gBuf.drawString("0", 5, 195);
				gBuf.drawString("1", 5, 107);
				gBuf.drawString("1", 125, 190);
				break;
			case 3:
				for (x = 0; x<2*prefs.getWmax(); x+=0.1 )
				{
		        	y = Math.exp(-u.getWalkConst()*(Math.pow(x/prefs.getWmax(), 2)));
		        	drawPointWalk(x,y,prefs.getWmax());
		        }
				gBuf.setColor(Color.BLACK );
				gBuf.drawString("x",192, 188);
				gBuf.drawString("y",5 ,30);
				gBuf.drawString("0", 5, 195);
				gBuf.drawString("1", 5, 107);
				break;
			case 4:
				for (x = 0; x<1; x+=0.01 )
				{
		        	y = Math.pow((1 - x),u.getSecurityConst());
		        	drawPoint(x,y);
		        }
				gBuf.setColor(Color.BLACK );
				gBuf.drawString("x",192, 188);
				gBuf.drawString("y",5 ,30);
				gBuf.drawString("0", 5, 195);
				gBuf.drawString("1", 5, 107);
				gBuf.drawString("1", 125, 190);
				break;
			case 5:
				for (x = 0; x<1; x+=0.01 )
				{
					y = (1 - Math.pow(x,u.getPrivacyConst()));
					drawPoint(x,y);
		        }
				gBuf.setColor(Color.BLACK );
				gBuf.drawString("x",192, 188);
				gBuf.drawString("y",5 ,30);
				gBuf.drawString("0", 5, 195);
				gBuf.drawString("1", 5, 107);
				gBuf.drawString("1", 125, 190);
				break;
			case 6:
				for (x = 0; x<=prefs.getNoCmax(); x+=0.01 )
				{
					if(x<=prefs.getNoCmax()){
						y = u.getNoCConst() * (prefs.getNoCmax() - x +1.0)/prefs.getNoCmax();
						drawPointLinear(x,y,prefs.getNoCmax());
					}
					else
					{
						y=0;
						drawPointLinear(x,y,prefs.getNoCmax());
					}
		        }
				gBuf.setColor(Color.BLACK );
				gBuf.drawString("x",192, 188);
				gBuf.drawString("y",5 ,30);
				gBuf.drawString("0", 5, 195);
				gBuf.drawString("1", 5, 107);
				break;
				default:
					break;
			}
		}
	});		
	add(functionslist);
	add(graphPanel);
		
	}
	
    public void setColor( Color c )
    {
        gBuf.setColor(c);
    }
     
    public void drawPointExp( double px, double py )
    {
        if ( px > 200 || px < 0 || py > 10 || py < -10 )
            return;
 
        px *= w/200;
        py *= h/2;
        py = h - y - py;
        gBuf.setColor(Color.RED );
        gBuf.drawLine( x+(int)px, y+(int)py, x+(int)px+1, y+(int)py+1 );
        this.repaint();
   
      
    }
    public void drawPoint( double px, double py )
    {
        if ( px > 300 || px < 0 || py > 10 || py < -10 )
            return;
 
        px *= w/2;
        py *= h/2;
        py = h - y - py;
 
        gBuf.setColor(Color.RED );
        gBuf.drawLine( x+(int)px, y+(int)py, x+(int)px+1, y+(int)py +1);
        this.repaint();
    }
    public void drawPointLinear( double px, double py, double noc )
    {
        if ( px > noc || px < 0 || py > 2 || py < 0 )
            return;
 
        px *= w*(1.0)/(noc);
        py *= h/2;
        py = h - py + y/2;
        
        gBuf.setColor(Color.RED );
        gBuf.drawLine( x+(int)px, y+(int)py, x+(int)px+1, y+(int)py+1);
        this.repaint();
    }
    public void drawPointWalk( double px, double py ,double wmax)
    {
        if ( px > 2*wmax || px < 0 || py > 1 || py < 0 )
            return;
 
        px *= (w*1.0)/(2.0*wmax);
        py *= h/2;
        py = h - y - py;
 
        gBuf.setColor(Color.RED );
        gBuf.drawLine( x+(int)px, y+(int)py, x+(int)px+1, y+(int)py +1);
        this.repaint();
    }
}

	 

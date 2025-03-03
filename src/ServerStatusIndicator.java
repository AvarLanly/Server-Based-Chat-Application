import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class ServerStatusIndicator extends JPanel {

    private boolean isServerOnline;

    public ServerStatusIndicator(boolean isServerOnline) {
        this.isServerOnline = isServerOnline;
    }

    
    
    public void setServerStatus(boolean isServerOnline) {
        this.isServerOnline = isServerOnline;
        repaint(); // Repaint the indicator when the status changes
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set the circle color based on the server status
        if (isServerOnline) {
            g.setColor(Color.GREEN);
        } 
        else {
            g.setColor(Color.RED);
        }

        // Draw a filled circle
        int diameter = Math.min(getWidth(), getHeight());
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;
        g.fillOval(x, y, diameter, diameter);
    }
}

// @author Akarsh Kiran Gowda
import javax.swing.SwingUtilities;
public class Main {
    public static void main(String[] args) {
        // Run the login screen when the application starts
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }
}

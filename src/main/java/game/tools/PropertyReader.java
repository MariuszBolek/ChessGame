package game.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
    private final static Logger logger = LoggerFactory.getLogger(PropertyReader.class);

    private final Font pieceFont = new Font("SansSerif", Font.PLAIN, 48);
    private final Properties properties = loadProperties();

    public PropertyReader() {
    }

    public Font getPieceFont() {
        return pieceFont;
    }

    public String getVersion() {
        return properties.getProperty("chess.version");
    }



    private Properties loadProperties() {
        Properties properties = new Properties();
        try {
            var inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("chess.properties");
            if (inputStream == null) {
                throw new IOException("Properties missing");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            logger.warn("Error while reading properties", e);
        }
        return properties;
    }





}

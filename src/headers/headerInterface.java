package headers;

import java.util.Vector;

public interface headerInterface {

    public String writeHeader();
    public String writeFooter();
    public String readHeader(String packet);
    public String readFooter();
}

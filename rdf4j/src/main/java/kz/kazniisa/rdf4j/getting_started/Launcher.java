package kz.kazniisa.rdf4j.getting_started;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import java.io.File;


public class Launcher {

    private static Logger logger = LoggerFactory.getLogger(Launcher.class);
    // Why This Failure marker
    private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");

    public static void main(String[] args) {
        try {
            logger.info("This is test info logging string!!");

            File dataDir = new File("D:\\rdf4j_data");
            Repository repo = new SailRepository(new NativeStore(dataDir));
            repo.init();
        } catch (Throwable t) {
            logger.error(WTF_MARKER, t.getMessage(), t);
        }
    }
}

package nu.mine.mosher.gedcom;

import java.io.File;
import java.io.IOException;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomPointeesOptions {
    public boolean help;
    public File gedcom;
    public File fringe;



    public void h() {
        help();
    }

    public void help() {
        this.help = true;
        System.err.println("Usage: java -jar gedcom-pointees-all.jar <indi.ids -g in.ged [-f finge.ids]");
        System.err.println("Finds all IDs referenced from given INDI IDs.");
        System.err.println("Optionally find INDIs not in the original set (at the fringes).");
        System.err.println("Options:");
        System.err.println("-g, --gedcom=FILE    GEDCOM file to extract from.");
        System.err.println("-f, --fringe=FILE    File to write fringe indi IDs to. Optional.");
    }

    public void g(final String file) throws IOException {
        gedcom(file);
    }

    public void gedcom(final String file) throws IOException {
        this.gedcom = new File(file);
        if (!this.gedcom.canRead()) {
            throw new IllegalArgumentException("Cannot read GEDCOM input file: "+this.gedcom.getCanonicalPath());
        }
    }

    public void f(final String file) throws IOException {
        fringe(file);
    }

    public void fringe(final String file) throws IOException {
        this.fringe = new File(file);
    }



    GedcomPointeesOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.gedcom == null) {
            throw new IllegalArgumentException("Missing required GEDCOM input file.");
        }
        return this;
    }
}

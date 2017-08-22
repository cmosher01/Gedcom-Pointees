package nu.mine.mosher.gedcom;

import java.io.File;
import java.io.IOException;

public class GedcomPointeesOptions extends GedcomOptions {
    public File in;
    public File out;
    public File outFringe;

    @Override
    public void help() {
        this.help = true;
        System.err.println("Usage: java -jar gedcom-pointees-all.jar [OPTION]... INDIS.ids.in POINTEES.ids.out [FRINGES.ids.out] <in.ged");
        System.err.println("Finds all IDs referenced from given INDI IDs.");
        System.err.println("Optionally find INDIs not in the original set (at the fringes).");
        System.err.println("Options:");
        super.options();
    }

    public void __(final String file) throws IOException {
        if (this.in == null) {
            this.in = new File(file);
            if (!this.in.canRead()) {
                throw new IllegalArgumentException("Cannot reads input file: "+this.in.getCanonicalPath());
            }
        } else if (this.out == null) {
            this.out = new File(file);
        } else if (this.outFringe == null) {
            this.outFringe = new File(file);
        }
    }


    public GedcomPointeesOptions verify() {
        if (this.help) {
            return this;
        }
        if (this.in == null) {
            throw new IllegalArgumentException("Missing required input file.");
        }
        return this;
    }
}

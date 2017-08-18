package nu.mine.mosher.gedcom;

import joptsimple.OptionParser;
import joptsimple.OptionSpec;

import java.io.File;
import java.util.List;

public class GedcomPointeesOptions extends GedcomOptions {
    private final OptionSpec<File> files;

    public GedcomPointeesOptions(final OptionParser parser) {
        super(parser);
        this.files = parser.nonOptions("indis.id.in pointees.id.out fringes.id.out").ofType(File.class).describedAs("FILES");
    }

    private List<File> files() {
        return this.files.values(get());
    }

    public File fileIndis() {
        if (files().size() <= 0) {
            throw new IllegalArgumentException("Missing indis.id.in file.");
        }
        return files().get(0);
    }

    public File filePointees() {
        if (files().size() <= 1) {
            throw new IllegalArgumentException("Missing pointees.id.out file.");
        }
        return files().get(1);
    }

    public File fileFringes() {
        if (files().size() <= 2) {
            throw new IllegalArgumentException("Missing fringes.id.out file.");
        }
        return files().get(2);
    }
}

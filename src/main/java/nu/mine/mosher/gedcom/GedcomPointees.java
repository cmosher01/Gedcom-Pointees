package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Christopher Alan Mosher on 2017-08-10
 */
public class GedcomPointees {
    private static final Logger log = Logger.getLogger("");

    private final File fileGedcom;
    private final File fileIndis;
    private final File filePointees;
    private final File fileFringes;

    private final Set<String> input = new HashSet<>();
    private final Set<String> pointee = new HashSet<>();
    private final Set<String> fringe = new HashSet<>();
    private final Set<String> seen = new HashSet<>();

    private GedcomTree gt;

    private GedcomPointees(final String filenameGedcom, final String filenameIndis, final String filenamePointees, final String filenameFringes) {
        this.fileGedcom = new File(filenameGedcom);
        this.fileIndis = new File(filenameIndis);
        this.filePointees = new File(filenamePointees);
        this.fileFringes = new File(filenameFringes);
    }

    public static void main(final String... args) throws InvalidLevel, IOException {
        if (args.length != 4) {
            throw new IllegalArgumentException("usage: java -jar gedcom-pointees.jar in.ged in.ids out.ids fringe.ids");
        } else {
            new GedcomPointees(args[0], args[1], args[2], args[3]).main();
        }
    }

    private void main() throws IOException, InvalidLevel {
        readValues();

        loadGedcom();

        pointees();

        writeSet(this.pointee, this.filePointees);
        writeSet(this.fringe, this.fileFringes);

        System.err.flush();
        System.out.flush();
    }

    private void loadGedcom() throws IOException, InvalidLevel {
        final Charset charset = Gedcom.getCharset(this.fileGedcom);
        this.gt = Gedcom.parseFile(fileGedcom, charset, false);
    }

    private void readValues() throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(this.fileIndis), "UTF-8"));
        for (String s = in.readLine(); s != null; s = in.readLine()) {
            this.input.add(s);
        }
        in.close();
    }

    private static void writeSet(final Set<String> set, final File file) throws IOException {
        final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        for (final String s : set) {
            out.write(s);
            out.newLine();
        }
        out.flush();
        out.close();
    }

    private void pointees() throws IOException {
        for (final String in : this.input) {
            this.seen.add(in);
            this.pointee.add(in);
            addPointees(this.gt.getNode(in));
        }
    }

    private void addPointees(final TreeNode<GedcomLine> node) {
        for (final TreeNode<GedcomLine> c : node) {
            addPointees(c);
        }

        final GedcomLine line = node.getObject();
        if (line.isPointer()) {
            addHelper(line.getPointer());
        }
    }

    private void addHelper(final String id) {
        final TreeNode<GedcomLine> pointee = this.gt.getNode(id);
        final GedcomLine pln = pointee.getObject();
        final String pid = pln.getID();
        if (pln.getTag().equals(GedcomTag.INDI)) {
            if (!this.input.contains(pid) && !this.seen.contains(pid)) {
                this.seen.add(pid);
                this.fringe.add(pid);
            }
        } else {
            this.seen.add(pid);
            this.pointee.add(pid);
            addPointees(pointee);
        }
    }
}

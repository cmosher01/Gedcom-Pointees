package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static nu.mine.mosher.logging.Jul.log;

/**
 * Created by Christopher Alan Mosher on 2017-08-10
 */
public class GedcomPointees {
    private final GedcomPointeesOptions options;

    private GedcomTree tree;

    private final Set<String> input = new HashSet<>();
    private final Set<String> pointee = new HashSet<>();
    private final Set<String> fringe = new HashSet<>();
    private final Set<String> seen = new HashSet<>();

    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        new GedcomPointees(new ArgParser<>(new GedcomPointeesOptions()).parse(args).verify()).main();
        System.out.flush();
        System.err.flush();
    }

    private GedcomPointees(final GedcomPointeesOptions options) {
        this.options = options;
    }

    private void main() throws IOException, InvalidLevel {
        if (this.options.help) {
            return;
        }
        readGedcom();
        readInput();
        pointees();
        writeOutput();
    }

    private void readGedcom() throws IOException, InvalidLevel {
        tree = Gedcom.readFile(new BufferedInputStream(new FileInputStream(this.options.gedcom)));
        new GedcomConcatenator(tree).concatenate();
    }

    private void readInput() throws IOException {
        new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in), StandardCharsets.UTF_8)).lines().forEach(this.input::add);
    }

    private void writeOutput() throws IOException {
        writeSet(this.pointee, null);
        if (this.options.fringe != null) {
            writeSet(this.fringe, this.options.fringe);
        }
    }

    private static void writeSet(final Set<String> set, final File file) throws IOException {
        final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(getOutputStream(file), StandardCharsets.UTF_8));
        for (final String s : set) {
            out.write(s);
            out.newLine();
        }
        out.flush();
        out.close();
    }

    private static OutputStream getOutputStream(final File file) throws FileNotFoundException {
        if (file == null) {
            return new FileOutputStream(FileDescriptor.out);
        }
        return new FileOutputStream(file);
    }



    private void pointees() throws IOException {
        for (final String id : this.input) {
            this.seen.add(id);
            this.pointee.add(id);
            final TreeNode<GedcomLine> node = this.tree.getNode(id);
            if (node == null) {
                log().warning("Cannot find record with ID: " + id);
            } else {
                addPointees(node);
            }
        }
    }

    private void addPointees(final TreeNode<GedcomLine> node) {
        node.forEach(this::addPointees);

        final GedcomLine line = node.getObject();
        if (line.isPointer()) {
            addHelper(line.getPointer());
        }
    }

    private void addHelper(final String id) {
        final TreeNode<GedcomLine> pointee = this.tree.getNode(id);
        if (pointee == null) {
            log().warning("Cannot find record with ID: " + id);
            return;
        }

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

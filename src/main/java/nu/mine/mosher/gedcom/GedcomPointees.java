package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Christopher Alan Mosher on 2017-08-10
 */
public class GedcomPointees implements Gedcom.Processor {
    private final GedcomPointeesOptions options;

    private final Set<String> input = new HashSet<>();
    private final Set<String> pointee = new HashSet<>();
    private final Set<String> fringe = new HashSet<>();
    private final Set<String> seen = new HashSet<>();

    public static void main(final String... args) throws InvalidLevel, IOException {
        final GedcomPointeesOptions options = new ArgParser<>(new GedcomPointeesOptions()).parse(args).verify();
        new Gedcom(options, new GedcomPointees(options)).main();
        System.out.flush();
        System.err.flush();
    }

    private GedcomPointees(final GedcomPointeesOptions options) {
        this.options = options;
    }

    @Override
    public boolean process(final GedcomTree tree) {
        try {
            readSet(this.options.in, this.input);

            pointees(tree);

            writeSet(this.pointee, this.options.out, true);
            if (this.options.outFringe != null) {
                writeSet(this.fringe, this.options.outFringe, false);
            }
        } catch (final Throwable e) {
            throw new IllegalStateException(e);
        }
        return false;
    }

    private static void readSet(final File file, final Set<String> set) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        for (String s = in.readLine(); s != null; s = in.readLine()) {
            set.add(s);
        }
        in.close();
    }

    private static void writeSet(final Set<String> set, final File file, final boolean isStdOk) throws IOException {
        final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(getOutputStream(file, isStdOk), "UTF-8"));
        for (final String s : set) {
            out.write(s);
            out.newLine();
        }
        out.flush();
        out.close();
    }


    private static OutputStream getOutputStream(final File file, final boolean isStdOk) throws FileNotFoundException {
        if (isStdOk && (file == null || file.getName().equals("-"))) {
            return new FileOutputStream(FileDescriptor.out);
        }
        return new FileOutputStream(file);
    }


    private void pointees(final GedcomTree tree) throws IOException {
        for (final String in : this.input) {
            this.seen.add(in);
            this.pointee.add(in);
            final TreeNode<GedcomLine> node = tree.getNode(in);
            if (node == null) {
                System.err.println("Cannot find record with ID: "+in);
            } else {
                addPointees(node, tree);
            }
        }
    }

    private void addPointees(final TreeNode<GedcomLine> node, final GedcomTree tree) {

        for (final TreeNode<GedcomLine> c : node) {
            addPointees(c, tree);
        }

        final GedcomLine line = node.getObject();
        if (line.isPointer()) {
            addHelper(line.getPointer(), tree);
        }
    }

    private void addHelper(final String id, final GedcomTree tree) {
        final TreeNode<GedcomLine> pointee = tree.getNode(id);
        if (pointee == null) {
            System.err.println("Cannot find record with ID: "+id);
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
            addPointees(pointee, tree);
        }
    }
}

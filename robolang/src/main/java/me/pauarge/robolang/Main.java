package me.pauarge.robolang;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;

import java.io.*;

import me.pauarge.robolang.TParser.prog_return;

class Main {

    private static boolean makeDot = false;
    private static boolean execute = true;

    static TLexer lexer;

    public static void main(String[] args) {
        try {
            lexer = new TLexer();

            if (args.length > 0) {
                int s = 0;

                if (args[0].startsWith("-dot")) {
                    makeDot = true;
                    s = 1;
                }
                // Recursively parse each directory, and each file on the
                // command line
                Tree t = parse(new File(args[s]));

            } else {
                System.err.println("Usage: java -jar robolang-1.0-jar-with-dependencies.jar <directory | filename.rl>");
            }
        } catch (Exception ex) {
            System.err.println("Robolang parser threw exception:");
            ex.printStackTrace();
        }
    }

    public static Tree parse(File source) throws Exception {
        try {
            String sourceFile = source.getName();

            if (sourceFile.length() > 3) {
                String suffix = sourceFile.substring(sourceFile.length() - 3).toLowerCase();
                if (suffix.compareTo(".rl") == 0) {
                    Tree t = parseSource(source.getAbsolutePath());
                    return t;
                }
            }
        } catch (Exception ex) {
            System.err.println("Robolang parser caught error on file open:");
            ex.printStackTrace();
        }
        return null;
    }

    public static Tree parseSource(String source) throws Exception {
        try {
            // First create a file stream using the povided file/path
            // and tell the lexer that that is the character source.
            // You can also use text that you have already read of course
            // by using the string stream.
            //
            lexer.setCharStream(new ANTLRFileStream(source, "UTF8"));

            // Using the lexer as the token source, we create a token
            // stream to be consumed by the parser
            //
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Now we need an instance of our parser
            //
            TParser parser = new TParser(tokens);
            prog_return psrReturn = parser.prog();
            Tree t = (Tree) psrReturn.getTree();

            if (makeDot && tokens.size() < 4096) {
                // Now stringify it if you want to...
                //
                // System.out.println(t.toStringTree());

                // Use the ANLTR built in dot generator
                //
                DOTTreeGenerator gen = new DOTTreeGenerator();

                // Which we can cause to generate the DOT specification
                // with the input file name suffixed with .dot. You can then use
                // the graphviz tools or zgrviewer (Java) to view the graphical
                // version of the dot file.
                //
                source = source.substring(0, source.length() - 2);
                String outputName = source + "dot";

                // It produces a jguru string template.
                //
                StringTemplate st = gen.toDOT(t, new CommonTreeAdaptor());

                // Create the output file and write the dot spec to it
                //
                FileWriter outputStream = new FileWriter(outputName);
                outputStream.write(st.toString());
                outputStream.close();

                // Invoke dot to generate a .png file
                //
                Process proc = Runtime.getRuntime().exec("dot -Tpng -o" + source + "png " + outputName);
                proc.waitFor();
            }

            if (execute) {
                try {
                    Interp I = new Interp(t);
                    I.run();
                } catch (RuntimeException e) {

                } catch (StackOverflowError e) {

                }
            }

            return t;
        } catch (FileNotFoundException ex) {
            // The file we tried to parse does not exist
            //
            System.err.println("\n  !!The file " + source + " does not exist!!\n");
            return null;
        } catch (Exception ex) {
            // Something went wrong in the parser, report this
            //
            System.err.println("Parser threw an exception:\n\n");
            ex.printStackTrace();
        }
        return null;
    }

}

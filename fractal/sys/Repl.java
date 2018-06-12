package fractal.sys;

import fractal.semantics.AbstractFractalEvaluator;
import fractal.semantics.FractalEvaluator;
import fractal.semantics.FractalState;
import java.io.*;
import fractal.syntax.FractalLexer;
import fractal.syntax.FractalParser;
import fractal.syntax.ASTProgram;
import fractal.values.FractalValue;
import java.awt.Color;
import java.util.ArrayList;

public class Repl<S, T> {

    public final String PROMPT = ">";
    protected Class<? extends AbstractFractalEvaluator> evalClass;
    AbstractFractalEvaluator interp;

    public Repl(Class<? extends AbstractFractalEvaluator> vClass, int width, int height) {
        this.interp = null;
        evalClass = vClass;
        try {
            interp = evalClass.newInstance();
            interp.setDisplaySize(width, height);
            //start
            //FractalState state = interp.getPersistentState();
            //TurtleDisplay display = state.getDisplay();
            //display.setBackground(Color.WHITE);
            //end
        } catch (InstantiationException | IllegalAccessException ie) {
            System.err.println(ie.getMessage());
            System.err.println("Fatal error: Failed to instantiate "
                    + "interpreter!  Terminating...");
            System.exit(1);
        }
    }

    /**
     * Set the size of the frame for showing the Turtle Display.
     * @param width The desired width of the frame.
     * @param height The desired height of the frame.
     */
    public void setDisplaySize(int width, int height) {
        interp.setDisplaySize(width, height);
    }

    /**
     * Set the foreground and background colours to be used by the Turtle
     * display.
     * @param fg The foreground colour
     * @param bg The background colour
     */
    public void setDisplayColours(Color fg, Color bg) {
        interp.setDisplayBackground(bg);
        interp.setDisplayForeground(fg);
    }

    public static void usage() {
        String[] instructions = {
            "The Repl may be invoked with 0 or more arguments.",
            "If no arguments are given, the Fractal evaluator is used to",
            "interpret commands read from standard input.",
            "If arguments are given they must be of the form:",
            "[<Eval class>] -- [<file1> <file2> ...]",
            "Eval class is the name of the class to be used to interpret the input",
            "If it is not supplied, it defaults to fractal.semantics.FractalEvaluator",
            "The -- must be supplied if any argumets are supplied at all; it ",
            "separates the evaluator class from the files to be processed.",
            "The files are read in left to right order, and processed one at a",
            "time. After all files have been processed, commands will be read",
            "from standard input and evaluated within a context that reflects",
            "the state after all the files have been processed.",
            "Note that this means that fractals can be defined in external files",
            "and then be manipulated interactively from the command line."
        };
        for (String line: instructions) {
            System.out.println(line);
        }

    }

    @SuppressWarnings("unchecked")
    public static void main(String args[]) {
        // if provided, first command line argument is class of evaluator
        // default is FractalEvaluator
        int width = 600;
        int height = 600;
        Repl<FractalState, FractalValue> repl;
        Class<? extends AbstractFractalEvaluator> evalClass = FractalEvaluator.class;
        ArrayList<String> fileList = new ArrayList<>();
        if (args.length > 0) {
            try {
                int idx = 0;
                while (idx < args.length && !args[idx].equals("--")) {
                    System.out.println(args[idx]);
                    idx += 1;
                }
                if (idx == 0) {
                    evalClass = FractalEvaluator.class;
                } else {
                    evalClass = (Class<? extends AbstractFractalEvaluator>) Class.forName(args[idx-1]);
                }
                for (int i = idx+1; i < args.length; i++) {
                    fileList.add(args[i]);
                }
            } catch (ClassNotFoundException cnfe) {
                System.err.println(cnfe.getMessage());
                usage();
                System.exit(1);
            }
        }
        repl = new Repl<>(evalClass, width, height);
        repl.setDisplayColours(Color.BLACK, Color.WHITE);
        repl.visitFiles(fileList);
        repl.loop();
    }

    public void visitFiles(ArrayList<String> fileNames) {
        // Treat all other command line arguments as files to be read and evaluated
        FileReader freader;
        for (String file : fileNames) {
            try {
                System.out.println("Reading from: " + file + "...");
                freader = new FileReader(new File(file));
                parseVisitShow(interp, freader);
                System.out.println("Done! Press ENTER to continue");
                System.in.read();
            } catch (FileNotFoundException fnfe) {
                System.err.println(fnfe.getMessage());
                System.err.println("Skipping it");
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
            }
        }
    }

    /**
     * The driver loop in which the standard input is read until EOF is pressed
     * (Ctrl-D on Unix, Ctrl-Z on Windows); on each pass of the loop, that input
     * is parsed, and visited, and the result is displayed .
     */
    public void loop() {
        System.out.println("Type commands at the prompt.\n" +
                "Press Ctrl-D (Ctrl-Z on Windows) to evaluate them.\n");
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            parseVisitShow(interp, reader);
        }
    }

    /**
     * Read a program from the given input reader, then parse it and evaluate it
     * and display the result.
     *
     * @param interp The interpreter to be used to evaluate the program
     * @param reader The input reader supplying the program
     */
    public void parseVisitShow(AbstractFractalEvaluator interp, Reader reader) {
        FractalParser parser;
        ASTProgram program = null;

        System.out.print(PROMPT);
        try {
            parser = new FractalParser(new FractalLexer(reader));
            program = (ASTProgram) parser.parse().value;
        } catch (Exception e) {
            System.out.println("Parse Error: " + e.getMessage());
        }

        if (program != null) {
            try {
                FractalValue result;
                // A null state indicates that this is the entry call to interp
                result = program.visit(interp, null);
                System.out.println("\nResult: " + result);
            } catch (FractalException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

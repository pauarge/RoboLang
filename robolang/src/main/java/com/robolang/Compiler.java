package com.robolang;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Compiler {
    private Tree t;
    private String path;
    private String className;
    private String code;

    public Compiler(Tree t, String path) {
        this.t = t;
        this.path = path;
        this.className = FilenameUtils.getBaseName(path);
    }

    public void writeFile() {
        compile();
        try {
            FileWriter f = new FileWriter(path);
            BufferedWriter out = new BufferedWriter(f);
            out.write(code);
            out.close();
        } catch (IOException e) {
            System.out.println("Could not write file");
            e.printStackTrace();
        }
    }

    private void compile() {
        Walker walker = new Walker(t, className, FilenameUtils.getFullPath(path));
        String codeAux = walker.getCode();
        try {
            code = new Formatter().formatSource(codeAux);
        } catch (FormatterException e) {
            System.out.println("The generated code is not correct");
            e.printStackTrace();
        }
    }

}

package com.robolang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RoboInterpreter {

    // TODO: Solve adding parents
    public static void main(String[] args) {
        try {
            String rltree = new String(Files.readAllBytes(Paths.get(args[0])));
            Node root = parse(rltree);
            System.out.println("Tree parsed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: Parse nodes with witespace
    private static Node parse(String inp) {
        Node node;

        if(inp.charAt(0) == '(' && inp.charAt(inp.length() - 1) == ')') {
            String name;
            inp = inp.substring(1, inp.length() - 1);
            int pivot = inp.indexOf(' ');

            if (pivot > -1) {
                name = inp.substring(0, pivot).trim();
                node = new Node(name);
                ArrayList<String> children = split_children(inp.substring(pivot, inp.length()).trim());
                for (int i = 0; i < children.size(); i++) {
                    Node child = parse(children.get(i));
                    node.addChild(child);
                }
            } else {
                node = new Node(inp);
            }
        } else {
            node = new Node(inp);
        }

        return node;
    }

    private static ArrayList<String> split_children(String inp) {
        ArrayList<String> res = new ArrayList<>();

        while (inp.length() > 0) {
            int i = 0;

            if(inp.charAt(0) == '('){
                int level = 1;
                while (level > 0) {
                    i++;
                    try {
                        if (inp.charAt(i) == '(') {
                            level++;
                        } else if (inp.charAt(i) == ')') {
                            level--;
                        }
                    } catch (Exception e){
                        System.out.println("FUCK");
                        System.out.println(inp);
                        System.out.println(i);
                        System.out.println(level);
                        System.out.println(res);
                        System.exit(0);
                    }
                }
            } else {
                i = inp.indexOf(' ');
                if(i == -1) {
                    res.add(inp);
                    inp = "";
                    continue;
                }
            }

            res.add(inp.substring(0, i + 1).trim());
            inp = inp.substring(i + 1, inp.length()).trim();
        }

        return res;
    }

}

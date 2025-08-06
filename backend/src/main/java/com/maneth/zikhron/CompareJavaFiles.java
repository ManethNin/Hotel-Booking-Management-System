package com.maneth.zikhron;

import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.tree.TreeContext;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.matchers.*;
import com.github.gumtreediff.actions.*;
import com.github.gumtreediff.gen.TreeGenerators;

import java.io.*;

public class CompareJavaFiles {
    public static void main(String[] args) throws Exception {
        // Initialize the tree generators
        Run.initGenerators();
        
        // Load the source files
        TreeContext src = TreeGenerators.getInstance().getTree("Old.java");
        TreeContext dst = TreeGenerators.getInstance().getTree("New.java");

        // Display the AST trees
        System.out.println("🌳 ABSTRACT SYNTAX TREES");
        System.out.println("=" + "=".repeat(60));
        
        System.out.println("\n📄 OLD.JAVA AST:");
        System.out.println("-" + "-".repeat(30));
        printTree(src.getRoot(), 0);
        
        System.out.println("\n📄 NEW.JAVA AST:");
        System.out.println("-" + "-".repeat(30));
        printTree(dst.getRoot(), 0);

        // Get the matcher and compute mappings
        Matcher matcher = Matchers.getInstance().getMatcher();
        MappingStore mappings = matcher.match(src.getRoot(), dst.getRoot());
        
        // Display mapping information
        System.out.println("\n🔗 TREE MAPPINGS (What nodes match between trees):");
        System.out.println("-" + "-".repeat(50));
        printMappings(mappings);
        
        // Generate actions using ChawatheScriptGenerator (a concrete implementation)
        EditScript editScript = new ChawatheScriptGenerator().computeActions(mappings);

        // Print the actions
        System.out.println("\n🔄 EDIT ACTIONS:");
        System.out.println("-" + "-".repeat(30));
        if (editScript.size() == 0) {
            System.out.println("No changes detected between the files.");
        } else {
            System.out.println("Changes detected (" + editScript.size() + " actions):");
            int count = 1;
            for (com.github.gumtreediff.actions.model.Action action : editScript) {
                System.out.println("[" + count + "] " + formatAction(action));
                count++;
            }
        }
        
        // Display statistics
        System.out.println("\n📊 STATISTICS:");
        System.out.println("-" + "-".repeat(20));
        System.out.println("Source tree nodes: " + src.getRoot().getMetrics().size);
        System.out.println("Target tree nodes: " + dst.getRoot().getMetrics().size);
        System.out.println("Mappings found: " + mappings.size());
        System.out.println("Edit actions: " + editScript.size());
    }
    
    /**
     * Recursively prints the AST tree structure
     */
    private static void printTree(Tree node, int depth) {
        // Create indentation based on depth
        String indent = "  ".repeat(depth);
        String connector = depth > 0 ? "├─ " : "";
        
        // Print node information
        String nodeInfo = node.getType().name;
        if (node.getLabel() != null && !node.getLabel().isEmpty()) {
            nodeInfo += ": " + node.getLabel();
        }
        
        System.out.println(indent + connector + nodeInfo + " [" + node.getPos() + "," + node.getEndPos() + "]");
        
        // Print children
        for (Tree child : node.getChildren()) {
            printTree(child, depth + 1);
        }
    }
    
    /**
     * Prints the mappings between source and destination trees
     */
    private static void printMappings(MappingStore mappings) {
        int count = 1;
        for (var mapping : mappings) {
            Tree srcNode = mapping.first;
            Tree dstNode = mapping.second;
            
            String srcInfo = srcNode.getType().name;
            if (srcNode.getLabel() != null && !srcNode.getLabel().isEmpty()) {
                srcInfo += ": " + srcNode.getLabel();
            }
            
            String dstInfo = dstNode.getType().name;
            if (dstNode.getLabel() != null && !dstNode.getLabel().isEmpty()) {
                dstInfo += ": " + dstNode.getLabel();
            }
            
            System.out.println("[" + count + "] " + srcInfo + " ↔ " + dstInfo);
            count++;
        }
        
        if (mappings.size() == 0) {
            System.out.println("No mappings found between the trees.");
        }
    }
    
    /**
     * Formats an action for better readability
     */
    private static String formatAction(com.github.gumtreediff.actions.model.Action action) {
        String actionType = action.getName().toUpperCase();
        String nodeName = action.getNode().getType().name;
        String label = action.getNode().getLabel();
        
        String nodeInfo = nodeName;
        if (label != null && !label.isEmpty()) {
            nodeInfo += ": " + label;
        }
        
        switch (actionType) {
            case "INSERT":
                return "➕ INSERT " + nodeInfo;
            case "DELETE":
                return "➖ DELETE " + nodeInfo;
            case "UPDATE":
                return "🔄 UPDATE " + nodeInfo;
            case "MOVE":
                return "📦 MOVE " + nodeInfo;
            default:
                return action.toString();
        }
    }
}

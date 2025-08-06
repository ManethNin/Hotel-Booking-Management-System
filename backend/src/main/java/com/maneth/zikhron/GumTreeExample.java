package com.maneth.zikhron;

import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.tree.TreeContext;
import com.github.gumtreediff.matchers.*;
import com.github.gumtreediff.actions.*;
import com.github.gumtreediff.gen.TreeGenerators;

import java.io.*;

/**
 * Example class demonstrating how to use GumTree library for Java file comparison.
 * This class shows how to:
 * 1. Initialize GumTree generators
 * 2. Load and parse Java files into abstract syntax trees
 * 3. Match trees and find differences
 * 4. Generate and display edit actions
 */
public class GumTreeExample {
    
    public static void main(String[] args) throws Exception {
        GumTreeExample example = new GumTreeExample();
        
        if (args.length == 2) {
            // Use command line arguments if provided
            example.compareFiles(args[0], args[1]);
        } else {
            // Use default test files
            example.compareFiles("Old.java", "New.java");
        }
    }
    
    /**
     * Compares two Java files and displays the differences
     * @param file1 Path to the first Java file
     * @param file2 Path to the second Java file
     */
    public void compareFiles(String file1, String file2) throws Exception {
        System.out.println("GumTree Java File Comparison");
        System.out.println("============================");
        System.out.println("Comparing: " + file1 + " vs " + file2);
        System.out.println();
        
        // Initialize the tree generators
        Run.initGenerators();
        
        try {
            // Load the source files and generate abstract syntax trees
            TreeContext src = TreeGenerators.getInstance().getTree(file1);
            TreeContext dst = TreeGenerators.getInstance().getTree(file2);

            // Get the matcher and compute mappings between nodes
            Matcher matcher = Matchers.getInstance().getMatcher();
            MappingStore mappings = matcher.match(src.getRoot(), dst.getRoot());
            
            // Generate edit script using Chawathe's algorithm
            EditScript editScript = new ChawatheScriptGenerator().computeActions(mappings);

            // Display results
            if (editScript.size() == 0) {
                System.out.println("✅ No changes detected between the files.");
            } else {
                System.out.println("📋 Changes detected (" + editScript.size() + " actions):");
                System.out.println("=" + "=".repeat(50));
                
                int actionCount = 1;
                for (com.github.gumtreediff.actions.model.Action action : editScript) {
                    System.out.println("[" + actionCount + "] " + formatAction(action));
                    actionCount++;
                }
            }
            
            // Display some statistics
            System.out.println();
            System.out.println("📊 Statistics:");
            System.out.println("- Source tree nodes: " + src.getRoot().getMetrics().size);
            System.out.println("- Target tree nodes: " + dst.getRoot().getMetrics().size);
            System.out.println("- Mappings found: " + mappings.size());
            System.out.println("- Edit actions: " + editScript.size());
            
        } catch (Exception e) {
            System.err.println("❌ Error comparing files: " + e.getMessage());
            System.err.println("Make sure both files exist and contain valid Java code.");
            throw e;
        }
    }
    
    /**
     * Formats an action for better readability
     * @param action The edit action to format
     * @return Formatted string representation of the action
     */
    private String formatAction(com.github.gumtreediff.actions.model.Action action) {
        String actionType = action.getName().toUpperCase();
        String nodeName = action.getNode().getType().name;
        
        switch (actionType) {
            case "INSERT":
                return "➕ INSERT " + nodeName;
            case "DELETE":
                return "➖ DELETE " + nodeName;
            case "UPDATE":
                return "🔄 UPDATE " + nodeName;
            case "MOVE":
                return "📦 MOVE " + nodeName;
            default:
                return action.toString();
        }
    }
}

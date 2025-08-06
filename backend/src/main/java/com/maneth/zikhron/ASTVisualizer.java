package com.maneth.zikhron;

import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.tree.TreeContext;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.matchers.*;
import com.github.gumtreediff.actions.*;
import com.github.gumtreediff.gen.TreeGenerators;

import java.io.*;
import java.util.*;

/**
 * Advanced AST Visualizer for GumTree
 * Shows detailed tree structures, mappings, and comparisons
 */
public class ASTVisualizer {
    
    public static void main(String[] args) throws Exception {
        ASTVisualizer visualizer = new ASTVisualizer();
        
        if (args.length == 2) {
            visualizer.visualizeComparison(args[0], args[1]);
        } else {
            visualizer.visualizeComparison("Old.java", "New.java");
        }
    }
    
    public void visualizeComparison(String file1, String file2) throws Exception {
        System.out.println("🌳 ADVANCED AST VISUALIZATION AND COMPARISON");
        System.out.println("=" + "=".repeat(80));
        System.out.println("Analyzing: " + file1 + " vs " + file2);
        System.out.println();
        
        // Initialize the tree generators
        Run.initGenerators();
        
        try {
            // Load the source files
            TreeContext src = TreeGenerators.getInstance().getTree(file1);
            TreeContext dst = TreeGenerators.getInstance().getTree(file2);
            
            // Display individual ASTs
            displayAST("SOURCE (" + file1 + ")", src.getRoot());
            displayAST("TARGET (" + file2 + ")", dst.getRoot());
            
            // Perform matching
            Matcher matcher = Matchers.getInstance().getMatcher();
            MappingStore mappings = matcher.match(src.getRoot(), dst.getRoot());
            
            // Display mappings
            displayMappings(mappings, src.getRoot(), dst.getRoot());
            
            // Generate and display edit script
            EditScript editScript = new ChawatheScriptGenerator().computeActions(mappings);
            displayEditScript(editScript);
            
            // Display tree comparison side by side
            displaySideBySideComparison(src.getRoot(), dst.getRoot(), mappings);
            
            // Display final statistics
            displayDetailedStatistics(src, dst, mappings, editScript);
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            throw e;
        }
    }
    
    private void displayAST(String title, Tree root) {
        System.out.println("\n📋 " + title + " AST STRUCTURE:");
        System.out.println("-" + "-".repeat(60));
        printTreeWithNumbers(root, 0, new HashMap<>());
        System.out.println();
    }
    
    private void printTreeWithNumbers(Tree node, int depth, Map<Tree, Integer> nodeNumbers) {
        // Assign a unique number to each node
        int nodeNum = nodeNumbers.size() + 1;
        nodeNumbers.put(node, nodeNum);
        
        String indent = "  ".repeat(depth);
        String connector = depth > 0 ? "├─ " : "";
        
        String nodeInfo = String.format("[%d] %s", nodeNum, node.getType().name);
        if (node.getLabel() != null && !node.getLabel().isEmpty()) {
            nodeInfo += ": '" + node.getLabel() + "'";
        }
        nodeInfo += String.format(" pos[%d,%d]", node.getPos(), node.getEndPos());
        
        System.out.println(indent + connector + nodeInfo);
        
        for (Tree child : node.getChildren()) {
            printTreeWithNumbers(child, depth + 1, nodeNumbers);
        }
    }
    
    private void displayMappings(MappingStore mappings, Tree srcRoot, Tree dstRoot) {
        System.out.println("\n🔗 NODE MAPPINGS (Matched elements between trees):");
        System.out.println("-" + "-".repeat(60));
        
        if (mappings.size() == 0) {
            System.out.println("No mappings found!");
            return;
        }
        
        int count = 1;
        for (var mapping : mappings) {
            Tree srcNode = mapping.first;
            Tree dstNode = mapping.second;
            
            String srcInfo = formatNodeInfo(srcNode);
            String dstInfo = formatNodeInfo(dstNode);
            
            System.out.println(String.format("[%d] %s ↔ %s", count, srcInfo, dstInfo));
            count++;
        }
        
        // Show unmapped nodes
        Set<Tree> mappedSrc = new HashSet<>();
        Set<Tree> mappedDst = new HashSet<>();
        for (var mapping : mappings) {
            mappedSrc.add(mapping.first);
            mappedDst.add(mapping.second);
        }
        
        System.out.println("\n🔸 UNMAPPED SOURCE NODES (will be deleted):");
        printUnmappedNodes(srcRoot, mappedSrc, 0);
        
        System.out.println("\n🔸 UNMAPPED TARGET NODES (will be inserted):");
        printUnmappedNodes(dstRoot, mappedDst, 0);
    }
    
    private void printUnmappedNodes(Tree node, Set<Tree> mappedNodes, int depth) {
        if (!mappedNodes.contains(node)) {
            String indent = "  ".repeat(depth);
            System.out.println(indent + "• " + formatNodeInfo(node));
        }
        
        for (Tree child : node.getChildren()) {
            printUnmappedNodes(child, mappedNodes, depth + 1);
        }
    }
    
    private void displayEditScript(EditScript editScript) {
        System.out.println("\n🔄 EDIT SCRIPT (Actions to transform source to target):");
        System.out.println("-" + "-".repeat(60));
        
        if (editScript.size() == 0) {
            System.out.println("✅ No changes needed!");
            return;
        }
        
        Map<String, Integer> actionCounts = new HashMap<>();
        
        for (int i = 0; i < editScript.size(); i++) {
            var action = editScript.get(i);
            String actionType = action.getName();
            actionCounts.put(actionType, actionCounts.getOrDefault(actionType, 0) + 1);
            
            String emoji = getActionEmoji(actionType);
            String nodeInfo = formatNodeInfo(action.getNode());
            
            System.out.println(String.format("[%d] %s %s %s", 
                i + 1, emoji, actionType.toUpperCase(), nodeInfo));
        }
        
        System.out.println("\n📈 Action Summary:");
        for (Map.Entry<String, Integer> entry : actionCounts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
    
    private void displaySideBySideComparison(Tree srcRoot, Tree dstRoot, MappingStore mappings) {
        System.out.println("\n🔀 SIDE-BY-SIDE TREE COMPARISON:");
        System.out.println("-" + "-".repeat(60));
        System.out.println(String.format("%-40s | %-40s", "SOURCE TREE", "TARGET TREE"));
        System.out.println("-" + "-".repeat(40) + "+" + "-".repeat(40));
        
        List<String> srcLines = getTreeLines(srcRoot, 0);
        List<String> dstLines = getTreeLines(dstRoot, 0);
        
        int maxLines = Math.max(srcLines.size(), dstLines.size());
        
        for (int i = 0; i < maxLines; i++) {
            String srcLine = i < srcLines.size() ? srcLines.get(i) : "";
            String dstLine = i < dstLines.size() ? dstLines.get(i) : "";
            
            // Truncate lines if too long
            if (srcLine.length() > 38) srcLine = srcLine.substring(0, 35) + "...";
            if (dstLine.length() > 38) dstLine = dstLine.substring(0, 35) + "...";
            
            System.out.println(String.format("%-40s | %-40s", srcLine, dstLine));
        }
    }
    
    private List<String> getTreeLines(Tree node, int depth) {
        List<String> lines = new ArrayList<>();
        String indent = "  ".repeat(depth);
        String connector = depth > 0 ? "├─ " : "";
        
        String nodeInfo = node.getType().name;
        if (node.getLabel() != null && !node.getLabel().isEmpty()) {
            nodeInfo += ": " + node.getLabel();
        }
        
        lines.add(indent + connector + nodeInfo);
        
        for (Tree child : node.getChildren()) {
            lines.addAll(getTreeLines(child, depth + 1));
        }
        
        return lines;
    }
    
    private void displayDetailedStatistics(TreeContext src, TreeContext dst, 
                                         MappingStore mappings, EditScript editScript) {
        System.out.println("\n📊 DETAILED STATISTICS:");
        System.out.println("-" + "-".repeat(30));
        
        int srcNodes = src.getRoot().getMetrics().size;
        int dstNodes = dst.getRoot().getMetrics().size;
        int mappedNodes = mappings.size();
        int editActions = editScript.size();
        
        System.out.println("Tree Metrics:");
        System.out.println("  Source nodes: " + srcNodes);
        System.out.println("  Target nodes: " + dstNodes);
        System.out.println("  Node difference: " + (dstNodes - srcNodes));
        
        System.out.println("\nMatching Metrics:");
        System.out.println("  Mapped pairs: " + mappedNodes);
        System.out.println("  Unmapped source: " + (srcNodes - mappedNodes));
        System.out.println("  Unmapped target: " + (dstNodes - mappedNodes));
        System.out.println("  Mapping ratio: " + String.format("%.2f%%", 
            (mappedNodes * 100.0) / Math.max(srcNodes, dstNodes)));
        
        System.out.println("\nEdit Metrics:");
        System.out.println("  Total actions: " + editActions);
        System.out.println("  Actions per node: " + String.format("%.2f", 
            editActions / (double) Math.max(srcNodes, dstNodes)));
        
        System.out.println("\nComplexity Assessment:");
        if (editActions == 0) {
            System.out.println("  🟢 No changes - files are identical");
        } else if (editActions < 10) {
            System.out.println("  🟡 Minor changes - low complexity");
        } else if (editActions < 50) {
            System.out.println("  🟠 Moderate changes - medium complexity");
        } else {
            System.out.println("  🔴 Major changes - high complexity");
        }
    }
    
    private String formatNodeInfo(Tree node) {
        String info = node.getType().name;
        if (node.getLabel() != null && !node.getLabel().isEmpty()) {
            info += ": '" + node.getLabel() + "'";
        }
        return info;
    }
    
    private String getActionEmoji(String actionType) {
        switch (actionType.toLowerCase()) {
            case "insert": return "➕";
            case "delete": return "➖";
            case "update": return "🔄";
            case "move": return "📦";
            default: return "❓";
        }
    }
}

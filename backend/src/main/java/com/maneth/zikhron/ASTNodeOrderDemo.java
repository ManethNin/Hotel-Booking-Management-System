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
 * Demonstrates why AST node numbers appear "out of order" for logical changes
 */
public class ASTNodeOrderDemo {
    
    public static void main(String[] args) throws Exception {
        ASTNodeOrderDemo demo = new ASTNodeOrderDemo();
        demo.demonstrateNodeOrdering("Old.java", "New.java");
    }
    
    public void demonstrateNodeOrdering(String file1, String file2) throws Exception {
        System.out.println("🔍 AST NODE ORDERING DEMONSTRATION");
        System.out.println("=" + "=".repeat(80));
        System.out.println("Understanding why node numbers seem 'out of order' for logical changes\n");
        
        Run.initGenerators();
        
        TreeContext src = TreeGenerators.getInstance().getTree(file1);
        TreeContext dst = TreeGenerators.getInstance().getTree(file2);
        
        // Show how nodes are numbered during traversal
        System.out.println("📋 TARGET FILE AST WITH TRAVERSAL ORDER:");
        System.out.println("-" + "-".repeat(60));
        Map<Tree, Integer> nodeNumbers = new HashMap<>();
        printTreeWithTraversalOrder(dst.getRoot(), 0, nodeNumbers);
        
        // Perform matching and show the edit script
        Matcher matcher = Matchers.getInstance().getMatcher();
        MappingStore mappings = matcher.match(src.getRoot(), dst.getRoot());
        EditScript editScript = new ChawatheScriptGenerator().computeActions(mappings);
        
        // Group actions by logical changes
        System.out.println("\n🔄 EDIT ACTIONS GROUPED BY LOGICAL CHANGES:");
        System.out.println("-" + "-".repeat(60));
        groupActionsByLogicalChange(editScript, nodeNumbers);
        
        // Explain the traversal order
        System.out.println("\n💡 WHY NUMBERS APPEAR 'OUT OF ORDER':");
        System.out.println("-" + "-".repeat(40));
        explainTraversalOrder();
    }
    
    private void printTreeWithTraversalOrder(Tree node, int depth, Map<Tree, Integer> nodeNumbers) {
        // Assign number in traversal order
        int nodeNum = nodeNumbers.size() + 1;
        nodeNumbers.put(node, nodeNum);
        
        String indent = "  ".repeat(depth);
        String connector = depth > 0 ? "├─ " : "";
        
        String nodeInfo = String.format("[%d] %s", nodeNum, node.getType().name);
        if (node.getLabel() != null && !node.getLabel().isEmpty()) {
            nodeInfo += ": '" + node.getLabel() + "'";
        }
        
        // Highlight new email-related nodes
        if (isEmailRelated(node)) {
            nodeInfo += " 📧 <-- EMAIL RELATED";
        }
        
        System.out.println(indent + connector + nodeInfo);
        
        // Traverse children in order
        for (Tree child : node.getChildren()) {
            printTreeWithTraversalOrder(child, depth + 1, nodeNumbers);
        }
    }
    
    private boolean isEmailRelated(Tree node) {
        if (node.getLabel() != null && node.getLabel().contains("email")) {
            return true;
        }
        
        // Check if this node is part of email field declaration
        Tree parent = node.getParent();
        while (parent != null) {
            if (parent.getType().name.equals("FieldDeclaration")) {
                // Check if this field declaration contains "email"
                return containsEmailInSubtree(parent);
            }
            if (parent.getType().name.equals("MethodDeclaration")) {
                // Check if this is getEmail or setEmail method
                return containsEmailInSubtree(parent);
            }
            parent = parent.getParent();
        }
        return false;
    }
    
    private boolean containsEmailInSubtree(Tree node) {
        if (node.getLabel() != null && node.getLabel().contains("email")) {
            return true;
        }
        for (Tree child : node.getChildren()) {
            if (containsEmailInSubtree(child)) {
                return true;
            }
        }
        return false;
    }
    
    private void groupActionsByLogicalChange(EditScript editScript, Map<Tree, Integer> nodeNumbers) {
        Map<String, List<String>> groupedActions = new LinkedHashMap<>();
        
        for (int i = 0; i < editScript.size(); i++) {
            var action = editScript.get(i);
            Tree node = action.getNode();
            String actionType = action.getName().toUpperCase();
            
            String group = categorizeAction(node);
            String actionDesc = String.format("[Node %d] %s %s%s", 
                nodeNumbers.getOrDefault(node, -1),
                getActionEmoji(actionType),
                actionType,
                formatNodeInfo(node));
            
            groupedActions.computeIfAbsent(group, k -> new ArrayList<>()).add(actionDesc);
        }
        
        // Display grouped actions
        int groupNum = 1;
        for (Map.Entry<String, List<String>> entry : groupedActions.entrySet()) {
            System.out.println(String.format("\n📦 GROUP %d: %s", groupNum++, entry.getKey()));
            System.out.println("   Actions: " + entry.getValue().size());
            
            for (String action : entry.getValue()) {
                System.out.println("   " + action);
            }
        }
    }
    
    private String categorizeAction(Tree node) {
        // Try to determine what logical change this node belongs to
        Tree current = node;
        
        while (current != null) {
            if (current.getType().name.equals("FieldDeclaration")) {
                if (containsEmailInSubtree(current)) {
                    return "ADD EMAIL FIELD";
                }
                return "OTHER FIELD CHANGE";
            }
            
            if (current.getType().name.equals("MethodDeclaration")) {
                if (containsEmailInSubtree(current)) {
                    Tree methodName = findMethodName(current);
                    if (methodName != null) {
                        if (methodName.getLabel().contains("getEmail")) {
                            return "ADD getEmail() METHOD";
                        } else if (methodName.getLabel().contains("setEmail")) {
                            return "ADD setEmail() METHOD";
                        } else if (methodName.getLabel().contains("Example")) {
                            return "MODIFY CONSTRUCTOR (add email param)";
                        }
                    }
                    return "EMAIL-RELATED METHOD CHANGE";
                }
                return "OTHER METHOD CHANGE";
            }
            
            current = current.getParent();
        }
        
        return "UNCATEGORIZED";
    }
    
    private Tree findMethodName(Tree methodDeclaration) {
        for (Tree child : methodDeclaration.getChildren()) {
            if (child.getType().name.equals("SimpleName")) {
                return child;
            }
        }
        return null;
    }
    
    private void explainTraversalOrder() {
        System.out.println("1. 🌳 AST nodes are numbered during DEPTH-FIRST TRAVERSAL");
        System.out.println("   - Start at root, go deep before going wide");
        System.out.println("   - Each node gets next available number");
        System.out.println();
        
        System.out.println("2. 📧 When you add 'private String email;', it creates:");
        System.out.println("   [17] FieldDeclaration       <- Parent container");
        System.out.println("   [18] Modifier: 'private'    <- First child");
        System.out.println("   [19] SimpleType             <- Second child");
        System.out.println("   [20] SimpleName: 'String'   <- Grandchild of SimpleType");
        System.out.println("   [21] VariableDeclarationFragment <- Third child");
        System.out.println("   [22] SimpleName: 'email'    <- Grandchild of Fragment");
        System.out.println();
        
        System.out.println("3. 🔄 But in edit actions, you see them as separate insertions:");
        System.out.println("   - Insert [17] FieldDeclaration");
        System.out.println("   - Insert [18] Modifier");  
        System.out.println("   - Insert [19] SimpleType");
        System.out.println("   - Insert [20] SimpleName: 'String'");
        System.out.println("   - Insert [21] VariableDeclarationFragment");
        System.out.println("   - Insert [22] SimpleName: 'email'");
        System.out.println();
        
        System.out.println("4. 🎯 The numbers ARE in order - it's just that:");
        System.out.println("   - They're assigned during tree traversal (depth-first)");
        System.out.println("   - One logical change creates multiple AST nodes");
        System.out.println("   - Edit actions are atomic (one per AST node)");
        System.out.println("   - Numbers reflect traversal order, not logical grouping");
        System.out.println();
        
        System.out.println("5. 💡 To see logical groupings:");
        System.out.println("   - Group actions by parent AST nodes");
        System.out.println("   - Look at the tree structure context");
        System.out.println("   - Understand that each syntax element = one AST node");
    }
    
    private String formatNodeInfo(Tree node) {
        String info = " " + node.getType().name;
        if (node.getLabel() != null && !node.getLabel().isEmpty()) {
            info += ": '" + node.getLabel() + "'";
        }
        return info;
    }
    
    private String getActionEmoji(String actionType) {
        switch (actionType.toLowerCase()) {
            case "insert-node": return "➕";
            case "delete-node": return "➖";
            case "update-node": return "🔄";
            case "move-node": return "📦";
            default: return "❓";
        }
    }
}

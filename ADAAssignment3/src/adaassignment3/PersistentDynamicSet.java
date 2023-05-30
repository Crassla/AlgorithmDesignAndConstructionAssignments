/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adaassignment3;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alex
 */
public class PersistentDynamicSet<E> extends BinarySearchTree<E> {

    //ansi code for reset
    public static final String RESET = "\033[0m";  // Text Reset

    //ansi codes for pretty printing
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    //boolean that controls whether nil nodes are shown when printing
    protected boolean showNil;

    //map and versioning for version control
    protected Map<Integer, BinaryTreeNode> versions;
    public int versionNumber;

    //instantiater
    public PersistentDynamicSet() {
        versions = new HashMap<>();
        versionNumber = 0;
        showNil = false;
    }

    //returns the ansi code for a specific number (determined by the version)
    protected String getColour(int num) {
        switch (num) {
            case 0:
                return BLACK;
            case 1:
                return RED;
            case 2:
                return GREEN;
            case 3:
                return YELLOW;
            case 4:
                return BLUE;
            case 5:
                return PURPLE;
            case 6:
                return CYAN;
            default:
                return WHITE;
        }
    }

    //newnode hook method
    @Override
    protected BinaryTreeNode newNode(BinaryTreeNode node) {
        PersistentBinaryTreeNode newNode = new PersistentBinaryTreeNode(node);
        return newNode;
    }

    //duplicate root node hook method
    @Override
    protected BinaryTreeNode duplicateRootNode() {
        PersistentBinaryTreeNode duplicateNode = new PersistentBinaryTreeNode(rootNode.element);
        PersistentBinaryTreeNode temp = (PersistentBinaryTreeNode) rootNode;
        duplicateNode.leftChild = rootNode.leftChild;
        duplicateNode.rightChild = rootNode.rightChild;
        duplicateNode.colour = versionNumber % 8;

        return duplicateNode;
    }

    //hook method for adding a node
    @Override
    protected void nodeAdded(BinaryTreeNode node) {
        PersistentBinaryTreeNode temp = (PersistentBinaryTreeNode) node;
        temp.colour = versionNumber % 8;
    }

    //hook method for visiting  a node
    @Override
    protected void NodeVisited(BinaryTreeNode currentNode, int right) {
        if (right == 1 && currentNode != null && currentNode.rightChild != null) {
            PersistentBinaryTreeNode duplicateNode = new PersistentBinaryTreeNode(currentNode.rightChild);
            duplicateNode.colour = versionNumber % 8;
            currentNode.rightChild = duplicateNode;
        } else if (right == -1 && currentNode != null && currentNode.leftChild != null) {
            PersistentBinaryTreeNode duplicateNode = new PersistentBinaryTreeNode(currentNode.leftChild);
            duplicateNode.colour = versionNumber % 8;
            currentNode.leftChild = duplicateNode;
        }
    }

    @Override
    public void replaceNodeVisited(BinaryTreeNode currentNode, int right) {
        if (right == 1 && currentNode != null && currentNode.rightChild != null) {
            BinaryTreeNode duplicateNode = new PersistentBinaryTreeNode(currentNode.rightChild);
            currentNode.rightChild = duplicateNode;
        } else if (currentNode != null && currentNode.leftChild != null) {
            BinaryTreeNode duplicateNode = new PersistentBinaryTreeNode(currentNode.leftChild);
            currentNode.leftChild = duplicateNode;
        }
    }

    @Override
    protected void getRoot() {
        versions.put(versionNumber, rootNode);
        versionNumber++;
    }

    //helper method to enable changing of previous versions
    protected void setRoot(int version) {
        rootNode = versions.get(version);
    }

    //returns the most current verion number
    public int getVersionNumber() {
        return this.versionNumber - 1;
    }

    //prints a specific version
    public String printVersion(int version) {
        String output = "";
        if (versions.containsKey(version)) {
            output = ("Version " + version + 1 + " tree: " + versions.get(version));
        }

        return output;
    }

    //prints the latest version
    public String printLatest() {
        return "Lastest Version: " + (versionNumber) + "\nTree: \n" + versions.get(versionNumber - 1);
    }

    //prints all versions
    @Override
    public String toString() {
        String output = "\n";

        for (int i = 0; i < versionNumber; i++) {
            output += "Version " + (i + 1) + ": \n\n";
            output += versions.get(i);
            output += "\n";
        }

        return output;
    }

    protected void showNil(boolean bol) {
        this.showNil = bol;
    }

    public static void main(String[] args) {  // create the binary search tree
        PersistentDynamicSet<String> tree = new PersistentDynamicSet<String>();
        // build the tree
        tree.add("cow");
        tree.add("fly");
        tree.add("fin");
        tree.add("bat");
        tree.add("ant");
        tree.add("ale");
        tree.add("a");
        tree.add("alz");
        tree.add("fox");
        tree.add("cat");
        tree.add("flea");
        tree.remove("bat");
        tree.remove("ant");
        tree.remove("alz");
        tree.remove("a");
        System.out.println(tree);
    }

    //new node with different print and additional colour field
    protected class PersistentBinaryTreeNode extends BinaryTreeNode {

        public int colour;

        public PersistentBinaryTreeNode(E element) {
            super(element);
            colour = 0;
        }

        public PersistentBinaryTreeNode(BinaryTreeNode node) {
            super(node);
            leftChild = node.leftChild;
            rightChild = node.rightChild;
            colour = 0;
        }

        public StringBuilder toString(StringBuilder prefix, boolean isRightChild, StringBuilder sb, boolean isRoot) {
            PersistentBinaryTreeNode right = (PersistentBinaryTreeNode) rightChild;
            PersistentBinaryTreeNode left = (PersistentBinaryTreeNode) leftChild;
            StringBuilder tempRight = new StringBuilder();
            if (right != null) {
                if (isRoot) {
                    right.toString(new StringBuilder().append(prefix).append(" "), false, sb, false);
                } else {
                    right.toString(new StringBuilder().append(prefix).append(getColour(colour)).append(isRightChild ? "│   " : "    ").append(RESET), false, sb, false);
                }
            } else if (showNil) {
                if (isRoot) {
                    tempRight.append(prefix).append(getColour(colour)).append(" ");
                    tempRight.append("┌── ").append("NIL").append("\n").append(RESET);
                } else {
                    tempRight.append(prefix).append(getColour(colour)).append("    ");
                    tempRight.append("┌── ").append("NIL").append("\n").append(RESET);
                }
            }
            if (isRoot) {
                sb.append(tempRight).append(prefix).append(getColour(colour)).append("-").append(element.toString()).append("\n").append(RESET);
            } else {
                sb.append(tempRight).append(prefix).append(getColour(colour)).append(isRightChild ? "└── " : "┌── ").append(element.toString()).append("\n").append(RESET);
            }
            if (left != null) {
                if (isRoot) {
                    left.toString(new StringBuilder().append(prefix).append(getColour(colour)).append(" ").append(RESET), true, sb, false);
                } else {
                    left.toString(new StringBuilder().append(prefix).append(getColour(colour)).append(isRightChild ? "    " : "│   ").append(RESET), true, sb, false);
                }
            } else if (showNil) {
                if (isRoot) {
                    sb.append(prefix).append(getColour(colour)).append(" ");
                    sb.append("└── ").append("NIL").append("\n").append(RESET);
                } else {
                    sb.append(prefix).append(getColour(colour)).append("    ");
                    sb.append("└── ").append("NIL").append("\n").append(RESET);
                }
            }

            return sb;
        }

        @Override
        public String toString() {
            return this.toString(new StringBuilder(), true, new StringBuilder(), true).toString();
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adaassignment3;

import static adaassignment3.PersistentDynamicSet.RESET;
import java.util.Stack;

/**
 *
 * @author alex
 */
public class BalancedPersistentDynamicSet<E> extends PersistentDynamicSet<E> {

    //stack to keep track of parents
    public Stack<RedBlackNode> parentStack;

    //private helper fields to grab checknode and parent from node removed hook method
    private RedBlackNode tcheckNode;
    private RedBlackNode tparent;

    //returns ansi red or black based on the nodes colour
    protected String getColour(boolean black) {
        if (black) {
            return BLACK;
        }
        return RED;
    }

    //removes red and black from options of colour
    protected String getColour(int num) {
        switch (num) {
            case 0:
                return CYAN;
            case 1:
                return PURPLE;
            case 2:
                return GREEN;
            case 3:
                return YELLOW;
            case 4:
                return BLUE;
            default:
                return WHITE;
        }
    }

    //duplicates nodes when they are visited and adds them onto the parent stack
    @Override
    protected void NodeVisited(BinaryTreeNode currentNode, int right) {
        if (currentNode != null) {
            RedBlackNode tempNode = (RedBlackNode) currentNode;
            if (parentStack.empty()) {
                parentStack.add(tempNode);
            } else if (parentStack.peek() != tempNode) {
                parentStack.add(tempNode);
            }
        }
        if (right == 1 && currentNode != null && currentNode.rightChild != null) {
            RedBlackNode duplicateNode = new RedBlackNode((RedBlackNode) currentNode.rightChild);
            duplicateNode.colour = versionNumber % 6;
            currentNode.rightChild = duplicateNode;
        } else if (right == 0 && currentNode != null && currentNode.leftChild != null) {
            //base case if parents need to be added onto the stack but the node doesn't need to be duplicated
        } else if (currentNode != null && currentNode.leftChild != null) {
            RedBlackNode duplicateNode = new RedBlackNode((RedBlackNode) currentNode.leftChild);
            duplicateNode.colour = versionNumber % 6;
            currentNode.leftChild = duplicateNode;
        }
    }

    //replacementnode doesn't add parents onto the stack
    @Override
    public void replaceNodeVisited(BinaryTreeNode currentNode, int right) {
        if (right == 1 && currentNode != null && currentNode.rightChild != null) {
            RedBlackNode duplicateNode = new RedBlackNode((RedBlackNode) currentNode.rightChild);
            duplicateNode.colour = versionNumber % 6;
            currentNode.rightChild = duplicateNode;
        } else if (right == 0 && currentNode != null && currentNode.leftChild != null) {

        } else if (currentNode != null && currentNode.leftChild != null) {
            RedBlackNode duplicateNode = new RedBlackNode((RedBlackNode) currentNode.leftChild);
            duplicateNode.colour = versionNumber % 6;
            currentNode.leftChild = duplicateNode;
        }
    }

    //creates a new red node to add
    @Override
    protected BinaryTreeNode newNode(BinaryTreeNode node) {
        RedBlackNode newNode = new RedBlackNode(node);
        newNode.colour = versionNumber % 6;
        return newNode;
    }

    //duplicates the root
    @Override
    protected BinaryTreeNode duplicateRootNode() {
        RedBlackNode duplicateNode = new RedBlackNode(rootNode.element);
        RedBlackNode temp = (RedBlackNode) rootNode;
        duplicateNode.leftChild = rootNode.leftChild;
        duplicateNode.rightChild = rootNode.rightChild;
        duplicateNode.black = temp.black;
        duplicateNode.colour = versionNumber % 6;

        return duplicateNode;
    }

    //instantiates parentstack
    public BalancedPersistentDynamicSet() {
        this.parentStack = new Stack<>();
    }
    
    //ADD Steps:
    //if node is root node change colour to black
    //if node is added onto black do nothing
    //if nodes parent is red check colour of uncle
    @Override
    protected void nodeAdded(BinaryTreeNode node) {
        RedBlackNode parent = null;
        if (!parentStack.empty()) {
            parent = parentStack.pop();
        }

        while (parent != null && !parent.black) {
            RedBlackNode grandparent = parentStack.pop();
            if (parent == grandparent.leftChild) {
                RedBlackNode uncle = null;
                if (grandparent.rightChild != null) {
                    uncle = new RedBlackNode((RedBlackNode) grandparent.rightChild);
                    uncle.colour = versionNumber % 6;
                }

                if (uncle != null && !uncle.black) {
                    parent.black = true;
                    uncle.black = true;
                    grandparent.rightChild = uncle;
                    grandparent.black = false;
                    node = grandparent;
                } else {
                    if (node == parent.rightChild) {
                        node = parent;
                        leftRotate((RedBlackNode) node, grandparent);
                    }

                    parent.black = true;
                    grandparent.black = false;
                    RedBlackNode greatGrandParent = null;
                    if (!parentStack.empty()) {
                        greatGrandParent = parentStack.pop();
                    }
                    rightRotate(grandparent, greatGrandParent);
                }
            } else {
                RedBlackNode uncle = null;
                if (grandparent.leftChild != null) {
                    uncle = new RedBlackNode((RedBlackNode) grandparent.leftChild);
                    uncle.colour = versionNumber % 6;
                }

                if (uncle != null && !uncle.black) {
                    parent.black = true;
                    uncle.black = true;
                    grandparent.leftChild = uncle;
                    grandparent.black = false;
                    node = grandparent;
                } else {
                    if (node == parent.leftChild) {
                        node = parent;
                        rightRotate((RedBlackNode) node, grandparent);
                    }
                    parent.black = true;
                    grandparent.black = false;
                    RedBlackNode greatGrandParent = null;
                    if (!parentStack.empty()) {
                        greatGrandParent = parentStack.pop();
                    }
                    leftRotate(grandparent, greatGrandParent);
                }

            }
            parent = null;
            if (!parentStack.empty()) {
                parent = parentStack.pop();
            }
        }

        RedBlackNode temp = (RedBlackNode) rootNode;
        temp.black = true;
        parentStack.clear();
    }

    //rotates right about node x
    protected RedBlackNode rightRotate(RedBlackNode x, RedBlackNode parent) {
        RedBlackNode y = new RedBlackNode((RedBlackNode) x.leftChild);
        y.colour = versionNumber % 6;
        RedBlackNode newX = new RedBlackNode(x);
        newX.colour = versionNumber % 6;
        if (y.rightChild != null) {
            newX.leftChild = new RedBlackNode((RedBlackNode) y.rightChild);
        } else {
            newX.leftChild = null;
        }

        if (parent == null) {
            rootNode = y;
        } else {
            if (parent.leftChild == x) {
                parent.leftChild = y;
            } else {
                parent.rightChild = y;
            }
        }

        y.rightChild = newX;

        return y;
    }

    //rotates left about node x
    protected RedBlackNode leftRotate(RedBlackNode x, RedBlackNode parent) {
        RedBlackNode y = new RedBlackNode((RedBlackNode) x.rightChild);
        y.colour = versionNumber % 6;
        RedBlackNode newX = new RedBlackNode(x);
        newX.colour = versionNumber % 6;
        if (y.leftChild != null) {
            newX.rightChild = new RedBlackNode((RedBlackNode) y.leftChild);
        } else {
            newX.rightChild = null;
        }

        if (parent == null) {
            rootNode = y;
        } else {
            if (parent.leftChild == x) {
                parent.leftChild = y;
            } else {
                parent.rightChild = y;
            }
        }

        y.leftChild = newX;

        return y;
    }

    //removes a node keeping track of which node if any needs to be checked and the nodes parent
    @Override
    protected BinaryTreeNode nodeRemoved(BinaryTreeNode deletedNode, BinaryTreeNode replacementNode, int children) {
        RedBlackNode checkNode = null;
        RedBlackNode parent = null;

        if (children == 2) {
            RedBlackNode temp = (RedBlackNode) replacementNode;
            RedBlackNode dtemp = (RedBlackNode) deletedNode;

            if (temp.black) {
                checkNode = (RedBlackNode) replacementNode;
                if (!parentStack.empty()) {
                    parent = parentStack.pop();
                }
                temp.black = dtemp.black;
            }
        } else if (children == 1) {
            RedBlackNode dtemp = (RedBlackNode) deletedNode;
            if (dtemp.black) {
                checkNode = (RedBlackNode) replacementNode;
                if (!parentStack.empty()) {
                    parent = parentStack.pop();
                }
            }
        } else {
            RedBlackNode dtemp = (RedBlackNode) deletedNode;
            if (dtemp.black) {
                if (!parentStack.empty()) {
                    parent = parentStack.pop();
                }
                if (parent != null && parent.leftChild != null && parent.leftChild == deletedNode) {
                    parent.leftChild = null;
                } else if (parent != null && parent.rightChild != null && parent.rightChild == deletedNode) {
                    parent.rightChild = null;
                }
                checkNode = parent;
                if (!parentStack.empty()) {
                    parent = parentStack.pop();
                }
            }
        }

        if (checkNode != null) {
            tparent = parent;
            tcheckNode = checkNode;
        } else {
            tparent = null;
            tcheckNode = null;
        }

        return replacementNode;
    }

    //if there is a node to be checked it runs fixup on the node
    //resets the root node to black after the fixup to ensure it remains black
    @Override
    protected void fixup() {
        if (tcheckNode != null) {
            redBlackDeleteFixup(tparent, tcheckNode);
        }
        if (rootNode != null) {
            RedBlackNode root = (RedBlackNode) rootNode;
            root.black = true;
        }
    }

    //helper method to fixup red black tree after delete
    private void redBlackDeleteFixup(RedBlackNode parent, RedBlackNode checkNode) {
        while (parent != null && checkNode.black) {
            RedBlackNode grandparent = null;
            if (!parentStack.empty()) {
                grandparent = parentStack.pop();
            }
            if (parent.leftChild != null && parent.leftChild == checkNode) {
                RedBlackNode w = null;
                if (parent.rightChild != null) {
                    w = (RedBlackNode) parent.rightChild;
                }

                if (w != null && !w.black) {
                    w.black = true;
                    parent.black = false;
                    leftRotate(parent, grandparent);
                    w = (RedBlackNode) parent.rightChild;
                }

                RedBlackNode templ = null;
                RedBlackNode tempr = null;

                if (w != null && w.leftChild != null) {
                    templ = (RedBlackNode) w.leftChild;
                } else if (w != null && w.rightChild != null) {
                    tempr = (RedBlackNode) w.rightChild;
                }

                if (w == null || (templ == null || templ.black) && (tempr == null || tempr.black)) {
                    if (w != null) {
                        w.black = false;
                    }
                    checkNode = parent;
                    parent = null;
                    if (!parentStack.empty()) {
                        parent = parentStack.pop();
                    }
                } else {
                    if (tempr == null || tempr.black) {
                        templ.black = true;
                        w.black = false;
                        rightRotate(w, parent);
                        w = (RedBlackNode) parent.rightChild;
                    }

                    w.black = parent.black;
                    parent.black = true;
                    if (tempr != null) {
                        tempr.black = true;
                    }
                    leftRotate(parent, grandparent);
                    parent = null;
                }
            } else {
                RedBlackNode w = null;
                if (parent.leftChild != null) {
                    w = (RedBlackNode) parent.leftChild;
                }

                if (w != null && !w.black) {
                    w.black = true;
                    parent.black = false;
                    rightRotate(parent, grandparent);
                    w = (RedBlackNode) parent.leftChild;
                }

                RedBlackNode templ = null;
                RedBlackNode tempr = null;

                if (w != null && w.leftChild != null) {
                    templ = (RedBlackNode) w.leftChild;
                } else if (w != null && w.rightChild != null) {
                    tempr = (RedBlackNode) w.rightChild;
                }

                if (w == null || (templ == null || templ.black) && (tempr == null || tempr.black)) {
                    if (w != null) {
                        w.black = false;
                    }
                    checkNode = parent;
                    parent = null;
                    if (!parentStack.empty()) {
                        parent = parentStack.pop();
                    }
                } else {
                    if (templ == null || templ.black) {
                        templ.black = true;
                        w.black = false;
                        leftRotate(w, parent);
                        w = (RedBlackNode) parent.leftChild;
                    }

                    w.black = parent.black;
                    parent.black = true;
                    if (templ != null) {
                        templ.black = true;
                    }
                    rightRotate(parent, grandparent);
                    parent = null;
                }
            }
        }
        checkNode.black = true;
        RedBlackNode root = (RedBlackNode) rootNode;
        root.black = true;
        parentStack.clear();
    }

    public static void main(String[] args) {
        BalancedPersistentDynamicSet<String> tree = new BalancedPersistentDynamicSet<>();
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
    protected class RedBlackNode extends PersistentBinaryTreeNode {

        public boolean black;

        public RedBlackNode(E element) {
            super(element);
            this.black = false;
        }

        public RedBlackNode(BinaryTreeNode node) {
            super(node);
            this.black = false;
        }

        public RedBlackNode(RedBlackNode node) {
            super(node);
            this.black = node.black;
            this.colour = node.colour;
        }

        public StringBuilder toString(StringBuilder prefix, boolean isRightChild, StringBuilder sb, boolean isRoot) {
            RedBlackNode right = (RedBlackNode) rightChild;
            RedBlackNode left = (RedBlackNode) leftChild;
            StringBuilder tempRight = new StringBuilder();
            if (right != null) {
                if (isRoot) {
                    right.toString(new StringBuilder().append(prefix).append(" "), false, sb, false);
                } else {
                    right.toString(new StringBuilder().append(prefix).append(getColour(colour)).append(isRightChild ? "│   " : "    ").append(RESET), false, sb, false);
                }
            } else if (showNil) {
                tempRight.append(prefix).append(getColour(colour)).append("    ");
                tempRight.append("┌── ").append(RESET).append(getColour(true)).append("NIL").append("\n").append(RESET);
            }
            if (isRoot) {
                sb.append(tempRight).append("-").append(getColour(colour)).append(getColour(black)).append(element.toString()).append("\n").append(RESET);
            } else {
                sb.append(tempRight).append(prefix).append(getColour(colour)).append(isRightChild ? "└── " : "┌── ").append(getColour(black)).append(element.toString()).append("\n").append(RESET);
            }
            if (left != null) {
                if (isRoot) {
                    left.toString(new StringBuilder().append(prefix).append(" "), true, sb, false);
                } else {
                    left.toString(new StringBuilder().append(prefix).append(getColour(colour)).append(isRightChild ? "    " : "│   ").append(RESET), true, sb, false);
                }
            } else if (showNil) {
                sb.append(prefix).append(getColour(colour)).append("    ");
                sb.append("└── ").append(RESET).append(getColour(true)).append("NIL").append("\n").append(RESET);
            }
            return sb;
        }

        @Override
        public String toString() {
            return this.toString(new StringBuilder(), true, new StringBuilder(), true).toString();
        }
    }
}

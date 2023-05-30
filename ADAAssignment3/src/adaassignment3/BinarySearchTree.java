package adaassignment3;

/**
 * A class that implements a sorted set collection using a binary search tree.
 * Note this implementation of a binary tree does not have duplicate (equal)
 * elements. This class allows a restricted view of the tree, between
 * fromElement (inclusive) and toElement (exclusive)
 *
 * @author Andrew Ensor
 */
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class BinarySearchTree<E> extends AbstractSet<E>
        implements SortedSet<E> {

    private int numElements;
    protected BinaryTreeNode rootNode;
    private Comparator<? super E> comparator;//null for natural ordering
    private E fromElement, toElement; // bounds for visible view of tree

    public BinarySearchTree() {
        super();
        numElements = 0;
        rootNode = null;
        comparator = null;
        fromElement = null;
        toElement = null;
    }

    public BinarySearchTree(Collection<? extends E> c) {
        this();
        for (E element : c) {
            add(element);
        }
    }

    public BinarySearchTree(Comparator<? super E> comparator) {
        this();
        this.comparator = comparator;
    }

    public BinarySearchTree(SortedSet<E> s) {
        this();
        this.comparator = s.comparator();
        for (E element : s) {
            add(element);
        }
    }

    // private constructor used to create a view of a portion of tree
    private BinarySearchTree(BinaryTreeNode rootNode,
            Comparator<? super E> comparator, E fromElement, E toElement) {
        this(comparator);
        this.rootNode = rootNode;
        this.fromElement = fromElement;
        this.toElement = toElement;
        // calculate the number of elements
        this.numElements = countNodes(rootNode);
    }

    // recursive helper method that counts number of descendants of node
    private int countNodes(BinaryTreeNode node) {
        if (node == null) {
            return 0;
        } else {
            return countNodes(node.leftChild) + 1
                    + countNodes(node.rightChild);
        }
    }

    // helper method that determines whether an element is within the
    // specified view
    private boolean withinView(E element) {
        boolean inside = true;
        if (fromElement != null && compare(element, fromElement) < 0) {
            inside = false;
        }
        if (toElement != null && compare(element, toElement) >= 0) {
            inside = false;
        }
        return inside;
    }

    // adds the element to the sorted set provided it is not already in
    // the set, and returns true if the sorted set did not already
    // contain the element
    public boolean add(E o) {
        if (!withinView(o)) {
            throw new IllegalArgumentException("Outside view");
        }
        BinaryTreeNode newNode = new BinaryTreeNode(o);
        newNode = newNode(newNode);
        boolean added = false;
        if (rootNode == null) {
            rootNode = newNode;
            nodeAdded(newNode);
            added = true;
        } else {  // find where to add newNode
            rootNode = duplicateRootNode();
            BinaryTreeNode currentNode = rootNode;
            boolean done = false;
            while (!done) {
                int comparison = compare(o, currentNode.element);
                if (comparison < 0) // newNode is less than currentNode
                {
                    if (currentNode.leftChild == null) {  // add newNode as leftChild
                        NodeVisited(currentNode, 0);
                        currentNode.leftChild = newNode;
                        nodeAdded(currentNode.leftChild);
                        done = true;
                        added = true;
                    } else {
                        NodeVisited(currentNode, -1);
                        currentNode = currentNode.leftChild;
                    }
                } else if (comparison > 0)//newNode is greater than currentNode
                {
                    if (currentNode.rightChild == null) {  // add newNode as rightChild
                        NodeVisited(currentNode, 0);
                        currentNode.rightChild = newNode;
                        nodeAdded(currentNode.rightChild);
                        done = true;
                        added = true;
                    } else {
                        NodeVisited(currentNode, 1);
                        currentNode = currentNode.rightChild;
                    }
                } else if (comparison == 0) // newNode equal to currentNode
                {
                    done = true; // no duplicates in this binary tree impl.
                }
            }
        }
        if (added) {
            numElements++;
            getRoot();
        }
        return added;
    }

    //hook method for visiting a node
    //-1 for left 1 for right 0 for rootNode
    protected void NodeVisited(BinaryTreeNode currentNode, int right) {

    }

    //hook method for visiting a node in make replacement
    protected void replaceNodeVisited(BinaryTreeNode currentNode, int right) {

    }

    //hook method to duplicate the root
    protected BinaryTreeNode duplicateRootNode() {
        return rootNode;
    }

    //hook method to get the root
    protected void getRoot() {

    }

    //hook method for when adding a new node
    protected void nodeAdded(BinaryTreeNode node) {

    }

    //hook method for adding a new node (setting a node to a subset of BinaryTreeNode
    protected BinaryTreeNode newNode(BinaryTreeNode node) {
        return node;
    }

    //hook method for when node being removed
    protected BinaryTreeNode nodeRemoved(BinaryTreeNode deletedNode, BinaryTreeNode replacementNode, int children) {
        return replacementNode;
    }

    //hook method to fixup the tree
    protected void fixup() {

    }

    // performs a comparison of the two elements, using the comparator
    // if not null, otherwise using the compareTo method
    private int compare(E element1, E element2) {
        if (comparator != null) {
            return comparator.compare(element1, element2);
        } else if (element1 != null && element1 instanceof Comparable) {
            return ((Comparable) element1).compareTo(element2); //unchecked
        } else if (element2 != null && element2 instanceof Comparable) {
            return -((Comparable) element2).compareTo(element1);//unchecked
        } else {
            return 0;
        }
    }

    // remove the element from the sorted set and returns true if the
    // element was in the sorted set
    public boolean remove(Object o) {
        boolean removed = false;
        E element = (E) o; // unchecked, could throw exception
        if (!withinView(element)) {
            throw new IllegalArgumentException("Outside view");
        }
        if (rootNode != null) {  // check if root to be removed
            rootNode = duplicateRootNode();
            if (compare(element, rootNode.element) == 0) {
                int children = 2;
                if (rootNode.leftChild == null && rootNode.rightChild == null) {
                    children = 0;
                } else if (rootNode.leftChild == null || rootNode.rightChild == null) {
                    children = 1;
                }
                rootNode = nodeRemoved(rootNode, makeReplacement(rootNode), children);
                fixup();
                removed = true;
            } else {  // search for the element o
                BinaryTreeNode parentNode = rootNode;
                BinaryTreeNode removalNode;
                // determine whether to traverse to left or right of root
                if (compare(element, rootNode.element) < 0) {
                    NodeVisited(rootNode, -1);
                    removalNode = rootNode.leftChild;
                } else // compare(element, rootNode.element)>0
                {
                    NodeVisited(rootNode, 1);
                    removalNode = rootNode.rightChild;
                }
                while (removalNode != null && !removed) {  // determine whether the removalNode has been found
                    int comparison = compare(element, removalNode.element);
                    if (comparison == 0) {
                        if (removalNode == parentNode.leftChild) {
                            int children = 2;
                            if (parentNode.leftChild.leftChild == null && parentNode.leftChild.rightChild == null) {
                                children = 0;
                            } else if (parentNode.leftChild.leftChild == null || parentNode.leftChild.rightChild == null) {
                                children = 1;
                            }
                            parentNode.leftChild = nodeRemoved(parentNode.leftChild, makeReplacement(parentNode.leftChild), children);
                            fixup();
                        } else // removalNode==parentNode.rightChild
                        {
                            int children = 2;
                            if (parentNode.rightChild.leftChild == null && parentNode.rightChild.rightChild == null) {
                                children = 0;
                            } else if (parentNode.rightChild.leftChild == null || parentNode.rightChild.rightChild == null) {
                                children = 1;
                            }
                            parentNode.rightChild = nodeRemoved(parentNode.rightChild, makeReplacement(parentNode.rightChild), children);
                            fixup();
                        }
                        removed = true;
                    } else // determine whether to traverse to left or right
                    {
                        parentNode = removalNode;
                        if (comparison < 0) {
                            NodeVisited(removalNode, -1);
                            removalNode = removalNode.leftChild;
                        } else // comparison>0
                        {
                            NodeVisited(removalNode, 1);
                            removalNode = removalNode.rightChild;
                        }
                    }
                }
            }
        }
        if (removed) {
            numElements--;
            getRoot();
        }
        return removed;
    }

    // helper method which removes removalNode (presumed not null) and
    // returns a reference to node that should take place of removalNode
    private BinaryTreeNode makeReplacement(BinaryTreeNode removalNode) {
        BinaryTreeNode replacementNode = null;
        // check cases when removalNode has only one child
        if (removalNode.leftChild != null && removalNode.rightChild == null) {
            replaceNodeVisited(removalNode, -1);
            replacementNode = removalNode.leftChild;
        } else if (removalNode.leftChild == null && removalNode.rightChild != null) {
            replaceNodeVisited(removalNode, 1);
            replacementNode = removalNode.rightChild;
        } // check case when removalNode has two children
        else if (removalNode.leftChild != null && removalNode.rightChild != null) {  // find the inorder successor and use it as replacementNode
            replaceNodeVisited(removalNode, 1);
            BinaryTreeNode parentNode = removalNode;
            replacementNode = removalNode.rightChild;
            if (replacementNode.leftChild == null) // replacementNode can be pushed up one level to replace
            // removalNode, move the left child of removalNode to be
            // the left child of replacementNode
            {
                replaceNodeVisited(removalNode, -1);
                replacementNode.leftChild = removalNode.leftChild;
            } else {  //find left-most descendant of right subtree of removalNode
                do {
                    parentNode = replacementNode;
                    replaceNodeVisited(parentNode, -1);
                    replacementNode = replacementNode.leftChild;
                } while (replacementNode.leftChild != null);
                // move the right child of replacementNode to be the left
                // child of the parent of replacementNode

                parentNode.leftChild = replacementNode.rightChild;
                // move the children of removalNode to be children of
                // replacementNode
                replaceNodeVisited(removalNode, -1);
                replacementNode.leftChild = removalNode.leftChild;
                NodeVisited(removalNode, 1);
                replacementNode.rightChild = removalNode.rightChild;
            }
        }
        // else both leftChild and rightChild null so no replacementNode
        return replacementNode;
    }

    public Iterator<E> iterator() {
        return new BinaryTreeIterator(rootNode);
    }

    // returns the number of elements in the tree
    public int size() {
        return numElements;
    }

    // removes all elements from the collection
    public void clear() {
        rootNode = null; // all nodes will be garbage collected as well
    }

    // overridden method with an efficient O(log n) search algorithm
    // rather than the superclasses O(n) linear search using iterator
    public boolean contains(Object o) {
        boolean found = false;
        E element = (E) o; // unchecked, could throw exception
        if (!withinView(element)) {
            return false;
        }
        BinaryTreeNode currentNode = rootNode;
        while (!found && currentNode != null) {
            containsVisited(currentNode);
            int comparison = compare(currentNode.element, element);
            if (comparison == 0) {
                found = true;
            } else if (comparison < 0) {
                currentNode = currentNode.rightChild;
            } else // comparison>0
            {
                currentNode = currentNode.leftChild;
            }
        }
        return found;
    }

    //hookmethod for visiting contains
    protected void containsVisited(BinaryTreeNode node) {

    }

    // returns the Comparator used to compare elements or null if
    // the element natural ordering is used
    public Comparator<? super E> comparator() {
        return comparator;
    }

    // returns the first (lowest) element currently in sorted set that
    // is at least as big as fromElement, returns null if none found
    public E first() {
        if (rootNode == null) {
            throw new NoSuchElementException("empty tree");
        }
        // find the least descendant of rootNode that is at least
        // as big as fromElement by traversing down tree from root
        BinaryTreeNode currentNode = rootNode;
        BinaryTreeNode leastYetNode = null; // smallest found so far
        while (currentNode != null) {
            if (compare(currentNode.element, fromElement) >= 0) {
                if (compare(currentNode.element, toElement) < 0) {
                    leastYetNode = currentNode;
                }
                // move to the left child to see if a smaller element okay
                // since all in right subtree will be larger
                currentNode = currentNode.leftChild;
            } else // compare(currentNode.element, fromElement)<0
            {  // move to the right child since this element too small
                // so all in left subtree will also be too small
                currentNode = currentNode.rightChild;
            }
        }
        if (leastYetNode == null) // no satisfactory node found
        {
            return null;
        } else {
            return leastYetNode.element;
        }
    }

    public SortedSet<E> headSet(E toElement) {
        return subSet(null, toElement);
    }

    // returns the last (highest) element currently in sorted set that
    // is less than toElement, return null if none found
    public E last() {
        if (rootNode == null) {
            throw new NoSuchElementException("empty tree");
        }
        // find the greatest descendant of rootNode that is less than
        // toElement by traversing down tree from root
        BinaryTreeNode currentNode = rootNode;
        BinaryTreeNode greatestYetNode = null; // greatest found so far
        while (currentNode != null) {
            if (compare(currentNode.element, toElement) < 0) {
                if (compare(currentNode.element, fromElement) >= 0) {
                    greatestYetNode = currentNode;
                }
                // move to the right child to see if a greater element okay
                // since all in left subtree will be smaller
                currentNode = currentNode.rightChild;
            } else // compare(currentNode.element, toElement)>=0
            {  // move to the left child since this element too large
                // so all in right subtree will also be too large
                currentNode = currentNode.leftChild;
            }
        }
        if (greatestYetNode == null) // no satisfactory node found
        {
            return null;
        } else {
            return greatestYetNode.element;
        }
    }

    public SortedSet<E> subSet(E fromElement, E toElement) {
        return new BinarySearchTree<E>(rootNode, comparator, fromElement,
                toElement);
    }

    public SortedSet<E> tailSet(E fromElement) {
        return subSet(fromElement, null);
    }

    // outputs the elements stored in the full binary tree (not just
    // the view) using inorder traversal
    public String toString() {
        return "Tree: \n" + rootNode;
    }

    public static void main(String[] args) {  // create the binary search tree
        SortedSet<String> tree = new BinarySearchTree<String>();
        // build the tree
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
        System.out.println(tree);
        tree.remove("bat");
        tree.remove("ant");
        tree.remove("alz");
        tree.remove("a");
        System.out.println(tree);
    }

    // inner class that represents a node in the binary tree
    // where each node consists of the element and links to
    // left child and right child (no need for link to parent)
    protected class BinaryTreeNode {

        public BinaryTreeNode leftChild, rightChild;
        public E element;

        public BinaryTreeNode(E element) {
            this.element = element;
            leftChild = null;
            rightChild = null;
        }

        public BinaryTreeNode(BinaryTreeNode node) {
            this.element = node.element;
            leftChild = node.leftChild;
            rightChild = node.rightChild;
        }

        // returns a string representation of the node and
        // its children using inorder (left-this-right) traversal
        public StringBuilder toString(StringBuilder prefix, boolean isRightChild, StringBuilder sb, boolean isRoot) {
            if (rightChild != null) {
                if (isRoot) 
                    rightChild.toString(new StringBuilder().append(prefix).append(" "), false, sb, false);
                else
                    rightChild.toString(new StringBuilder().append(prefix).append(isRightChild ? "│   " : "    "), false, sb, false);
            }
            if (isRoot) {
                sb.append(prefix).append("-").append(element.toString()).append("\n");
            } else {
                sb.append(prefix).append(isRightChild ? "└── " : "┌── ").append(element.toString()).append("\n");
            }
            if (leftChild != null) {
                if (isRoot)
                    leftChild.toString(new StringBuilder().append(prefix).append(" "), true, sb, false);
                else
                    leftChild.toString(new StringBuilder().append(prefix).append(isRightChild ? "    " : "│   "), true, sb, false);
            }

            return sb;
        }

        @Override
        public String toString() {
            return this.toString(new StringBuilder(), true, new StringBuilder(), true).toString();
        }
    }

    // inner class that represents an Iterator for a binary tree
    private class BinaryTreeIterator implements Iterator<E> {

        private LinkedList<E> list;
        private Iterator<E> iterator;

        public BinaryTreeIterator(BinaryTreeNode rootNode) {  // puts the elements in a linked list using inorder traversal
            list = new LinkedList<E>();
            traverseInOrder(rootNode);
            iterator = list.iterator();
        }

        // recursive helper method that traverses the subtree from node
        // adding the elements to the list collection
        private void traverseInOrder(BinaryTreeNode node) {
            if (node != null) {
                traverseInOrder(node.leftChild);
                if (withinView(node.element)) {
                    list.add(node.element);
                }
                traverseInOrder(node.rightChild);
            }
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public E next() {
            return iterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

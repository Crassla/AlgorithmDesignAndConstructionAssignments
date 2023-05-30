/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author alex
 */
public class Subdivisions {

    public int costs[][];

    public int length;
    public int height;

    public int[][] matrix;

    private int costOfDivision;

    public Subdivisions(int length, int height) {
        this.costs = new int[length][height];

        populateCosts(length, height);
    }

    public void setRect(int length, int height, int costOfDivision) {
        this.length = length;
        this.height = height;
        this.costOfDivision = costOfDivision;
    }

    private void levelOrder(TreeNode node) {
        Queue<TreeNode> tempQueue = new LinkedList<>();
        int level = 0;
        node.lengthMin = 0;
        node.lengthMax = length;
        node.heightMin = 0;
        node.heightMax = height;
        tempQueue.add(node);

        while (!tempQueue.isEmpty()) {
            TreeNode tempNode = tempQueue.remove();
            level++;
            
            if (tempNode.leftNode == null && tempNode.rightNode == null) {
                for (int i = tempNode.heightMin; i < tempNode.heightMax; i++) {
                    for (int j = tempNode.lengthMin; j < tempNode.lengthMax; j++) {
                        matrix[j][i] = 12 * level;
                    }
                }
            } else if (tempNode.lengthWise) {
                tempNode.leftNode.heightMin = tempNode.heightMin;
                tempNode.leftNode.heightMax = tempNode.heightMax;
                tempNode.leftNode.lengthMin = tempNode.lengthMin;
                tempNode.leftNode.lengthMax = tempNode.lengthMin + tempNode.divisionIndex;
                
                tempNode.rightNode.heightMin = tempNode.heightMin;
                tempNode.rightNode.heightMax = tempNode.heightMax;
                tempNode.rightNode.lengthMin = tempNode.lengthMin + tempNode.divisionIndex;
                tempNode.rightNode.lengthMax = tempNode.lengthMax;
            } else {
                tempNode.leftNode.heightMin = tempNode.heightMin;
                tempNode.leftNode.heightMax = tempNode.heightMin + tempNode.divisionIndex;
                tempNode.leftNode.lengthMin = tempNode.lengthMin;
                tempNode.leftNode.lengthMax = tempNode.lengthMax;
                
                tempNode.rightNode.heightMin = tempNode.heightMin + tempNode.divisionIndex;
                tempNode.rightNode.heightMax = tempNode.heightMax;
                tempNode.rightNode.lengthMin = tempNode.lengthMin;
                tempNode.rightNode.lengthMax = tempNode.lengthMax;
            }
            

            if (tempNode.leftNode != null) {
                tempQueue.add(tempNode.leftNode);
            }
            if (tempNode.rightNode != null) {
                tempQueue.add(tempNode.rightNode);
            }
        }
    }

    public int bruteForceSubDivide() {
        TreeNode node = recBruteForce(length, height);

        matrix = new int[length][height];

        levelOrder(node);
        
        System.out.println("BruteForce Matrix: ");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                System.out.printf("%4d", matrix[j][i]);
            }
            System.out.println("");
        }
        return node.value;
    }

    //n = length iteration
    //0 for no division
    //1 for lengthwise division
    //-1 for heighwise division
    private TreeNode recBruteForce(int length, int height) {
        ArrayList<TreeNode> totalCosts = new ArrayList<>();

        if (length == 1 && height == 1) {
            TreeNode node = new TreeNode(length, height, costs[length - 1][height - 1]);

            return node;
        }

        TreeNode node = new TreeNode(length, height, costs[length - 1][height - 1]);

        totalCosts.add(node);

        for (int i = 1; i < length; i++) {
            TreeNode leftNode = recBruteForce(i, height);
            TreeNode rightNode = recBruteForce(length - i, height);
            TreeNode lengthNode = new TreeNode(length, height, leftNode.value + rightNode.value - costOfDivision * height);

            lengthNode.addChildren(leftNode, rightNode);
            lengthNode.division(i, true);

            totalCosts.add(lengthNode);
        }

        for (int i = 1; i < height; i++) {
            TreeNode leftNode = recBruteForce(length, i);
            TreeNode rightNode = recBruteForce(length, height - i);
            TreeNode heightNode = new TreeNode(length, height, leftNode.value + rightNode.value - costOfDivision * length);

            heightNode.addChildren(leftNode, rightNode);
            heightNode.division(i, false);

            totalCosts.add(heightNode);
        }

        TreeNode maxNode = Collections.max(totalCosts);

        return maxNode;
    }

    //calls the recursive inner class
    public int greedySolution() {
        matrix = new int[length][height];

        Count count = new Count();
        int value = recGreedySolution(length, height, count, matrix, 0, length, 0, height);
        
        System.out.println("Greedy Matrix: ");
            for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                System.out.printf("%4d", matrix[j][i]);
            }
            System.out.println("");
        }
        System.out.println("");

        return value;
    }

    //looks through all of the possible subdivision choices that can currently be made
    //saves the most profitable choice based on the cost of division to the max variable
    //recursively calls the program again on whatever is left over adding it to the total cost
    private int recGreedySolution(int length, int height, Count count, int[][] matrix, int lengthIndexMin, int lengthIndexMax, int heightIndexMin, int heightIndexMax) {
        //if the length and height is 1 it is not possible to subdivide
        if (length == 1 && height == 1) {
            return costs[length - 1][height - 1];
        }

        int max = costs[length - 1][height - 1];
        boolean changed = false;
        int nLength = length;
        int nHeight = height;

        for (int i = 1; i < length; i++) {
            int comp = costs[length - 1 - i][height - 1] + costs[i - 1][height - 1] - costOfDivision * height;
            if (comp > max) {
                changed = true;
                max = comp;
                nLength = i;
                nHeight = height;
            }
        }

        for (int i = 1; i < height; i++) {
            int comp = costs[length - 1][height - 1 - i] + costs[length - 1][i - 1] - costOfDivision * length;
            if (comp > max) {
                changed = true;
                max = comp;
                nHeight = i;
                nLength = length;
            }
        }

        //if the max is the current length and height return the value of that
        if (!changed) {
            for (int i = heightIndexMin; i < heightIndexMax; i++) {
                for (int j = lengthIndexMin; j < lengthIndexMax; j++) {
                    matrix[j][i] = count.count;
                }
            }

            count.count++;

            return max;
        }

        if (nLength == length) {
            for (int i = heightIndexMin; i < nHeight; i++) {
                for (int j = lengthIndexMin; j < lengthIndexMax; j++) {
                    matrix[j][i] = count.count;
                }
            }

            count.count++;

            for (int i = nHeight; i < heightIndexMin; i++) {
                for (int j = lengthIndexMin; j < lengthIndexMax; j++) {
                    matrix[j][i] = count.count;
                }
            }

            count.count++;

            return recGreedySolution(nLength, nHeight, count, matrix, lengthIndexMin, lengthIndexMax, heightIndexMin, heightIndexMin + nHeight) + recGreedySolution(nLength, height - nHeight, count, matrix, lengthIndexMin, lengthIndexMax, heightIndexMin + nHeight, heightIndexMax) - costOfDivision * length;
        }

        for (int i = heightIndexMin; i < heightIndexMax; i++) {
            for (int j = lengthIndexMin; j < nLength; j++) {
                matrix[j][i] = count.count;
            }
        }

        count.count++;

        for (int i = heightIndexMin; i < heightIndexMax; i++) {
            for (int j = nLength; j < lengthIndexMax; j++) {
                matrix[j][i] = count.count;
            }
        }

        count.count++;

        //otherwise recursively find the value of the uncalculated subvision       
        return recGreedySolution(nLength, nHeight, count, matrix, lengthIndexMin, lengthIndexMin + nLength, heightIndexMin, heightIndexMax) + recGreedySolution(length - nLength, nHeight, count, matrix, lengthIndexMin + nLength, lengthIndexMax, heightIndexMin, heightIndexMax) - costOfDivision * height;
    }

    public int exactApproach() {

        TreeNode[][] calculationMatrix = new TreeNode[length][height];

        TreeNode node = recExactApproach(length, height, calculationMatrix);

        levelOrder(node);

        System.out.println("Exact Matrix: ");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                System.out.printf("%4d", matrix[j][i]);
            }
            System.out.println("");
        }

        return node.value;
    }

    //needs to work like the bruteforce algorithum but ensure that no repeats happen
    //if a repeat happens takes the max value from the repeat that has already been calculated
    //this ensures that the algorthium only interates the exact number of times it has to but still produces the true maximum
    //result
    private TreeNode recExactApproach(int length, int height, TreeNode[][] calculationMatrix) {
        ArrayList<TreeNode> totalCosts = new ArrayList<>();

        if (length == 1 && height == 1) {
            TreeNode node = new TreeNode(length, height, costs[length - 1][height - 1]);

            return node;
        }

        TreeNode node = new TreeNode(length, height, costs[length - 1][height - 1]);


        totalCosts.add(node);

        for (int i = 1; i < length; i++) {
            if (calculationMatrix[length - i - 1][height - 1] != null && calculationMatrix[i - 1][height - 1] != null) {
                TreeNode leftNode = new TreeNode(calculationMatrix[i - 1][height - 1]);
                TreeNode rightNode = new TreeNode(calculationMatrix[length - i - 1][height - 1]);
                TreeNode lengthNode = new TreeNode(length, height, leftNode.value + rightNode.value - costOfDivision * height);

                lengthNode.addChildren(leftNode, rightNode);
                lengthNode.division(i, true);

                totalCosts.add(lengthNode);
            } else {
                TreeNode leftNode = recExactApproach(i, height, calculationMatrix);
                TreeNode rightNode = recExactApproach(length - i, height, calculationMatrix);
                calculationMatrix[length - i - 1][height - 1] = rightNode;
                calculationMatrix[i - 1][height - 1] = leftNode;

                TreeNode lengthNode = new TreeNode(length, height, rightNode.value + leftNode.value - costOfDivision * length);

                lengthNode.addChildren(leftNode, rightNode);
                lengthNode.division(i, true);


                totalCosts.add(lengthNode);
            }
        }

        for (int i = 1; i < height; i++) {
            if (calculationMatrix[length - 1][height - i - 1] != null && calculationMatrix[length - 1][i - 1] != null) {
                TreeNode leftNode = new TreeNode(calculationMatrix[length - 1][i - 1]);
                TreeNode rightNode = new TreeNode(calculationMatrix[length - 1][height - i - 1]);
                TreeNode heightNode = new TreeNode(length, height, leftNode.value + rightNode.value - costOfDivision * length);

                heightNode.addChildren(leftNode, rightNode);
                heightNode.division(i, false);

                totalCosts.add(heightNode);
            } else {
                TreeNode leftNode = recExactApproach(length, i, calculationMatrix);
                TreeNode rightNode = recExactApproach(length, height - i, calculationMatrix);
                calculationMatrix[length - 1][height - i - 1] = rightNode;
                calculationMatrix[length - 1][i - 1] = leftNode;
                TreeNode heightNode = new TreeNode(length, height, rightNode.value + leftNode.value - costOfDivision * length);

                heightNode.addChildren(leftNode, rightNode);
                heightNode.division(i, false);

                totalCosts.add(heightNode);
            }
        }

        TreeNode maxNode = Collections.max(totalCosts);

        return maxNode;
    }

    private void populateCosts(int length, int width) {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                costs[i][j] = (i + 1 + j + 1) * 4 - (i + 1) % (j + 1) * 2 - (j + 1) % (i + 1) * 2;
            }
        }
    }

    private void printCostTable(int width, int length) {
        System.out.print("   ");
        for (int i = 0; i < width; i++) {
            System.out.printf("%4d", i + 1);
        }

        System.out.println("");

        for (int i = 0; i < length; i++) {
            System.out.printf("%3d:", (i + 1));
            for (int j = 0; j < width; j++) {
                System.out.printf("%4d", costs[i][j]);
            }
            System.out.println("");
        }
    }

    public void setStaticArray() {
        int[][] values = {{9, 14, 7, 26, 44, 13, 46, 20, 74, 93, 44, 58, 123, 122, 109, 44, 121, 153, 152, 68},
        {14, 35, 20, 62, 73, 113, 80, 135, 52, 74, 233, 192, 163, 165, 99, 271, 155, 60, 252, 161},
        {7, 20, 93, 17, 39, 149, 207, 239, 258, 109, 223, 104, 299, 316, 216, 280, 435, 578, 134, 576},
        {26, 62, 17, 56, 78, 98, 246, 64, 291, 69, 121, 263, 131, 392, 529, 264, 194, 386, 620, 256},
        {44, 73, 39, 78, 71, 288, 229, 139, 221, 202, 549, 300, 209, 477, 628, 200, 721, 314, 901, 330},
        {13, 113, 149, 98, 288, 262, 349, 512, 534, 372, 660, 618, 392, 312, 561, 333, 477, 1009, 741, 1149},
        {46, 80, 207, 246, 229, 349, 437, 527, 492, 538, 458, 508, 592, 298, 1141, 1113, 1156, 675, 949, 588},
        {20, 135, 239, 64, 139, 512, 527, 117, 121, 873, 938, 316, 652, 746, 301, 224, 1054, 1079, 1339, 225},
        {74, 52, 258, 291, 221, 534, 492, 121, 487, 295, 265, 949, 1137, 596, 1110, 392, 1203, 775, 588, 533},
        {93, 74, 109, 69, 202, 372, 538, 873, 295, 924, 413, 266, 503, 380, 204, 269, 296, 762, 1694, 1350},
        {44, 233, 223, 121, 549, 660, 458, 938, 265, 413, 942, 1093, 793, 1613, 1302, 828, 1052, 1001, 272, 2105},
        {58, 192, 104, 263, 300, 618, 508, 316, 949, 266, 1093, 588, 949, 1382, 1678, 921, 1709, 1104, 2411, 1245},
        {123, 163, 299, 131, 209, 392, 592, 652, 1137, 503, 793, 949, 254, 1800, 528, 960, 2109, 528, 2399, 2554},
        {122, 165, 316, 392, 477, 312, 298, 746, 596, 380, 1613, 1382, 1800, 610, 2285, 1401, 1562, 318, 967, 2699},
        {109, 99, 216, 529, 628, 561, 1141, 301, 1110, 204, 1302, 1678, 528, 2285, 1587, 2325, 1816, 2585, 726, 688},
        {44, 271, 280, 264, 200, 333, 1113, 224, 392, 269, 828, 921, 960, 1401, 2325, 1953, 636, 3090, 830, 2352},
        {121, 155, 435, 194, 721, 477, 1156, 1054, 1203, 296, 1052, 1709, 2109, 1562, 1816, 636, 742, 2416, 1942, 2701},
        {153, 60, 578, 386, 314, 1009, 675, 1079, 775, 762, 1001, 1104, 528, 318, 2585, 3090, 2416, 1638, 2593, 484},
        {152, 252, 134, 620, 901, 741, 949, 1339, 588, 1694, 272, 2411, 2399, 967, 726, 830, 1942, 2593, 1952, 477},
        {68, 161, 576, 256, 330, 1149, 588, 225, 533, 1350, 2105, 1245, 2554, 2699, 688, 2352, 2701, 484, 477, 3252}};
        costs = values;
    }

    public void setStaticArray2() {
        int[][] values = {{20, 40, 100, 130, 150, 200},
        {40, 140, 250, 320, 400, 450},
        {100, 250, 350, 420, 450, 500},
        {130, 320, 420, 500, 600, 700},
        {150, 400, 450, 600, 700, 800},
        {200, 450, 500, 700, 800, 900}};

        costs = values;
    }

    public static void main(String[] args) {
        Subdivisions sub = new Subdivisions(1000, 1000);

        sub.setStaticArray2();

        sub.printCostTable(6, 6);

        sub.setRect(5, 6, 20);
        System.out.println("");

        long startTime = System.nanoTime();
        System.out.println("");
        System.out.println("Brute Force Cost: " + sub.bruteForceSubDivide());
        long endTime = System.nanoTime();
        long duration1 = (endTime - startTime) / 1000000;

        startTime = System.nanoTime();
        System.out.println("");
        System.out.println("Greedy Cost: " + sub.greedySolution());
        endTime = System.nanoTime();
        long duration2 = (endTime - startTime) / 1000000;

        startTime = System.nanoTime();
        System.out.println("");
        System.out.println("Exact Cost: " + sub.exactApproach());
        endTime = System.nanoTime();
        long duration3 = (endTime - startTime) / 1000000;

        System.out.println("Brute Force method took: " + duration1 + "ms");
        System.out.println("Approximate Greedy method took: " + duration2 + "ms");
        System.out.println("Exact Approach method took: " + duration3 + "ms");
    }
    
    private class Count {
        public int count;
        
        public Count() {
            this.count = 0;
        }
    }
    
    private class TreeNode implements Comparable<TreeNode> {
        public int divisionIndex;
        public boolean lengthWise;

        public int value;
        public int height;
        public int length;
        
        public TreeNode leftNode;
        public TreeNode rightNode;
        
        public int lengthMin;
        public int lengthMax;
        public int heightMin;
        public int heightMax;
        
        public TreeNode(int length, int height, int value) {
            this.value = value;
            this.height = height;
            this.length = length;
        }
        
        public TreeNode(TreeNode node) {
            this.value = node.value;
            this.height = node.height;
            this.length = node.length;

            this.leftNode = node.leftNode;
            this.rightNode = node.rightNode;
            
            this.lengthWise = node.lengthWise;
            this.divisionIndex = node.divisionIndex;
        }
        
        public void addChildren(TreeNode leftChild, TreeNode rightChild) {
            this.leftNode = leftChild;
            this.rightNode = rightChild;
        }
        
        public void division(int index, boolean lengthWise) {
            this.divisionIndex = index;
            this.lengthWise = lengthWise;
        }
        
        @Override
        public int compareTo(TreeNode o) {
            if (value > o.value) {
                return 1;
            }
            if (value < o.value) {
                return -1;
            }
            return 0;
        }
    }
}

import java.io.*;
import java.util.*;


public class RangeTree {
    public Node root;
    public RangeTree(ArrayList<Node> nodes) {
        nodes.sort(Comparator.comparingDouble(o -> o.x));
        root = buildXTree(nodes);
    }
    static class FastReader {
        BufferedReader br;

        StringTokenizer st;

        public FastReader() {
            br = new BufferedReader(new
                    InputStreamReader(System.in));
        }

        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }

        double nextDouble() {
            return Double.parseDouble(next());
        }


    }
    public ArrayList<Node> query(double x1, double x2, double y1, double y2) {
        ArrayList<Node> nodes = new ArrayList<>();
        printInsideNodes(nodes, root, x1, x2, y1, y2);
        ArrayList<Node> leaves = new ArrayList<>();
        for (Node node :
                nodes) {
            printLeaves(leaves, node);
        }
        leaves.sort(Comparator.comparingDouble(o -> o.y));
        return leaves;
    }
    public void printInsideNodes(List<Node> nodes, Node node, double x1, double x2, double y1, double y2) {

        // finding Split Node ...
        while (!node.isExternal && (node.key >= x2 || node.key < x1)) {
            if (x2 <= node.key) {
                node = node.leftChild;
            } else
                node = node.rightChild;
        }
        if (node.isExternal) {
            if (node.x >= x1 && node.x <= x2)
                printInsideNodesY(nodes, node, y1, y2);
        } else {
            // Traverse Left
            Node leftNode = node.leftChild;
            while (!leftNode.isExternal) {
                if (leftNode.key >= x1) {
                    printInsideNodesY(nodes, leftNode.rightChild, y1, y2);
                    leftNode = leftNode.leftChild;
                } else
                    leftNode = leftNode.rightChild;
            }
            if (leftNode.x >= x1 && leftNode.x <= x2)
                printInsideNodesY(nodes, leftNode, y1, y2);

            // Traverse Right
            Node rightNode = node.rightChild;
            while (!rightNode.isExternal) {
                if (rightNode.key <= x2) {
                    printInsideNodesY(nodes, rightNode.leftChild, y1, y2);
                    rightNode = rightNode.rightChild;
                } else
                    rightNode = rightNode.leftChild;
            }
            if (rightNode.x >= x1 && rightNode.x <= x2)
                printInsideNodesY(nodes, rightNode, y1, y2);
        }
    }
    public void printInsideNodesY(List<Node> nodes, Node node, double y1, double y2) {

        // finding Split
        if (node.isExternal) {
            if (node.y >= y1 && node.y <= y2)
                nodes.add(node);
        } else {
            node = node.yTree;
            while (!node.isExternal && (node.key >= y2 || node.key < y1)) {
                if (y2 <= node.key) {
                    node = node.leftChild;
                } else
                    node = node.rightChild;
            }
            if (node.isExternal) {
                if (node.y >= y1 && node.y <= y2)
                    nodes.add(node);
            } else {
                // Traverse Left
                Node leftNode = node.leftChild;
                while (!leftNode.isExternal) {
                    if (leftNode.key >= y1) {
                        nodes.add(leftNode.rightChild);
                        leftNode = leftNode.leftChild;
                    } else
                        leftNode = leftNode.rightChild;
                }
                if (leftNode.y >= y1 && leftNode.y <= y2)
                    nodes.add(leftNode);

                // Traverse Right
                Node rightNode = node.rightChild;
                while (!rightNode.isExternal) {
                    if (rightNode.key <= y2) {
                        nodes.add(rightNode.leftChild);
                        rightNode = rightNode.rightChild;
                    } else
                        rightNode = rightNode.leftChild;
                }
                if (rightNode.y >= y1 && rightNode.y <= y2)
                    nodes.add(rightNode);

            }
        }
    }
    public void printLeaves(List<Node> nodes, Node node) {
        if (!node.isExternal) {
            printLeaves(nodes, node.leftChild);
            printLeaves(nodes, node.rightChild);
        } else
            nodes.add(node);
    }
    public Node buildXTree(List<Node> nodes) {
        int size = nodes.size();
        if (size == 1)
            return nodes.get(0);
        Node leftChild = buildXTree(nodes.subList(0, size / 2));
        Node rightChild = buildXTree(nodes.subList(size / 2, size));
        Node mid = new Node(leftChild, rightChild, biggestX(leftChild));
        nodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return Double.compare(o1.y, o2.y);
            }
        });
        mid.yTree = buildYTree(nodes);
        return mid;
    }
    public Node buildYTree(List<Node> nodes) {
        int size = nodes.size();
        if (size == 1)
            return nodes.get(0);
        Node leftChild = buildYTree(nodes.subList(0, size / 2));
        Node rightChild = buildYTree(nodes.subList(size / 2, size));
        return new Node(leftChild, rightChild, biggestY(leftChild));
    }
    public double biggestX(Node node) {
        while (!node.isExternal)
            node = node.rightChild;
        return node.x;
    }
    public double biggestY(Node node) {
        while (!node.isExternal)
            node = node.rightChild;
        return node.y;

    }
    public static void main(String[] args) throws IOException {
        ArrayList<Node> nodes = new ArrayList<>();
        FastReader fs = new FastReader();
        BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(System.out));

        int count = fs.nextInt();
        for (int i = 0; i < count; i++) {
            nodes.add(new Node(fs.nextDouble(), 0));
        }
        for (int j = 0; j < count; j++) {
            nodes.get(j).y = fs.nextDouble();
        }
        RangeTree rangeTree = new RangeTree(nodes);
        int op = fs.nextInt();
        double x1, x2, y1, y2;
        List<Node> results;
        for (int f = 0; f < op; f++) {
            x1 = fs.nextDouble();
            y1 = fs.nextDouble();
            x2 = fs.nextDouble();
            y2 = fs.nextDouble();
            results = rangeTree.query(x1, x2, y1, y2);
            if (results.size() == 0) {
                bf.write("None" + '\n');
            } else {
                for (Node node :
                        results) {

                    if (node.x == Math.floor(node.x)) {
                        bf.write((int) node.x + " ");
                    } else
                        bf.write(node.x+" ");
                }
                bf.write('\n');
                for (Node node :
                        results) {
                    if (node.y == Math.floor(node.y)) {
                        bf.write((int) node.y+" ");
                    } else
                        bf.write(node.y+" ");
                }
                bf.write("" + '\n');
            }
            bf.flush();
        }
    }
}

class Node {
    public boolean isExternal;
    public double x, y;
    public double key;
    public Node yTree;
    public Node leftChild;
    public Node rightChild;



    public Node(Node leftChild, Node rightChild, double key) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.isExternal = false;
        this.key = key;

        x = y = 0;
    }

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
        isExternal = true;
        yTree = leftChild = rightChild = null;
    }

    @Override
    public String toString() {
        if (isExternal)
            return "X:" + x + " Y:" + y;
        return String.valueOf(key);
    }
}
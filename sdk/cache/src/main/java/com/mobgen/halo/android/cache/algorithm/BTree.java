package com.mobgen.halo.android.cache.algorithm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.NoSuchElementException;

/**
 * jBixbe BTree.
 */
@JsonObject
public class BTree<T extends Comparable<T>> {

    /**
     * Root of the tree.
     */
    @JsonField
    Node mRoot;
    @JsonField
    String mId;

    protected BTree() {
    }

    /**
     * Creates an empty balanced tree.
     */
    public BTree(String id) {
        mRoot = null;
        mId = id;
    }

    /**
     * Creates a balances tree using the given node as tree root.
     */
    public BTree(Node root) {
        this.mRoot = root;
    }

    /**
     * Inserts an element into the tree.
     */
    public void insert(T info) {
        insert(info, mRoot, null, false);
    }

    /**
     * Checks whether the given element is already in the tree.
     */
    public T member(T info) {
        return member(info, mRoot);
    }

    /**
     * Removes an element from the tree.
     */
    public void delete(T info) {
        delete(info, mRoot);
    }

    /**
     * Returns a text representation of the tree.
     */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        inOrder(new TraverseFunction<T>() {
            @Override
            public boolean onTraverse(T item) {
                builder.append(item.toString());
                return false;
            }
        });
        return builder.toString();
    }

    /**
     * Returns all elements of the tree in in-order traversing.
     */
    public void inOrder(TraverseFunction<T> function) {
        inOrder(mRoot, function, false);
    }

    /**
     * Returns the height of the tree.
     */
    public int getHeight() {
        return getHeight(mRoot);
    }

    private void insert(T info, Node node, Node parent, boolean right) {
        if (node == null) {
            if (parent == null) {
                mRoot = node = new Node(info, null);
            } else if (right) {
                parent.mRight = node = new Node(info, parent);
            } else {
                parent.mLeft = node = new Node(info, parent);
            }
            restructInsert(node, false);
        } else if (info.compareTo(node.mInformation) == 0) {
            node.mInformation = info;
        } else if (info.compareTo(node.mInformation) > 0) {
            insert(info, node.mRight, node, true);
        } else {
            insert(info, node.mLeft, node, false);
        }
    }

    private T member(T info, Node node) {
        T member;
        if (node == null) {
            member = null;
        } else if (info.compareTo(node.mInformation) == 0) {
            member = node.mInformation;
        } else if (info.compareTo(node.mInformation) > 0) {
            member = member(info, node.mRight);
        } else {
            member = member(info, node.mLeft);
        }

        return member;
    }

    private void delete(T info, Node node) throws NoSuchElementException {
        if (node == null) {
            throw new NoSuchElementException();
        } else if (info.compareTo(node.mInformation) == 0) {
            deleteNode(node);
        } else if (info.compareTo(node.mInformation) > 0) {
            delete(info, node.mRight);
        } else {
            delete(info, node.mLeft);
        }
    }

    private void deleteNode(Node node) {
        Node eNode, minMaxNode, delNode = null;
        boolean rightNode = false;
        if (node.isLeaf()) {
            if (node.mParent == null) {
                mRoot = null;
            } else if (node.isRightNode()) {
                node.mParent.mRight = null;
                rightNode = true;
            } else if (node.isLeftNode()) {
                node.mParent.mLeft = null;
            }
            delNode = node;
        } else if (node.hasLeftNode()) {
            minMaxNode = node.mLeft;
            for (eNode = node.mLeft; eNode != null; eNode = eNode.mRight) {
                minMaxNode = eNode;
            }
            delNode = minMaxNode;
            node.mInformation = minMaxNode.mInformation;

            if (node.mLeft.mRight != null) {
                minMaxNode.mParent.mRight = minMaxNode.mLeft;
                rightNode = true;
            } else {
                minMaxNode.mParent.mLeft = minMaxNode.mLeft;
            }

            if (minMaxNode.mLeft != null) {
                minMaxNode.mLeft.mParent = minMaxNode.mParent;
            }
        } else if (node.hasRightNode()) {
            minMaxNode = node.mRight;
            delNode = minMaxNode;
            rightNode = true;

            node.mInformation = minMaxNode.mInformation;

            node.mRight = minMaxNode.mRight;
            if (node.mRight != null) {
                node.mRight.mParent = node;
            }
            node.mLeft = minMaxNode.mLeft;
            if (node.mLeft != null) {
                node.mLeft.mParent = node;
            }
        }
        restructDelete(delNode.mParent, rightNode);
    }

    private int getHeight(Node node) {
        int height;
        if (node == null) {
            height = -1;
        } else {
            height = 1 + Math.max(getHeight(node.mLeft), getHeight(node.mRight));
        }
        return height;
    }

    private boolean inOrder(Node node, TraverseFunction<T> function, boolean breakTraverse) {
        boolean shouldBreak = breakTraverse;
        if (shouldBreak) {
            return true;
        }
        if (node != null) {
            shouldBreak = inOrder(node.mLeft, function, false);
            shouldBreak |= function.onTraverse(node.mInformation);
            shouldBreak |= inOrder(node.mRight, function, shouldBreak);
        }
        return shouldBreak;
    }

    private void restructInsert(Node node, boolean wasRight) {
        if (node != mRoot) {
            if (node.mParent.mBalance == '_') {
                if (node.isLeftNode()) {
                    node.mParent.mBalance = '/';
                    restructInsert(node.mParent, false);
                } else {
                    node.mParent.mBalance = '\\';
                    restructInsert(node.mParent, true);
                }
            } else if (node.mParent.mBalance == '/') {
                if (node.isRightNode()) {
                    node.mParent.mBalance = '_';
                } else {
                    if (!wasRight) {
                        rotateRight(node.mParent);
                    } else {
                        doubleRotateRight(node.mParent);
                    }
                }
            } else if (node.mParent.mBalance == '\\') {
                if (node.isLeftNode()) {
                    node.mParent.mBalance = '_';
                } else {
                    if (wasRight) {
                        rotateLeft(node.mParent);
                    } else {
                        doubleRotateLeft(node.mParent);
                    }
                }
            }
        }
    }

    private void restructDelete(@Nullable Node node, boolean wasRight) {
        Node parent;
        boolean isRight = false;
        boolean climb = false;
        boolean canClimb;

        if (node == null) {
            return;
        }

        parent = node.mParent;
        canClimb = (parent != null);

        if (canClimb) {
            isRight = node.isRightNode();
        }

        if (node.mBalance == '_') {
            if (wasRight) {
                node.mBalance = '/';
            } else {
                node.mBalance = '\\';
            }
        } else if (node.mBalance == '/') {
            if (wasRight) {
                if (node.mLeft.mBalance == '\\') {
                    doubleRotateRight(node);
                    climb = true;
                } else {
                    rotateRight(node);
                    if (node.mBalance == '_') {
                        climb = true;
                    }
                }
            } else {
                node.mBalance = '_';
                climb = true;
            }
        } else {
            if (wasRight) {
                node.mBalance = '_';
                climb = true;
            } else {
                if (node.mRight.mBalance == '/') {
                    doubleRotateLeft(node);
                    climb = true;
                } else {
                    rotateLeft(node);
                    if (node.mBalance == '_') {
                        climb = true;
                    }
                }
            }
        }

        if (canClimb && climb) {
            restructDelete(parent, isRight);
        }
    }

    private void rotateLeft(Node node) {
        Node rightNode = node.mRight;

        if (node.mParent == null) {
            mRoot = rightNode;
        } else {
            if (node.isLeftNode()) {
                node.mParent.mLeft = rightNode;
            } else {
                node.mParent.mRight = rightNode;
            }
        }

        node.mRight = rightNode.mLeft;
        if (node.mRight != null) {
            node.mRight.mParent = node;
        }

        rightNode.mParent = node.mParent;
        node.mParent = rightNode;
        rightNode.mLeft = node;

        if (rightNode.mBalance == '_') {
            node.mBalance = '\\';
            rightNode.mBalance = '/';
        } else {
            node.mBalance = '_';
            rightNode.mBalance = '_';
        }
    }

    private void rotateRight(Node node) {
        Node leftNode = node.mLeft;

        if (node.mParent == null) {
            mRoot = leftNode;
        } else {
            if (node.isLeftNode()) {
                node.mParent.mLeft = leftNode;
            } else {
                node.mParent.mRight = leftNode;
            }
        }

        node.mLeft = leftNode.mRight;
        if (node.mLeft != null) {
            node.mLeft.mParent = node;
        }

        leftNode.mParent = node.mParent;
        node.mParent = leftNode;
        leftNode.mRight = node;

        if (leftNode.mBalance == '_') {
            node.mBalance = '/';
            leftNode.mBalance = '\\';
        } else {
            node.mBalance = '_';
            leftNode.mBalance = '_';
        }
    }

    private void doubleRotateLeft(Node node) {
        Node rightNode = node.mRight;
        Node leftNode = rightNode.mLeft;

        if (node.mParent == null) {
            mRoot = leftNode;
        } else {
            if (node.isLeftNode()) {
                node.mParent.mLeft = leftNode;
            } else {
                node.mParent.mRight = leftNode;
            }
        }

        leftNode.mParent = node.mParent;

        node.mRight = leftNode.mLeft;
        if (node.mRight != null) {
            node.mRight.mParent = node;
        }
        rightNode.mLeft = leftNode.mRight;
        if (rightNode.mLeft != null) {
            rightNode.mLeft.mParent = rightNode;
        }

        leftNode.mLeft = node;
        leftNode.mRight = rightNode;

        node.mParent = leftNode;
        rightNode.mParent = leftNode;

        if (leftNode.mBalance == '/') {
            node.mBalance = '_';
            rightNode.mBalance = '\\';
        } else if (leftNode.mBalance == '\\') {
            node.mBalance = '/';
            rightNode.mBalance = '_';
        } else {
            node.mBalance = '_';
            rightNode.mBalance = '_';
        }

        leftNode.mBalance = '_';
    }

    private void doubleRotateRight(Node node) {
        Node leftNode = node.mLeft;
        Node rightNode = leftNode.mRight;

        if (node.mParent == null) {
            mRoot = rightNode;
        } else {
            if (node.isLeftNode()) {
                node.mParent.mLeft = rightNode;
            } else {
                node.mParent.mRight = rightNode;
            }
        }

        rightNode.mParent = node.mParent;

        node.mLeft = rightNode.mRight;
        if (node.mLeft != null) {
            node.mLeft.mParent = node;
        }
        leftNode.mRight = rightNode.mLeft;
        if (leftNode.mRight != null) {
            leftNode.mRight.mParent = leftNode;
        }

        rightNode.mRight = node;
        rightNode.mLeft = leftNode;

        node.mParent = rightNode;
        leftNode.mParent = rightNode;

        if (rightNode.mBalance == '/') {
            leftNode.mBalance = '_';
            node.mBalance = '\\';
        } else if (rightNode.mBalance == '\\') {
            leftNode.mBalance = '/';
            node.mBalance = '_';
        } else {
            leftNode.mBalance = '_';
            node.mBalance = '_';
        }
        rightNode.mBalance = '_';
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @JsonObject
    public class Node {
        @JsonField
        T mInformation;
        @JsonField
        private Node mParent;
        @JsonField
        private Node mLeft;
        @JsonField
        private Node mRight;
        @JsonField
        private char mBalance;

        public Node() {
        }

        public Node(T information, Node parent) {
            mInformation = information;
            mParent = parent;
            mLeft = null;
            mRight = null;
            mBalance = '_';
        }

        boolean isLeaf() {
            return mLeft == null && mRight == null;
        }

        boolean isNode() {
            return !isLeaf();
        }

        boolean hasLeftNode() {
            return null != mLeft;
        }

        boolean hasRightNode() {
            return mRight != null;
        }

        boolean isLeftNode() {
            return mParent.mLeft == this;
        }

        boolean isRightNode() {
            return mParent.mRight == this;
        }
    }

    public interface TraverseFunction<T> {
        boolean onTraverse(T item);
    }
}
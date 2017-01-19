package com.mobgen.halo.android.content.models;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.List;

/**
 * Search query.
 */
@Keep
public class SearchSyntax {
    /**
     * The builder for the query.
     */
    private SearchQuery.Builder mCurrentBuilder;
    /**
     * The listeners.
     */
    private BuildSearchListener mListener;
    /**
     * The infix expression.
     */
    private List<SearchExpression> mInfixExpression;

    /**
     * The search query constructor.
     *
     * @param builder The query builder.
     * @param listener The listener for the query.
     */
    protected SearchSyntax(@NonNull SearchQuery.Builder builder, @NonNull BuildSearchListener listener) {
        mCurrentBuilder = builder;
        mInfixExpression = new ArrayList<>();
        mListener = listener;
    }

    /**
     * And operation.
     *
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax and() {
        mInfixExpression.add(Condition.and());
        return this;
    }

    /**
     * Or operation.
     *
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax or() {
        mInfixExpression.add(Condition.or());
        return this;
    }

    /**
     * Not in operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax in(@NonNull String property, @NonNull List value) {
        mInfixExpression.add(Operator.in(property, value));
        return this;
    }

    /**
     * Not in operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax nin(@NonNull String property, @NonNull List value) {
        mInfixExpression.add(Operator.nin(property, value));
        return this;
    }

    /**
     * Equals to operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax eq(@NonNull String property, @Nullable Object value) {
        mInfixExpression.add(Operator.eq(property, value));
        return this;
    }

    /**
     * Not equals to operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax neq(@NonNull String property, @Nullable Object value) {
        mInfixExpression.add(Operator.neq(property, value));
        return this;
    }

    /**
     * Less than operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax lt(@NonNull String property, @NonNull Number value) {
        mInfixExpression.add(Operator.minor(property, value));
        return this;
    }

    /**
     * Less than operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax lt(@NonNull String property, @NonNull Date value) {
        mInfixExpression.add(Operator.minor(property, value));
        return this;
    }

    /**
     * Less than or equals to operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax lte(@NonNull String property, @NonNull Number value) {
        mInfixExpression.add(Operator.minorEq(property, value));
        return this;
    }

    /**
     * Less than or equals to operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax lte(@NonNull String property, @NonNull Date value) {
        mInfixExpression.add(Operator.minorEq(property, value));
        return this;
    }

    /**
     * Greater than operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax gt(@NonNull String property, @NonNull Number value) {
        mInfixExpression.add(Operator.major(property, value));
        return this;
    }

    /**
     * Greater than operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax gt(@NonNull String property, @NonNull Date value) {
        mInfixExpression.add(Operator.major(property, value));
        return this;
    }

    /**
     * Greater or equals to operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax gte(@NonNull String property, @NonNull Number value) {
        mInfixExpression.add(Operator.majorEq(property, value));
        return this;
    }

    /**
     * Greater or equals to operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax gte(@NonNull String property, @NonNull Date value) {
        mInfixExpression.add(Operator.majorEq(property, value));
        return this;
    }

    /**
     * Like operation.
     *
     * @param property The property.
     * @param value    The value.
     * @return The current query.
     */
    @Keep
    @Api(2.2)
    @NonNull
    public SearchSyntax like(@NonNull String property, @Nullable Object value) {
        mInfixExpression.add(Operator.like(property, value));
        return this;
    }

    /**
     * Begins a group in the query.
     *
     * @return The group to begin.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax beginGroup() {
        mInfixExpression.add(new Parenthesis(true));
        return this;
    }

    /**
     * Ends the group in the query.
     *
     * @return The group in the query.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchSyntax endGroup() {
        mInfixExpression.add(new Parenthesis(false));
        return this;
    }

    /**
     * Ends the search process.
     *
     * @return The search process to end.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public SearchQuery.Builder end() {
        mListener.onBuild(buildCriteria());
        return mCurrentBuilder;
    }

    /**
     * Takes the infix expression and transforms it to polish notation that can
     * be used with the search web service.
     *
     * @return The search expression built.
     */
    @Nullable
    private SearchExpression buildCriteria() {
        SearchExpression finalExpression;
        // Algorithm for postfix expression
        List<SearchExpression> postfixExpression = toPostfixExpression(mInfixExpression);
        finalExpression = toChainedExpression(postfixExpression);
        if (finalExpression != null) {
            finalExpression = simplify(finalExpression);
        }
        return finalExpression;
    }

    /**
     * Provides the chained expression that can be sent to the search web service.
     *
     * @param postfixExpression The postfix expression.
     * @return The search expression created.
     */
    @Nullable
    private SearchExpression toChainedExpression(@NonNull List<SearchExpression> postfixExpression) {
        Deque<SearchExpression> pendingItems = new ArrayDeque<>();
        for (SearchExpression expression : postfixExpression) {
            if (expression instanceof Condition) {
                if (pendingItems.size() < 2) {
                    throw new HaloConfigurationException("The expression is not well-formed.");
                }
                ((Condition) expression).add(pendingItems.pop());
                ((Condition) expression).add(pendingItems.pop());
            }
            pendingItems.push(expression);
        }
        if (pendingItems.size() > 1) {
            throw new HaloConfigurationException("The expression is not well-formed.");
        }
        if (!pendingItems.isEmpty()) {
            return pendingItems.pop();
        }
        return null;
    }

    /**
     * Simplifies the expression as much as possible.
     *
     * @param expression The expression.
     * @return The expression simplified.
     */
    @NonNull
    private SearchExpression simplify(@NonNull SearchExpression expression) {
        if (expression instanceof Condition) {
            Condition condition = (Condition) expression;
            for (int i = 0; i < condition.getItems().size(); i++) {
                SearchExpression expr = condition.getItems().get(i);
                if (expr instanceof Condition) {
                    condition.getItems().add(i, simplify(condition.getItems().remove(i)));
                    if (condition.getName().equals(((Condition) expr).getName())) {
                        condition.getItems().addAll(((Condition) expr).getItems());
                        condition.getItems().remove(i);
                    }
                }
            }
        }
        return expression;
    }

    /**
     * Translate an infix expression into a postfix expression.
     *
     * @param infixExpression The infix expression.
     * @return The list with the postfix expression.
     */
    @NonNull
    private List<SearchExpression> toPostfixExpression(@NonNull List<SearchExpression> infixExpression) {
        // http://faculty.cs.niu.edu/~hutchins/csci241/eval.htm
        Deque<SearchExpression> stack = new ArrayDeque<>();
        List<SearchExpression> postfixExpression = new ArrayList<>();
        for (SearchExpression expression : infixExpression) {
            if (expression instanceof Operator) {
                //Add it to the expression
                postfixExpression.add(expression);
            } else if (expression instanceof Parenthesis) {
                //Is left parenthesis the put in the stack
                if (((Parenthesis) expression).isOpened()) {
                    stack.push(expression);
                } else {
                    //Not is empty and not a left parenthesis
                    while (!stack.isEmpty() &&
                            (!(stack.peek() instanceof Parenthesis) ||
                                    !((Parenthesis) stack.peek()).isOpened())) { //Ensure is not a left parenthesis
                        postfixExpression.add(stack.pop()); //Adds the item to the postfix
                    }
                    if (!stack.isEmpty()) { //There is a mismatching parenthesis with closed but not opened
                        stack.pop(); //Popping the left parenthesis
                    } else {
                        throw new HaloConfigurationException("Malformed expression. There is a mismatching parenthesis");
                    }
                }
            } else if (expression instanceof Condition) {
                //Is empty or left parenthesis
                if (stack.isEmpty() ||
                        (stack.peek() instanceof Parenthesis && ((Parenthesis) stack.peek()).isOpened())) {
                    stack.push(expression);
                } else {
                    //Not is empty and not a left parenthesis
                    while (!stack.isEmpty() &&
                            (!(stack.peek() instanceof Parenthesis) ||
                                    ((Parenthesis) stack.peek()).isOpened())) { //Ensure is not a left parenthesis
                        postfixExpression.add(stack.pop()); //Adds the item to the postfix
                    }
                    stack.push(expression); //Popping the left parenthesis
                }
            }
        }
        while (!stack.isEmpty()) {
            SearchExpression expr = stack.pop();
            if (expr instanceof Parenthesis) {
                throw new HaloConfigurationException("Malformed expression. There is a mismatching parenthesis");
            }
            postfixExpression.add(expr);
        }
        return postfixExpression;
    }

    /**
     * The build search listener.
     */
    @Keep
    public interface BuildSearchListener {
        /**
         * When the build is executed for the given query.
         * @param criteria The criteria.
         */
        void onBuild(@Nullable SearchExpression criteria);
    }
}
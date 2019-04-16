package poly;

import java.util.HashSet;
import java.util.Set;

public class Expression {
    private Set<Term> element = new HashSet<>();

    private boolean isVariable = false;

    public void setVariable() {
        isVariable = true;
    }

    public boolean getVariable() {
        return isVariable;
    }

    public Expression() {
    }

    public Expression(Term term) {
        element.clear();
        element.add(term);
    }

    public void addTerm(Term term) {
        boolean found = false;
        for (Term t : element) {
            if (term.getFactor().equals(t.getFactor())) {
                t.setCoeff(t.getCoeff().add(term.getCoeff()));
                found = true;
                break;
            }
        }
        if (!found) {
            element.add(term);
        }
    }

    public Set<Term> getElement() {
        return element;
    }

    public void addExpression(Expression merge) {
        for (Term term : merge.getElement()) {
            addTerm(term);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Expression)) {
            return false;
        }
        Expression exp = (Expression) o;
        if (isVariable && exp.getVariable()) {
            return true;
        }
        Set<Term> other = exp.getElement();
        for (Term term : element) {
            boolean found = false;
            for (Term next : other) {
                if (term.equals(next)) {
                    found = true;
                }
            }
            if (!found) {
                return false;
            }
        }
        for (Term term : other) {
            boolean found = false;
            for (Term next : element) {
                if (term.equals(next)) {
                    found = true;
                }
            }
            if (!found) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return element.hashCode();
    }

}

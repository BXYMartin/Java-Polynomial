package poly;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Optimizer {
    private Expression polynomial = null;
    public Optimizer(Expression poly) {
        polynomial = poly;
    }


    private boolean optimizeFactor(Factor factor) {
        for (Iterator<Map.Entry<Expression, BigInteger>> it
             = factor.getSinFactor().entrySet().iterator(); it.hasNext();) {
            Map.Entry<Expression, BigInteger> item = it.next();
            if (item.getValue().compareTo(new BigInteger("0")) == 0) {
                it.remove();
                continue;
            }
            if (optimizeExpression(item.getKey())) { return true; }
        }
        for (Iterator<Map.Entry<Expression, BigInteger>> it
             = factor.getVarFactor().entrySet().iterator(); it.hasNext();) {
            Map.Entry<Expression, BigInteger> item = it.next();
            if (item.getValue().compareTo(new BigInteger("0")) == 0) {
                it.remove();
                continue;
            }
            if (optimizeExpression(item.getKey())) { return true; }
        }
        return false;
    }

    private boolean optimizeTerm(Term term) {
        if (term.getCoeff().compareTo(new BigInteger("0")) == 0) {
            return true;
        }
        else {
            return optimizeFactor(term.getFactor());
        }
    }

    private boolean optimizeExpression(Expression exp) {
        if (exp.getVariable()) { return false; }
        Iterator<Term> it = exp.getElement().iterator();
        while (it.hasNext()) {
            Term term = it.next();
            if (optimizeTerm(term)) { it.remove(); }
        }
        if (exp.getElement().size() == 0) {
            exp.addTerm(new Term(new BigInteger("0"), new Factor()));
            return true; }
        return false;
    }


    private boolean mergeTerm(Term term, Set<Term> termVector) {
        for (Term other:termVector) {
            boolean flag = true;
            for (Map.Entry<Expression, BigInteger> entry :
                    other.getFactor().getSinFactor().entrySet()) {
                if(entry.getKey().getVariable()){
                    flag = false;
                }
            }
            for (Map.Entry<Expression, BigInteger> entry :
                    other.getFactor().getCosFactor().entrySet()) {
                if(entry.getKey().getVariable()){
                    flag = false;
                }
            }
            for (Map.Entry<Expression, BigInteger> entry :
                    other.getFactor().getVarFactor().entrySet()) {
                if(entry.getKey().getVariable()){
                    flag = false;
                }
            }
            if(flag) {
                for (Map.Entry<Expression, BigInteger> entry :
                        other.getFactor().getSinFactor().entrySet()) {
                    term.getFactor().insertSin(entry.getKey(), entry.getValue());
                }
                for (Map.Entry<Expression, BigInteger> entry :
                        other.getFactor().getCosFactor().entrySet()) {
                    term.getFactor().insertCos(entry.getKey(), entry.getValue());
                }
                for (Map.Entry<Expression, BigInteger> entry :
                        other.getFactor().getVarFactor().entrySet()) {
                    term.getFactor().insertVar(entry.getKey(), entry.getValue());
                }
                term.setCoeff(term.getCoeff().multiply(other.getCoeff()));
                termVector.clear();
            }
            return flag;
        }
        return true;
    }

    private boolean optimizeClosure(Expression exp) {
        for (Term term : exp.getElement()) {
            boolean flag = true;
            for (Map.Entry<Expression, BigInteger> entry :
                    term.getFactor().getVarFactor().entrySet()) {
                optimizeClosure(entry.getKey());
                if (entry.getKey().getElement().size() > 1) {
                    flag = false;
                }
            }
            if (flag) {
                for (Map.Entry<Expression, BigInteger> entry :
                        term.getFactor().getVarFactor().entrySet()) {
                    if(mergeTerm(term, entry.getKey().getElement())) {
                        term.getFactor().getVarFactor().remove(entry.getKey());
                    }
                }
            }
        }
        return false;
    }


    public Expression runOptimization() {
        optimizeExpression(polynomial);
        optimizeClosure(polynomial);
        return polynomial;
    }

}

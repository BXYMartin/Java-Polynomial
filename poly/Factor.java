package poly;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Factor {
    private Map<Expression, BigInteger> sinFactor = new HashMap<>();
    private Map<Expression, BigInteger> cosFactor = new HashMap<>();
    private Map<Expression, BigInteger> varFactor = new HashMap<>();

    public Factor() {
    }

    public Factor(Map<Expression, BigInteger> sin
            , Map<Expression, BigInteger> cos
            , Map<Expression, BigInteger> var) {
        sinFactor = sin;
        cosFactor = cos;
        varFactor = var;

    }

    public Map<Expression, BigInteger> getSinFactor() {
        return sinFactor;
    }

    public Map<Expression, BigInteger> getCosFactor() {
        return cosFactor;
    }

    public Map<Expression, BigInteger> getVarFactor() {
        return varFactor;
    }

    public void insertSin(Expression exp, BigInteger pow) {
        if (sinFactor.containsKey(exp)) {
            sinFactor.put(exp, sinFactor.get(exp).add(pow));
        } else {
            sinFactor.put(exp, pow);
        }
    }

    public void insertCos(Expression exp, BigInteger pow) {
        if (cosFactor.containsKey(exp)) {
            cosFactor.put(exp, cosFactor.get(exp).add(pow));
        } else {
            cosFactor.put(exp, pow);
        }
    }

    public void insertVar(Expression exp, BigInteger pow) {
        if (varFactor.containsKey(exp)) {
            varFactor.put(exp, varFactor.get(exp).add(pow));
        } else {
            varFactor.put(exp, pow);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Factor)) { return false; }
        Factor i = (Factor) o;

        for (Map.Entry<Expression, BigInteger> entry : sinFactor.entrySet()) {
            if (i.sinFactor.get(entry.getKey()) == null) {
                return false;
            }
            if (sinFactor.get(entry.getKey()).compareTo(
                    i.sinFactor.get(entry.getKey())) != 0) {
                return false;
            }
        }

        for (Map.Entry<Expression, BigInteger> entry : cosFactor.entrySet()) {
            if (i.cosFactor.get(entry.getKey()) == null) {
                return false;
            }
            if (cosFactor.get(entry.getKey()).compareTo(
                    i.cosFactor.get(entry.getKey())) != 0) {
                return false;
            }
        }

        for (Map.Entry<Expression, BigInteger> entry : varFactor.entrySet()) {
            if (i.varFactor.get(entry.getKey()) == null) {
                return false;
            }
            if (varFactor.get(entry.getKey()).compareTo(
                    i.varFactor.get(entry.getKey())) != 0) {
                return false;
            }
        }

        for (Map.Entry<Expression, BigInteger> entry : i.sinFactor.entrySet()) {
            if (sinFactor.get(entry.getKey()) == null) {
                return false;
            }
            if (sinFactor.get(entry.getKey()).compareTo(
                    i.sinFactor.get(entry.getKey())) != 0) {
                return false;
            }
        }

        for (Map.Entry<Expression, BigInteger> entry : i.cosFactor.entrySet()) {
            if (cosFactor.get(entry.getKey()) == null) {
                return false;
            }
            if (cosFactor.get(entry.getKey()).compareTo(
                    i.cosFactor.get(entry.getKey())) != 0) {
                return false;
            }
        }

        for (Map.Entry<Expression, BigInteger> entry : i.varFactor.entrySet()) {
            if (varFactor.get(entry.getKey()) == null) {
                return false;
            }
            if (varFactor.get(entry.getKey()).compareTo(
                    i.varFactor.get(entry.getKey())) != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash * 31 + sinFactor.hashCode();
        hash = hash * 31 + cosFactor.hashCode();
        hash = hash * 31 + varFactor.hashCode();
        return hash;
    }
}

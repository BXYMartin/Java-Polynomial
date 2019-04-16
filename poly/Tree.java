package poly;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tree {
    private Expression polynomial = new Expression();
    private Expression derivation = new Expression();
    private StringBuilder out = new StringBuilder();
    private boolean debug = false;
    private String poly = null;

    private String parseClosure(String target) {
        StringBuilder result = new StringBuilder();
        int level = 0;
        for (int i = 0; i < target.length(); i++) {
            if (target.charAt(i) == '(') {
                if (level == 0) { result.append('<'); }
                else { result.append('('); }
                level++;
            } else if (target.charAt(i) == ')') {
                if (level == 1) {
                    result.append('>');
                } else {
                    result.append(')');
                }
                level--;
            } else {
                result.append(target.charAt(i));
            }
        }
        return result.toString();
    }

    private String uniFormat(String input) {
        String ignoreRegex = "(?=[^>]*(<|$))";
        String target = input;
        target = target.replaceAll("[ \t]" + ignoreRegex, "");
        // Process dual operators
        String signRegex = "\\+-|\\+\\+|-\\+|--";
        Pattern pattern = Pattern.compile(signRegex);
        Matcher matcher = pattern.matcher(target);
        while (matcher.find()) {
            target = target.replaceAll("[+][-]" + ignoreRegex, "\\-");
            target = target.replaceAll("[+][+]" + ignoreRegex, "\\+");
            target = target.replaceAll("[-][-]" + ignoreRegex, "\\+");
            target = target.replaceAll("[-][+]" + ignoreRegex, "\\-");
        }
        // Process abbreviated symbols
        target = target.replaceAll("[-]sin" + ignoreRegex
                , "-1*sin");
        target = target.replaceAll("[+]sin|^sin" + ignoreRegex
                , "+1*sin");
        target = target.replaceAll("[-]cos" + ignoreRegex
                , "-1*cos");
        target = target.replaceAll("[+]cos|^cos" + ignoreRegex
                , "+1*cos");
        target = target.replaceAll("[-]x" + ignoreRegex
                , "-1*x");
        target = target.replaceAll("[+]x|^x" + ignoreRegex
                , "+1*x");
        target = target.replaceAll("[+]<|^<", "+1*<");
        target = target.replaceAll("[-]<", "-1*<");
        return target;
    }

    private boolean enterFactor(Factor temp, Matcher term) {
        String subRegex = "<([^>]+)>";
        BigInteger power = new BigInteger("0");
        Expression inside = null;
        Pattern sub = Pattern.compile(subRegex);
        if (term.group("power") != null && new BigInteger(term.group(3))
                .compareTo(new BigInteger("10000")) > 0) {
            System.out.println("WRONG FORMAT!");
            System.exit(0);
        }
        if (term.group("factor").startsWith("sin")) {
            if (term.group("power") != null) {
                power = power.add(new BigInteger(term.group(3)));
            } else { power = power.add(new BigInteger("1")); }
            Matcher exp = sub.matcher(term.group(0));
            if (exp.find()) {
                inside = new Expression();
                enterExpression(inside, exp.group(1));
            } else { System.out.println("Unknown Error"); }
            temp.insertSin(inside, power);
        } else if (term.group("factor").startsWith("cos")) {
            if (term.group("power") != null) {
                power = power.add(new BigInteger(term.group(3)));
            } else {
                power = power.add(new BigInteger("1"));
            }
            Matcher exp = sub.matcher(term.group(0));
            if (exp.find()) {
                inside = new Expression();
                enterExpression(inside, exp.group(1));
            } else {
                System.out.println("Unknown Error");
            }
            temp.insertCos(inside, power);
        } else if (term.group("factor").startsWith("x")) {
            if (term.group("power") != null) {
                power = power.add(new BigInteger(term.group(3)));
            } else {
                power = power.add(new BigInteger("1"));
            }
            inside = new Expression();
            enterExpression(inside, "x");
            temp.insertVar(inside, power);
        } else if (term.group("factor").startsWith("<")) {
            if (term.group("power") != null) {
                power = power.add(new BigInteger(term.group(3)));
            } else {
                power = power.add(new BigInteger("1"));
            }
            Matcher exp = sub.matcher(term.group(0));
            if (exp.find()) {
                inside = new Expression();
                enterExpression(inside, "(" + exp.group(1) + ")");
            } else {
                System.out.println("Unknown Error");
            }
            temp.insertVar(inside, power);
        }
        return false;
    }

    private void enterTerm(Term t, String target) {
        String factorRegex = "(?<factor>sin<[^>]+>|cos<[^>]+>|x|<[^>]+>)" +
                "(\\^(?<power>[+-]?\\d+))?|(?<coeff>[-+]?\\d+)";
        BigInteger coeff = new BigInteger("1");
        Pattern pattern = Pattern.compile(factorRegex);
        Matcher term = pattern.matcher(target);
        Factor factor = new Factor();
        while (term.find()) {
            if (debug) {
                System.out.println("[%] Extracted Term " + term.group(0));
            }
            if (term.group("factor") != null) {
                enterFactor(factor, term);
            } else if (term.group("coeff") != null) {
                coeff = coeff.multiply(new BigInteger(term.group("coeff")));
            } else {
                System.out.println("Unknown Error!");
            }
        }
        t.setCoeff(coeff);
        t.setFactor(factor);
    }

    private void enterExpression(Expression exp, String target) {
        String processed = parseClosure(target);
        if (checkFoolish(processed)) {
            String subRegex = "^[ \t]*<([^>]+)>[ \t]*$";
            Pattern sub = Pattern.compile(subRegex);
            Matcher str = sub.matcher(processed);
            if (str.find()) {
                processed = str.group(1);
                processed = parseClosure(processed);
            }
        }
        else {
            System.out.println("WRONG FORMAT!");
            System.exit(0);
        }
        if (checkFormat(processed)) {
            if (target.equals("x")) {
                exp.setVariable();
                return;
            }
            processed = uniFormat(processed);

            String termRegex = "((sin<[^>]+>|cos<[^>]+>|x|<[^>]+>)(" +
                    "\\^[+-]?\\d+)?|([-+]?\\d+))" +
                    "(\\*((sin<[^>]+>|cos<[^>]+>|x|<[^>]+>)(" +
                    "\\^[+-]?\\d+)?|([-+]?\\d+)))*";

            Pattern pattern = Pattern.compile(termRegex);
            Matcher matcher = pattern.matcher(processed);
            while (matcher.find()) {
                if (debug) {
                    System.out.println("[*] Group: " + matcher.group(0));
                }
                Term term = new Term();
                enterTerm(term, matcher.group(0));
                exp.addTerm(term);
            }

        } else {
            System.out.println("WRONG FORMAT!");
            System.exit(0);
        }
    }

    // Replace \d+ with (\d{1,4}|[0]+\d{0,4})
    private boolean checkFoolish(String target) {
        final String polyTerm = "[ \t]*(sin[ \t]*<[^>]+>" +
                "|cos[ \t]*<[^>]+>|x)[ \t]*";
        final String polyRegex = "^" +
                "[ \t]*<[^>]+>[ \t]*" +
                "|[ \t]*(" + polyTerm + "(" +
                "\\^[ \t]*[+-]?\\d+)?|([-+]?\\d+))[ \t]*" +
                "$";
        Pattern pattern = Pattern.compile(polyRegex);
        Matcher matcher = pattern.matcher(target);
        return matcher.matches();
    }

    // Replace \d+ with (\d{1,4}|[0]+\d{0,4})
    private boolean checkFormat(String target) {
        final String polyTerm = "[ \t]*(sin[ \t]*<[^>]+>" +
                "|cos[ \t]*<[^>]+>|x)[ \t]*";
        final String polyRegex = "^" +
                "[ \t]*[-+]?[ \t]*[-+]?[ \t]*(" +
                "(" + polyTerm + "(" +
                "\\^[ \t]*[+-]?\\d+)?|([-+]?\\d+)|<[^>]+>)[ \t]*" +
                "([ \t]*\\*[ \t]*" +
                "(" + polyTerm + "(" +
                "\\^[ \t]*[+-]?\\d+)?|([-+]?\\d+)|<[^>]+>)[ \t]*)*)" +
                "([ \t]*[-+][ \t]*[-+]?[ \t]*(" +
                "(" + polyTerm + "(" +
                "\\^[ \t]*[+-]?\\d+)?|([-+]?\\d+)|<[^>]+>))" +
                "([ \t]*\\*[ \t]*" +
                "(" + polyTerm + "(" +
                "\\^[ \t]*[+-]?\\d+)?|([-+]?\\d+)|<[^>]+>)[ \t]*)*[ \t]*)*" +
                "$";
        Pattern pattern = Pattern.compile(polyRegex);
        Matcher matcher = pattern.matcher(target);
        return matcher.matches();
    }

    public void buildTree(String target) {
        poly = "(" + target + ")";
        enterExpression(polynomial, poly);
    }

    private void mergePut(Map<Expression, BigInteger> map
            , Expression exp, BigInteger pow) {
        if (pow.compareTo(new BigInteger("0")) == 0) {
            return;
        }
        if (map.get(exp) != null) {
            map.put(exp, map.get(exp).add(pow));
        } else { map.put(exp, pow); }
    }

    private void mergePutAll(Map<Expression, BigInteger> map
            , Map<Expression, BigInteger> other) {
        for (Map.Entry<Expression, BigInteger> entry : other.entrySet()) {
            mergePut(map, entry.getKey(), entry.getValue());
        }
    }

    private void derivateSet(char t, Object[] set
            , Factor factor, Expression diff) {
        for (int i = 0; i < set.length; i++) {
            BigInteger coeff = new BigInteger("1");
            Map<Expression, BigInteger> sinFactor = new HashMap<>();
            Map<Expression, BigInteger> cosFactor = new HashMap<>();
            Map<Expression, BigInteger> varFactor = new HashMap<>();
            Map.Entry<Expression, BigInteger> entry =
                    (Map.Entry<Expression, BigInteger>) set[i];
            coeff = coeff.multiply(entry.getValue());
            if (t == 's') {
                mergePut(sinFactor, entry.getKey(), entry.getValue()
                        .subtract(new BigInteger("1")));
                mergePut(cosFactor, entry.getKey(), new BigInteger("1"));
            }
            else if (t == 'c') {
                mergePut(cosFactor, entry.getKey(), entry.getValue()
                        .subtract(new BigInteger("1")));
                mergePut(sinFactor, entry.getKey(), new BigInteger("1"));
                coeff = coeff.multiply(new BigInteger("-1"));
            }
            else {
                mergePut(varFactor, entry.getKey(), entry.getValue()
                        .subtract(new BigInteger("1"))); }

            if (!entry.getKey().getVariable()) {
                Expression inside = new Expression();
                derivateExpression(entry.getKey(), inside);
                if (inside.getElement().size() <= 1) {
                    for (Term term:inside.getElement()) {
                        coeff = coeff.multiply(term.getCoeff());
                        mergePutAll(sinFactor, term.getFactor().getSinFactor());
                        mergePutAll(cosFactor, term.getFactor().getCosFactor());
                        mergePutAll(varFactor, term.getFactor().getVarFactor());
                    }
                }
                else { mergePut(varFactor, inside, new BigInteger("1")); }
            }
            for (int j = 0; j < set.length; j++) {
                if (i == j) { continue; }
                Map.Entry<Expression, BigInteger> other =
                        (Map.Entry<Expression, BigInteger>) set[j];
                if (t == 's') {
                    mergePut(sinFactor, other.getKey(), other.getValue()); }
                else if (t == 'c') {
                    mergePut(cosFactor, other.getKey(), other.getValue()); }
                else { mergePut(varFactor, other.getKey(), other.getValue()); }
            }
            if (t != 'c') { mergePutAll(cosFactor, factor.getCosFactor()); }
            if (t != 's') { mergePutAll(sinFactor, factor.getSinFactor()); }
            if (t != 'v') { mergePutAll(varFactor, factor.getVarFactor()); }
            Factor diffFactor = new Factor(sinFactor, cosFactor, varFactor);
            Term term = new Term(coeff, diffFactor);
            diff.addTerm(term);
        }
    }

    private void derivateFactor(Factor factor, Expression diff) {
        Object[] sinSet = factor.getSinFactor().entrySet().toArray();
        derivateSet('s', sinSet, factor, diff);
        Object[] cosSet = factor.getCosFactor().entrySet().toArray();
        derivateSet('c', cosSet, factor, diff);
        Object[] varSet = factor.getVarFactor().entrySet().toArray();
        derivateSet('v', varSet, factor, diff);
        if (diff.getElement().size() == 0) {
            diff.addTerm(new Term());
        }
    }

    private void derivateTerm(Term term, Expression diff) {
        BigInteger coeff = term.getCoeff();
        Expression exp = new Expression();
        derivateFactor(term.getFactor(), exp);
        for (Term t : exp.getElement()) {
            t.setCoeff(t.getCoeff().multiply(coeff));
        }
        diff.addExpression(exp);
    }

    private void derivateExpression(Expression exp, Expression diff) {
        if (exp.getVariable()) {
            Term term = new Term();
            term.setCoeff(new BigInteger("1"));
            diff.addTerm(term);
            return;
        }
        for (Term term : exp.getElement()) {
            Expression termDerivation = new Expression();
            derivateTerm(term, termDerivation);
            diff.addExpression(termDerivation);
        }
    }

    public void derivateTree() {
        derivateExpression(polynomial, derivation);
    }

    private void printInside(Expression exp) {
        int size = 0;
        boolean getRid = exp.getVariable();
        for (Term term:exp.getElement()) {
            size += term.getFactor().getSinFactor().size() +
                    term.getFactor().getCosFactor().size() +
                    term.getFactor().getVarFactor().size();
            getRid = (exp.getVariable() ||
                    (exp.getElement().size() == 1
                            && (size == 0 || (size == 1 && term.getCoeff()
                            .compareTo(new BigInteger("1")) == 0))));
        }
        if (!getRid) { out.append("("); }
        printExpression(exp);
        if (!getRid) { out.append(")"); }
    }

    private void printFactor(Factor factor, boolean first) {
        boolean omit = first;
        for (Map.Entry<Expression, BigInteger> entry :
                factor.getSinFactor().entrySet()) {
            if (!omit) { out.append("*"); }
            out.append("sin(");
            printInside(entry.getKey());
            out.append(")");
            if (entry.getValue().compareTo(new BigInteger("1")) != 0) {
                out.append("^" + entry.getValue());
            }
            omit = false;
        }
        for (Map.Entry<Expression, BigInteger> entry :
                factor.getCosFactor().entrySet()) {
            if (!omit) { out.append("*"); }
            out.append("cos(");
            printInside(entry.getKey());
            out.append(")");
            if (entry.getValue().compareTo(new BigInteger("1")) != 0) {
                out.append("^" + entry.getValue());
            }
            omit = false;
        }
        for (Map.Entry<Expression, BigInteger> entry :
                factor.getVarFactor().entrySet()) {
            if (!omit) { out.append("*"); }
            if (entry.getKey().getVariable()) { out.append("x"); } else {
                out.append("(");
                printExpression(entry.getKey());
                out.append(")");
            }
            if (entry.getValue().compareTo(new BigInteger("1")) != 0) {
                out.append("^" + entry.getValue());
            }
            omit = false;
        }
        if (omit) { out.append("1"); }
    }

    private void printTerm(Term term, boolean first) {
        boolean omit = false;
        if (first && term.getCoeff().compareTo(new BigInteger("0")) >= 0) {
            if (term.getCoeff().compareTo(new BigInteger("1")) == 0) {
                omit = true;
            }
            else { out.append(term.getCoeff()); }
        }
        else if (term.getCoeff().compareTo(new BigInteger("1")) == 0) {
            out.append("+");
            omit = true;
        }
        else if (term.getCoeff().compareTo(new BigInteger("-1")) == 0) {
            out.append("-");
            omit = true;
        }
        else if (term.getCoeff().compareTo(new BigInteger("0")) >= 0) {
            out.append("+");
            out.append(term.getCoeff());
        } else { out.append(term.getCoeff()); }
        printFactor(term.getFactor(), omit);

    }

    private void printExpression(Expression exp) {
        if (exp.getVariable()) { out.append("x"); } else {
            boolean first = true;
            for (Term term : exp.getElement()) {
                printTerm(term, first);
                first = false;
            }
        }
    }

    public String printTree() {
        printExpression(derivation);
        if (out.length() == 0) { out.append(0); }
        String result = out.toString();
        return result;
    }


    public void optimizeTree() {
        Optimizer op = new Optimizer(derivation);
        derivation = op.runOptimization();
    }
}

package poly;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Polynomial {
    // Variables
    private boolean debug = false;
    private String polyExpression = null;
    private String shortExpression = null;
    private Map<Factor, BigInteger> valuePair = new HashMap<>();
    private Map<Factor, BigInteger> derivatePair = new HashMap<>();
    private Map<Factor, BigInteger> betterPair = new HashMap<>();
    private Map<Factor, BigInteger> tempPair = new HashMap<>();
    private Map<String, Vector<Term>> reduceList = new HashMap<>();
    private Map<String, BigInteger> sinMin = new HashMap<>();
    private Map<String, BigInteger> cosMin = new HashMap<>();
    private Term con = new Term(new BigInteger("1"), new Factor());
    private Expression sinReplacement = new Expression(con);
    private Term sinSquare = new Term(new BigInteger("-1"),
            new Factor(new BigInteger("0"),
                    new BigInteger("0"), new BigInteger("2")));

    private Expression cosReplacement = new Expression(con);
    private Term cosSquare = new Term(new BigInteger("-1"),
            new Factor(new BigInteger("0"),
                    new BigInteger("2"), new BigInteger("0")));

    // Debug activator
    public void setDebug() {
        debug = true;
    }

    public boolean isDebug() {
        return debug;
    }

    // Constructor
    public Polynomial(String targetPolyString) {
        polyExpression = targetPolyString;
    }

    // Format checker
    public boolean checkFormat() {
        // Check regex
        final String polyRegex = "^" +
                "[ \t]*[-+]?[ \t]*[-+]?[ \t]*(" +
                "((sin[ \t]*\\(x\\)|cos[ \t]*\\(x\\)|x)[ \t]*(" +
                "\\^[ \t]*[+-]?\\d+)?|([-+]?\\d+))" +
                "([ \t]*\\*[ \t]*" +
                "((sin[ \t]*\\(x\\)|cos[ \t]*\\(x\\)|x)[ \t]*(" +
                "\\^[ \t]*[+-]?\\d+)?|([-+]?\\d+)))*)" +
                "([ \t]*[-+][ \t]*[-+]?[ \t]*(" +
                "((sin[ \t]*\\(x\\)|cos[ \t]*\\(x\\)|x)[ \t]*(" +
                "\\^[ \t]*[+-]?\\d+)?|([-+]?\\d+)))" +
                "([ \t]*\\*[ \t]*" +
                "((sin[ \t]*\\(x\\)|cos[ \t]*\\(x\\)|x)[ \t]*(" +
                "\\^[ \t]*[+-]?\\d+)?|([-+]?\\d+)))*)*" +
                "[ \t]*$";

        return matchRegex(polyRegex, polyExpression);
    }

    // Regex checker
    private static boolean matchRegex(String regex, String target) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);
        return matcher.matches();
    }

    private void insertTerm(boolean derivation
            , BigInteger coeff, Factor factor) {
        BigInteger origin = null;
        if (derivation) {
            if (derivatePair.get(factor) == null) {
                origin = new BigInteger("0");
            } else {
                origin = derivatePair.get(factor);
            }
            derivatePair.put(factor, origin.add(coeff));
        } else {
            if (valuePair.get(factor) == null) {
                origin = new BigInteger("0");
            } else {
                origin = valuePair.get(factor);
            }
            valuePair.put(factor, origin.add(coeff));
        }
    }

    private void derivateTerm(Term term) {
        BigInteger minus = new BigInteger("-1");
        Factor xfactor = new Factor(term.getFactor().getxPower().add(minus),
                term.getFactor().getSinPower(), term.getFactor().getCosPower());
        BigInteger xcoeff = term.getCoeff()
                .multiply(term.getFactor().getxPower());
        Factor sinFactor = new Factor(term.getFactor().getxPower(),
                term.getFactor().getSinPower().add(minus),
                term.getFactor().getCosPower().subtract(minus));
        BigInteger sinCoeff = term.getCoeff()
                .multiply(term.getFactor().getSinPower());
        Factor cosFactor = new Factor(term.getFactor().getxPower(),
                term.getFactor().getSinPower().subtract(minus),
                term.getFactor().getCosPower().add(minus));
        BigInteger cosCoeff = term.getCoeff()
                .multiply(term.getFactor().getCosPower()).multiply(minus);
        insertTerm(true, xcoeff, xfactor);
        insertTerm(true, sinCoeff, sinFactor);
        insertTerm(true, cosCoeff, cosFactor);
    }

    private void derivatePolynomial() {
        Iterator it = valuePair.keySet().iterator();
        // Get iterator
        while (it.hasNext()) {
            Object key = it.next();
            derivateTerm(new Term(valuePair.get(key), (Factor) key));
        }
    }

    private String getTest(Map<Factor, BigInteger> temp) {
        boolean isFirst = true;
        Map<Factor, BigInteger> test = new HashMap<>(temp);
        StringBuilder target = new StringBuilder();
        Iterator dit = test.keySet().iterator();
        while (dit.hasNext()) {
            Object key = dit.next();
            BigInteger coeff = test.get(key);
            if (coeff.compareTo(new BigInteger("0")) > 0) {
                target.append(((Factor) key).print(isFirst, coeff));
                isFirst = false;
                test.remove(key);
                break;
            }
        }
        // - -2 * sin(x)     - +001 * x * sin(x) * cos(x) * sin(x) ^   -2
        dit = test.keySet().iterator();
        while (dit.hasNext()) {
            Object key = dit.next();
            BigInteger coeff = test.get(key);
            target.append(((Factor) key).print(isFirst, coeff));
            if (!coeff.equals(new BigInteger("0"))) {
                isFirst = false;
            }
        }
        if (isFirst) {
            target.append("0");
        }
        return target.toString();
    }

    private String getDerivation() {
        boolean isFirst = true;
        StringBuilder target = new StringBuilder();
        Iterator dit = derivatePair.keySet().iterator();
        while (dit.hasNext()) {
            Object key = dit.next();
            BigInteger coeff = derivatePair.get(key);
            if (coeff.compareTo(new BigInteger("0")) > 0) {
                target.append(((Factor) key).print(isFirst, coeff));
                isFirst = false;
                derivatePair.remove(key);
                break;
            }
        }
        // - -2 * sin(x)     - +001 * x * sin(x) * cos(x) * sin(x) ^   -2
        dit = derivatePair.keySet().iterator();
        while (dit.hasNext()) {
            Object key = dit.next();
            BigInteger coeff = derivatePair.get(key);
            target.append(((Factor) key).print(isFirst, coeff));
            if (!coeff.equals(new BigInteger("0"))) {
                isFirst = false;
            }
        }
        if (isFirst) {
            target.append("0");
        }
        return target.toString();
    }

    private void processMatcher(Matcher matcher) {
        BigInteger coeff = new BigInteger("1");
        Factor temp = new Factor();
        String termRegex = "(?<factor>sin\\(x\\)|cos\\(x\\)|x)" +
                "(\\^(?<power>[+-]?\\d+))?|(?<coeff>[-+]?\\d+)";
        Pattern pattern = Pattern.compile(termRegex);
        Matcher term = pattern.matcher(matcher.group(0));
        while (term.find()) {
            if (debug) {
                System.out.println("[%] Extracted Term " + term.group(0));
            }
            if (term.group("factor") != null) {
                if (term.group("factor").equals("sin(x)")) {
                    if (term.group("power") != null) {
                        temp.setSinPower(new BigInteger(term.group(3))
                                .add(temp.getSinPower()));
                    } else {
                        temp.setSinPower(new BigInteger("1")
                                .add(temp.getSinPower()));
                    }
                } else if (term.group("factor").equals("cos(x)")) {
                    if (term.group("power") != null) {
                        temp.setCosPower(new BigInteger(term.group(3))
                                .add(temp.getCosPower()));
                    } else {
                        temp.setCosPower(new BigInteger("1")
                                .add(temp.getCosPower()));
                    }
                } else if (term.group("factor").equals("x")) {
                    if (term.group("power") != null) {
                        temp.setxPower(new BigInteger(term.group(3))
                                .add(temp.getxPower()));
                    } else {
                        temp.setxPower(new BigInteger("1")
                                .add(temp.getxPower()));
                    }
                }
            } else if (term.group("coeff") != null) {
                coeff = coeff.multiply(new BigInteger(term.group("coeff")));
            } else {
                System.out.println("Unknown Error!");
            }
        }
        insertTerm(false, coeff, temp);
    }

    private void preProcess() {
        // Process spaces
        polyExpression = polyExpression.replaceAll("[ \t]", "");
        // Process dual operators
        String signRegex = "\\+-|\\+\\+|-\\+|--";
        Pattern pattern = Pattern.compile(signRegex);
        Matcher matcher = pattern.matcher(polyExpression);
        while (matcher.find()) {
            polyExpression = polyExpression.replaceAll("[+][-]", "\\-");
            polyExpression = polyExpression.replaceAll("[+][+]", "\\+");
            polyExpression = polyExpression.replaceAll("[-][-]", "\\+");
            polyExpression = polyExpression.replaceAll("[-][+]", "\\-");
        }
        // Process abbreviated symbols
        polyExpression = polyExpression.replaceAll("[-]sin\\(x\\)"
                , "-1*sin\\(x\\)");
        polyExpression = polyExpression.replaceAll("[+]sin\\(x\\)|^sin\\(x\\)"
                , "+1*sin\\(x\\)");
        polyExpression = polyExpression.replaceAll("[-]cos\\(x\\)"
                , "-1*cos\\(x\\)");
        polyExpression = polyExpression.replaceAll("[+]cos\\(x\\)|^cos\\(x\\)"
                , "+1*cos\\(x\\)");
        polyExpression = polyExpression.replaceAll("[-]x"
                , "-1*x");
        polyExpression = polyExpression.replaceAll("[+]x|^x"
                , "+1*x");
    }

    private void partialRun(long startTime, BigInteger sinIter,
                            BigInteger cosIter, int pos, Vector<Term> poly,
                            Map<Factor, BigInteger> test) {
        if (pos >= poly.size()) {
            if (!tempPair.isEmpty()) {
                if (getTest(test).length() < getTest(tempPair).length()) {
                    tempPair = new HashMap<>(test); }
            } else { tempPair = new HashMap<>(test); }
            if (debug) { System.out.println(getTest(test)); }
        } else {
            Term entry = poly.elementAt(pos);
            for (BigInteger i = new BigInteger("0");
                 i.equals(new BigInteger("0")) ||
                         i.compareTo(entry.getFactor()
                                 .getSinPower().subtract(sinIter)
                                 .divide(new BigInteger("2"))) <= 0;
                 i = i.add(new BigInteger("1"))) {
                for (BigInteger j = new BigInteger("0");
                     j.equals(new BigInteger("0")) ||
                             j.compareTo(entry.getFactor()
                                     .getCosPower().subtract(cosIter)
                                     .divide(new BigInteger("2"))) <= 0;
                     j = j.add(new BigInteger("1"))) {

                    if (System.nanoTime() - startTime > 8.0e8) { return; }
                    Factor reduced = new Factor(entry.getFactor().getxPower(),
                            entry.getFactor().getSinPower(),
                            entry.getFactor().getCosPower());
                    reduced.setSinPower(reduced.getSinPower()
                            .subtract(i.multiply(new BigInteger("2"))));
                    reduced.setCosPower(reduced.getCosPower()
                            .subtract(j.multiply(new BigInteger("2"))));
                    Accumulation acc = new Accumulation(new Expression(
                            new Term(entry.getCoeff(), reduced)));

                    for (BigInteger k = new BigInteger("0");
                         k.compareTo(i) < 0;
                         k = k.add(new BigInteger("1"))) {
                        acc.addExpression(sinReplacement);
                    }

                    for (BigInteger l = new BigInteger("0");
                         l.compareTo(j) < 0;
                         l = l.add(new BigInteger("1"))) {
                        acc.addExpression(cosReplacement);
                    }
                    Vector<Term> result = acc.calcExpression();
                    Map<Factor, BigInteger> temp = new HashMap<>(test);
                    for (int k = 0; k < result.size(); k++) {
                        Factor factor = result.elementAt(k).getFactor();
                        BigInteger coeff = result.elementAt(k).getCoeff();
                        BigInteger origin;
                        if (temp.get(factor) == null) {
                            origin = new BigInteger("0");
                        } else { origin = temp.get(factor); }
                        temp.put(factor, origin.add(coeff));
                    }
                    partialRun(startTime, sinIter, cosIter
                            , pos + 1, poly, temp);
                }
            }
        }
    }

    private void reductPartial() {
        Iterator<Map.Entry<Factor, BigInteger>> it =
                derivatePair.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Factor, BigInteger> entry = it.next();
            if (!entry.getValue().equals(new BigInteger("0"))) {
                Term term = new Term(entry.getValue(), entry.getKey());
                if (reduceList.containsKey(entry.getKey()
                        .getxPower().toString())) {
                    reduceList.get(entry.getKey()
                            .getxPower().toString()).add(term);
                } else {
                    Vector<Term> init = new Vector<>();
                    init.add(term);
                    reduceList.put(entry.getKey().getxPower().toString(), init);
                }
                if (sinMin.get(entry.getKey().getxPower().toString()) == null ||
                        sinMin.get(entry.getKey().getxPower().toString())
                                .compareTo(entry.getKey().getSinPower()) > 0) {
                    sinMin.put(entry.getKey().getxPower().toString(),
                            entry.getKey().getSinPower());
                }
                if (cosMin.get(entry.getKey().getxPower().toString()) == null ||
                        cosMin.get(entry.getKey().getxPower().toString())
                                .compareTo(entry.getKey().getCosPower()) > 0) {
                    cosMin.put(entry.getKey().getxPower().toString(),
                            entry.getKey().getCosPower());
                }
            }
        }
        sinReplacement.addTerm(sinSquare);
        cosReplacement.addTerm(cosSquare);
        Iterator<Map.Entry<String, Vector<Term>>> search =
                reduceList.entrySet().iterator();
        while (search.hasNext()) {
            Map.Entry<String, Vector<Term>> entry = search.next();
            Vector<Term> reduce = entry.getValue();
            if (reduce.size() > 10 || reduce.size() < 2) {
                for (int i = 0; i < reduce.size(); i++) {
                    betterPair.put(reduce.elementAt(i)
                            .getFactor(), reduce.elementAt(i).getCoeff());
                }
            }
            else {
                Map<Factor, BigInteger> test = new HashMap<>();
                tempPair = new HashMap<>();
                partialRun(System.nanoTime(), sinMin.get(entry.getKey()),
                        cosMin.get(entry.getKey()), 0, reduce, test);
                if (tempPair.isEmpty()) {
                    for (int i = 0; i < reduce.size(); i++) {
                        betterPair.put(reduce.elementAt(i).getFactor(),
                                reduce.elementAt(i).getCoeff());
                    } }
                else {
                    Iterator<Map.Entry<Factor, BigInteger>> get =
                            tempPair.entrySet().iterator();
                    while (get.hasNext()) {
                        Map.Entry<Factor, BigInteger> item = get.next();
                        betterPair.put(item.getKey(), item.getValue());
                    } } } } }

    public String getResult() {

        String polyCleanRegex =
                "((sin\\(x\\)|cos\\(x\\)|x)(" +
                        "\\^[+-]?\\d+)?|([-+]?\\d+))" +
                        "(\\*((sin\\(x\\)|cos\\(x\\)|x)(" +
                        "\\^[+-]?\\d+)?|([-+]?\\d+)))*";

        preProcess();

        Pattern pattern = Pattern.compile(polyCleanRegex);
        Matcher matcher = pattern.matcher(polyExpression);
        while (matcher.find()) {
            if (debug) {
                System.out.println("[*] Group: " + matcher.group(0));
            }
            processMatcher(matcher);
        }
        if (debug) {
            System.out.println("[!] Before Processing...");
            Iterator it = valuePair.keySet().iterator();
            // Get iterator
            while (it.hasNext()) {
                Object key = it.next();
                System.out.println(
                        "[i] Term:" + ((Factor) key).peek(valuePair.get(key)));
            }
        }
        derivatePolynomial();
        if (debug) {
            System.out.println("[!] After Processing...");
            Iterator dit = derivatePair.keySet().iterator();
            // Get iterator
            while (dit.hasNext()) {
                Object key = dit.next();
                System.out.println("[i] Term:" +
                        ((Factor) key).peek(derivatePair.get(key)));
            }
        }
        /*
        reductPolynomial();
        if (shortExpression != null) {
            return shortExpression;
        } else {
            return getDerivation();
        }
        */
        reductPartial();
        return getTest(betterPair);
        //return getDerivation();

    }

}

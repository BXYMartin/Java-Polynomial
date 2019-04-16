package poly;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Polynomial {
    // Variables
    private boolean debug = false;
    private String polyExpression = null;
    private Map valuePair = new HashMap();
    private Map derivatePair = new HashMap();

    // Debug activator
    public void setDebug() {
        debug = true;
    }

    // Constructor
    public Polynomial(String targetPolyString) {
        polyExpression = targetPolyString;
    }

    // Format checker
    public boolean checkFormat() {
        // Check regex expression
        final String polyRegex = "^" +
                "[ \t]*[-+]?[ \t]*(" +
                "[ \t]*[-+]?[0-9]+[ \t]*\\*[ \t]*" +
                "x[ \t]*(\\^[ \t]*[+-]?[0-9]+)?" +
                "|[ \t]*[-+]?[ \t]*x([ \t]*\\^[ \t]*[+-]?[0-9]+)?" +
                "|[ \t]*[-+]?[0-9]+)" +
                "([ \t]*[-+][ \t]*(" +
                "[-+]?[0-9]+[ \t]*\\*[ \t]*" +
                "x([ \t]*\\^[ \t]*[+-]?[0-9]+)?" +
                "|[-+]?[ \t]*x([ \t]*\\^[ \t]*[+-]?[0-9]+)?" +
                "|[-+]?[0-9]+))*" +
                "[ \t]*$";
                System.out.println(polyRegex);
        return matchRegex(polyRegex, polyExpression);
    }

    // Regex checker
    private static boolean matchRegex(String regex, String target) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);
        return matcher.matches();
    }

    private void derivatePolynomial() {
        Iterator it = valuePair.keySet().iterator();
        // Get iterator
        while (it.hasNext()) {
            Object key = it.next();
            BigInteger pow = new BigInteger(key.toString());
            derivatePair.put(pow.add(new BigInteger("-1")),
                    pow.multiply(new BigInteger(
                            valuePair.get(key).toString())));
        }
    }

    private String getDerivation() {
        boolean isFirst = true;
        StringBuilder target = new StringBuilder();
        Iterator dit = derivatePair.keySet().iterator();
        while (dit.hasNext()) {
            Object key = dit.next();
            BigInteger coeff = new BigInteger(derivatePair.get(key).toString());
            if (coeff.compareTo(new BigInteger("0")) > 0) {
                if (coeff.compareTo(new BigInteger("1")) == 0
                        && !key.toString().equals("0")) { ; }
                else {
                    target.append(coeff);
                    if (!key.toString().equals("0")) {
                        target.append("*");
                    }
                }
                isFirst = false;
                if (key.toString().equals("0")) { ; }
                else if (key.toString().equals("1")) {
                    target.append("x");
                } else {
                    target.append("x^" + key);
                }
                derivatePair.remove(key);
                break;
            }
        }
        dit = derivatePair.keySet().iterator();
        while (dit.hasNext()) {
            Object key = dit.next();
            BigInteger coeff = new BigInteger(derivatePair.get(key).toString());
            if (coeff.compareTo(new BigInteger("0")) == 0) {
                continue;
            } else if (coeff.compareTo(new BigInteger("0")) > 0 && !isFirst) {
                target.append("+");
            }
            if (coeff.compareTo(new BigInteger("-1")) == 0
                    && !key.toString().equals("0")) {
                target.append("-");
            } else if (coeff.compareTo(new BigInteger("1")) == 0
                    && !key.toString().equals("0")) { ; }
            else {
                target.append(coeff);
                if (!key.toString().equals("0")) {
                    target.append("*");
                }
            }
            isFirst = false;
            if (key.toString().equals("0")) { ; }
            else if (key.toString().equals("1")) {
                target.append("x");
            } else {
                target.append("x^" + key);
            }
        }
        if (isFirst) {
            target.append("0");
        }
        return target.toString();
    }

    private void processMatcher(Matcher matcher) {
        String coeff = null;
        String pow = null;
        if (matcher.group(1) != null) {
            coeff = matcher.group(1);
            pow = matcher.group(2);
        } else if (matcher.group(3) != null) {
            coeff = matcher.group(3);
            pow = "1";
        } else if (matcher.group(4) != null) {
            coeff = matcher.group(4);
            pow = "0";
        } else {
            coeff = "0";
            pow = "0";
            if (debug) {
                System.out.println("Unknown Error!");
            }
        }
        BigInteger one = null;
        BigInteger two = null;
        BigInteger power = new BigInteger(pow);
        if (valuePair.get(power.toString()) == null) {
            one = new BigInteger("0");
            two = new BigInteger(coeff);
        } else {
            one = new BigInteger(valuePair.get(power.toString()).toString());
            two = new BigInteger(coeff);
        }
        if (debug) {
            System.out.println(
                    "[+] Adding " + one + " and " + two +
                            " as Coefficient, Power " + power);
        }
        valuePair.put(power.add(new BigInteger("0")).toString(),
                one.add(two).toString());
    }

    public String getResult() {
        // Process spaces
        polyExpression = polyExpression.replaceAll("[ \t]", "");
        // Process dual operators
        polyExpression = polyExpression.replaceAll("[+][-]", "\\-");
        polyExpression = polyExpression.replaceAll("[+][+]", "\\+");
        polyExpression = polyExpression.replaceAll("[-][-]", "\\+");
        polyExpression = polyExpression.replaceAll("[-][+]", "\\-");
        // Process abbreviated symbols
        polyExpression = polyExpression.replaceAll("[-]x", "-1*x");
        polyExpression = polyExpression.replaceAll("[+]x|^x", "+1*x");


        String polyCleanRegex =
                "([-+]?[0-9]+)\\*x\\^([+-]?[0-9]+)" +       // Group 1, 2
                        "|([-+]?[0-9]+)\\*x" +              // Group 3
                        "|([-+]?[0-9]+)";                   // Group 4
                        System.out.println(polyCleanRegex);
        Pattern pattern = Pattern.compile(polyCleanRegex);
        Matcher matcher = pattern.matcher(polyExpression);
        while (matcher.find()) {
            processMatcher(matcher);
            if (debug) {
                System.out.println("[*] Group: " + matcher.group(0));
            }
        }
        if (debug) {
            System.out.println("[!] Before Processing...");
            Iterator it = valuePair.keySet().iterator();
            // Get iterator
            while (it.hasNext()) {
                Object key = it.next();
                System.out.println(
                        "[i] Power:" + key.toString() +
                                " Coeff:" + valuePair.get(key).toString());
            }
        }

        derivatePolynomial();

        if (debug) {
            System.out.println("[!] After Processing...");
            Iterator dit = derivatePair.keySet().iterator();
            // Get iterator
            while (dit.hasNext()) {
                Object key = dit.next();
                System.out.println(
                        "[i] Power:" + key.toString() +
                                " Coeff:" + derivatePair.get(key).toString());
            }
        }

        return getDerivation();
    }

}

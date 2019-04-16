import subprocess
import random
import sympy

from xeger import Xeger

round = 1000
expressionRegex = "^(@[+-])*@$"
termRegex = "^(#\\*)*#$"
sinFactorRegex = "^sin\\(~\\)(\\^\\+\\d{1,3})?$"
cosFactorRegex = "^cos\\(~\\)(\\^\\+\\d{1,3})?$"
powerFactorRegex = "^x(\\^\\+\\d{1,3})?$"
constFactorRegex = "^\\d{1,3}$"
exprFactorRegex = "^\\(!\\)$"
x = Xeger(limit=1)


def generate():
    max_round = 3
    result: str = x.xeger(expressionRegex)
    generate_round = 0
    while True:
        generate_round += 1
        for termCount in range(result.count('@')):
            result = result.replace('@', x.xeger(termRegex), 1)
        for factorCount in range(result.count('#')):
            factor_type = random.randint(0, 4)
            if factor_type == 0:
                if generate_round > max_round:
                    result = result.replace('#', x.xeger(powerFactorRegex), 1)
                else:
                    result = result.replace('#', x.xeger(sinFactorRegex), 1)
            elif factor_type == 1:
                if generate_round > max_round:
                    result = result.replace('#', x.xeger(powerFactorRegex), 1)
                else:
                    result = result.replace('#', x.xeger(cosFactorRegex), 1)
            elif factor_type == 2:
                result = result.replace('#', x.xeger(powerFactorRegex), 1)
            elif factor_type == 3:
                result = result.replace('#', x.xeger(constFactorRegex), 1)
            elif factor_type == 4:
                if generate_round > max_round:
                    result = result.replace('#', x.xeger(powerFactorRegex), 1)
                else:
                    result = result.replace('#', x.xeger(exprFactorRegex), 1)
        for innerCount in range(result.count('~')):
            factor_type = random.randint(0, 4)
            if factor_type == 0:
                if generate_round > max_round:
                    result = result.replace('~', x.xeger(powerFactorRegex), 1)
                else:
                    result = result.replace('~', x.xeger(sinFactorRegex), 1)
            elif factor_type == 1:
                if generate_round > max_round:
                    result = result.replace('~', x.xeger(powerFactorRegex), 1)
                else:
                    result = result.replace('~', x.xeger(cosFactorRegex), 1)
            elif factor_type == 2:
                result = result.replace('~', x.xeger(powerFactorRegex), 1)
            elif factor_type == 3:
                result.replace('~', x.xeger(constFactorRegex), 1)
            elif factor_type == 4:
                if generate_round > max_round:
                    result = result.replace('~', x.xeger(powerFactorRegex), 1)
                else:
                    result = result.replace('~', x.xeger(exprFactorRegex), 1)
        for exprCount in range(result.count('!')):
            result = result.replace('!', x.xeger(expressionRegex), 1)
        if result.find('@') == -1 and result.find('#') == -1 and result.find('~') == -1 and result.find('!') == -1:
            break
    return result


def main():
    x = sympy.Symbol('x')
    for i in range(round):
        test = generate()
        sympy_test = sympy.sympify(test.replace('^', '**'))
        sympy_diff = sympy.diff(sympy_test, x)
        print('\r---',end="")

        open('test.in', 'w').writelines(test)
        stdout = open("test.out", "w")
        stdin = open("test.in", "r")
        run = subprocess.Popen(["/usr/bin/java", "poly.Main"], stdin=stdin.fileno(), stdout=stdout.fileno())
        run.communicate()
        #subprocess.run(['/usr/bin/java', 'poly.Main', '<', 'test.in', '>', 'test.out'], shell=True)
        my_result = open('test.out', 'r').readline()
        my_diff = sympy.sympify(my_result.replace('^', '**'))

        for test_round in range(20):
            print('\r///',end="")
            value_true = sympy_diff.evalf(subs={x: test_round})
            value_false = my_diff.evalf(subs={x: test_round})
            if abs(value_true - value_false) > 1e-6:
                print('False')
                print(value_true)
                print(value_false)
                print(sympy_diff)
                print(my_diff)
                print()
                print(test)
                return
        print('\r\\\\\\',end="")


if __name__ == '__main__':
    main()

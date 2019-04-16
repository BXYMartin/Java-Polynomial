import subprocess
import random
import sympy

from xeger import Xeger

round = 1
expressionRegex = "^(@[+-])*@$"
termRegex = "^(#\\*)*#$"
sinFactorRegex = "^sin\\(~\\)$"
cosFactorRegex = "^cos\\(~\\)$"
powerFactorRegex = "^x(\\^[+-]\\d{1,3})?$"
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
        test = open('test.in', 'r').readline()
        sympy_test = sympy.sympify(test.replace('^', '**'))
        sympy_diff = sympy.diff(sympy_test, x)

        my_result = open('test.out', 'r').readline()
        my_diff = sympy.sympify(my_result.replace('^', '**'))
        for test_round in range(100):
            value_true = sympy_diff.evalf(subs={x: test_round})
            value_false = my_diff.evalf(subs={x: test_round})
            if abs(value_true - value_false) > 1e-8:
                print('False')
                print(value_true)
                print(value_false)
                print(sympy_diff)
                print(my_diff)
                print(test)
                return
        print("Success!")


if __name__ == '__main__':
    main()

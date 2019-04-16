#!/usr/bin/env wolframscript

(* generate "num" samples of a mixed distribution *)
source = ToExpression[$ScriptCommandLine[[2]]]; 
reference = ToString[$ScriptCommandLine[[2]]];
standard = ToExpression[$ScriptCommandLine[[3]]];

diff = D[source, x]
failed = False;
For[x=-10,x<=10,x+=0.1,
If[Abs[standard-diff]>0.0001 && !failed,
Print["0"];failed = True;]]
If[!failed,Print["1"]]

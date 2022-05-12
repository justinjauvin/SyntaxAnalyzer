# SyntaxAnalyzer
A syntax analyzer for an arbitrary language.

Rules:

In your program: 
1.	Implement a lexical analyzer as a subprogram of your program. Each time the lexical analyzer is called, it should return the next lexeme and its token code.  
2.	Implement a parser based on the following EBNF rules. Create a subprogram for each nonterminal symbol which should parse only sentences that can be generated by the nonterminal. 

==================================================================
<program>  ->  program begin <statement_list> end
<statement_list> -> <statement> {;<statement>}
<statement>  ->  <assignment_statement> | <if_statement> | <loop_statement> 
<assignment_statement> -> <variable> = <expression>
<variable> -> identifier  (An identifier is a string that begins with a letter followed by 0 or more letters and/or digits)
<expression> -> <term> { (+|-) <term>}           
<term> -> <factor> {(* | /) <factor> }
<factor> -> identifier | int_constant | (<expr>)
<if_statement> ->  if (<logic_expression>) then  <statement> 
<logic_expression> -> <variable> (< | >) <variable>  (Assume that logic expressions have only less than or greater than operators)
<loop_statement> ->  loop (<logic_expression>)  <statement>
=====================================================================

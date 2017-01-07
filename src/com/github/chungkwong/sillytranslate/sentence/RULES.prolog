append([],X,X).
append([H|T],Y,[H|L]):-append(T,Y,L).

noun(A):-'n.'(A).
noun(A):-'pron.'(A).
noun(A):-'abbr.'(A).
noun(A):-''(A).
verb(A):-'v.'(A).
verb(A):-'vbl.'(A).
verb(A):-'aux.'(A).
verb(A):-''(A).
intransitive_verb(A):-'vi.'(A).
intransitive_verb(A):-verb(A).
transitive_verb(A):-'vt.'(A).
transitive_verb(A):-verb(A).
adjective(A):-'adj.'(A).
adjective(A):-'a.'(A).
adjective(A):-'num.'(A).
adjective(A):-'art.'(A).
adverb(A):-'adv.'(A).
aux(A):-'aux.'(A).
conj(A):-'conj.'(A).
prep(A):-'prep.'(A),text(A,B),'\\=='(B,"的").
separator(A):-text(A,"，").
separator(A):-text(A,"；").

translate_quote([C|X],[G|Y],E,F,G,H):-text(C,E),text(D,F),append(A,[D],X),translate(A,AA),append(AA,[H],Y).

translate_basic_prepositional_phrase([H|T],[H|S]):-prep(H),translate_noun_phrase(T,S).
translate_prepositional_phrase(X,Y):-translate_basic_prepositional_phrase(X,Y).
translate_prepositional_phrase(X,Y):-append(A,B,X),translate_basic_prepositional_phrase(A,AA),translate_prepositional_phrase(B,BB),append(AA,BB,Y).
translate_adverbial([A],[A]):-adverb(A).
translate_adverbial([H|T],[H|S]):-adverb(H),translate_adverbial(T,S).
translate_adverbial(X,Y):-translate_prepositional_phrase(X,Y).
translate_adverbial(X,Y):-append(A,B,X),translate_prepositional_phrase(A,AA),translate_adverbial(B,BB),append(AA,BB,Y).
translate_basic_adjective([A],[A]):-adjective(A).
translate_basic_adjective([H|L],[H|LL]):-adjective(H),translate_basic_adjective(L,LL).
translate_basic_adjective([H|L],[H|LL]):-adverb(H),translate_basic_adjective(L,LL).
translate_adjective(X,Y):-translate_basic_adjective(X,Y).
translate_adjective(X,Y):-append(A,[C|B],X),text(C,E),translate_conjection(E,D),translate_basic_adjective(A,AA),translate_adjective(B,BB),append(AA,[D|BB],Y).

basic_noun_phrase([A],[A]):-noun(A).
basic_noun_phrase([H|L],[H|LL]):-noun(H),basic_noun_phrase(L,LL).
basic_noun_phrase([H|L],[H|LL]):-noun(H),translate_quote(L,LL,"(",")","（","）").
basic_noun_phrase(X,Y):-translate_quote(X,Y,"\"","\"","“","”").
translate_basic_noun_phrase(X,Y):-basic_noun_phrase(X,Y).
translate_basic_noun_phrase(X,Y):-append(A,B,X),translate_adjective(A,AA),basic_noun_phrase(B,BB),append(AA,BB,Y).
translate_basic_noun_phrase([W|X],Y):-text(W,T),wh(T,WW),translate_to_infinitive(X,XX),append(XX,[WW],Y).
translate_basic_noun_phrase([W,A,B|X],Y):-text(W,T),wh(T,WW),translate_clause([A,B|X],XX),append(XX,[WW],Y).
translate_noun_phrase(X,Y):-translate_basic_noun_phrase(X,Y).
translate_noun_phrase(X,Y):-append(A,[C|B],X),text(C,"的"),translate_noun_phrase(A,AA),translate_basic_noun_phrase(B,BB),append(BB,[C|AA],Y).
translate_noun_phrase(X,Y):-append(A,[C|B],X),text(C,E),wh(E,_),translate_noun_phrase(A,AA),translate_clause(B,BB),append(BB,["的"|AA],Y).
translate_noun_phrase(X,Y):-append(A,[C|B],X),text(C,"那"),translate_noun_phrase(A,AA),translate_clause(B,BB),append(BB,["的"|AA],Y).
translate_noun_phrase(X,Y):-append(A,[C|B],X),text(C,E),translate_conjection(E,D),translate_noun_phrase(A,AA),translate_basic_noun_phrase(B,BB),append(AA,[D|BB],Y).
translate_noun_phrase(X,Y):-append(N,P,X),translate_prepositional_phrase(P,Q),translate_noun_phrase(N,M),append(Q,["的"|M],Y).
translate_conjection("，","、").
translate_conjection("和","和").
translate_conjection("与","与").
translate_conjection("及","及").
translate_conjection("或","或").
wh("哪个","的哪个").
wh("什么","的").
wh("什么地方","的地方").
wh("什么时候","的时候").
wh("为什么","的原因").
wh("如何","的方法").
wh("谁","的人").

translate_to_infinitive([T|L],LL):-text(T,"去"),translate_verb_phrase(L,LL).
translate_to_infinitive([T,V],[V]):-text(T,"去"),transitive_verb(V).
translate_to_infinitive([T,V|X],[V|Y]):-text(T,"去"),transitive_verb(V),translate_adjective(X,Y).
translate_basic_verb_phrase([A],[A]):-intransitive_verb(A).
translate_basic_verb_phrase([V|N],[V|M]):-transitive_verb(V),translate_noun_phrase(N,M).
translate_basic_verb_phrase([A|J],[A|JJ]):-intransitive_verb(A),translate_adjective(J,JJ).
translate_verb_phrase(X,Y):-translate_basic_verb_phrase(X,Y).
translate_verb_phrase(X,Y):-append(A,B,X),translate_basic_verb_phrase(A,AA),translate_to_infinitive(B,BB),append(AA,BB,Y).
translate_verb_phrase(X,Y):-append(A,B,X),translate_basic_verb_phrase(A,AA),translate_adverbial(B,BB),append(BB,AA,Y).
translate_verb_phrase([A,V],["被",V]):-aux(A),transitive_verb(V).
translate_verb_phrase([A,V|J],["被"|Y]):-aux(A),transitive_verb(V),translate_adverbial(J,JJ),append(JJ,[V],Y).
translate_verb_phrase([A,B,V],[B,"被",V]):-aux(A),adverb(B),transitive_verb(V).
translate_verb_phrase([A,B,V|J],[B,"被"|Y]):-aux(A),adverb(B),transitive_verb(V),translate_adverbial(J,JJ),append(JJ,[V],Y).
translate_verb_phrase([A|J],JJ):-aux(A),translate_verb_phrase(J,JJ).

translate_clause(X,XX):-append(N,V,X),translate_noun_phrase(N,NN),translate_verb_phrase(V,VV),append(NN,VV,XX).
translate_clause([X],[X]).

translate_sentence(X,Y):-translate_clause(X,Y).
translate_sentence(X,Y):-append(A,[C|B],X),conj(C),translate_clause(A,AA),translate_clause(B,BB),append(AA,[C|BB],Y).
translate_sentence([C|X],[C|XX]):-conj(C),translate_clause(X,XX).

translate(X,Y):-translate_sentence(X,Y).
translate(X,Y):-append(A,[C|B],X),separator(C),translate_sentence(A,AA),translate_sentence(B,BB),append(AA,[C|BB],Y).
translate(X,Y):-translate_noun_phrase(X,Y).
translate(X,Y):-translate_verb_phrase(X,Y).

%translate(X,X).

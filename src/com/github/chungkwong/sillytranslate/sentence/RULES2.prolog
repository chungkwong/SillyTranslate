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
prep(A):-'prep.'(A),text(A,B),'\\=='(B,[30340]).
separator(A):-text(A,[65292]).
separator(A):-text(A,[65307]).

translate_quote([C|X],[G|Y],E,F,G,H):-text(C,E),append(A,[D],X),text(D,F),translate(A,AA),append(AA,[H],Y).

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
basic_noun_phrase([H|L],[H|LL]):-noun(H),translate_quote(L,LL,[40],[41],[65288],[65289]).
basic_noun_phrase(X,Y):-translate_quote(X,Y,[34],[34],[8220],[8221]).
translate_basic_noun_phrase(X,Y):-basic_noun_phrase(X,Y).
translate_basic_noun_phrase(X,Y):-append(A,B,X),translate_adjective(A,AA),basic_noun_phrase(B,BB),append(AA,BB,Y).
translate_basic_noun_phrase([W|X],Y):-text(W,T),wh(T,WW),translate_to_infinitive(X,XX),append(XX,[WW],Y).
translate_basic_noun_phrase([W,A,B|X],Y):-text(W,T),wh(T,WW),translate_clause([A,B|X],XX),append(XX,[WW],Y).
translate_noun_phrase(X,Y):-translate_basic_noun_phrase(X,Y).
translate_noun_phrase(X,Y):-append(A,[C|B],X),text(C,[30340]),translate_basic_noun_phrase(A,AA),translate_noun_phrase(B,BB),append(BB,[C|AA],Y).
translate_noun_phrase(X,Y):-append(A,[C|B],X),text(C,E),wh(E,_),translate_noun_phrase(A,AA),translate_clause(B,BB),append(BB,[[30340]|AA],Y).
translate_noun_phrase(X,Y):-append(A,[C|B],X),text(C,[37027]),translate_noun_phrase(A,AA),translate_clause(B,BB),append(BB,[[30340]|AA],Y).
translate_noun_phrase(X,Y):-append(A,[C|B],X),text(C,E),translate_conjection(E,D),translate_noun_phrase(A,AA),translate_basic_noun_phrase(B,BB),append(AA,[D|BB],Y).
translate_noun_phrase(X,Y):-append(N,P,X),translate_prepositional_phrase(P,Q),translate_noun_phrase(N,M),append(Q,[[30340]|M],Y).
translate_conjection([65292],[12289]).
translate_conjection([21644],[21644]).
translate_conjection([19982],[19982]).
translate_conjection([21450],[21450]).
translate_conjection([25110],[25110]).
wh([21738,20010],[30340,21738,20010]).
wh([20160,20040],[30340]).
wh([20160,20040,22320,26041],[30340,22320,26041]).
wh([20160,20040,26102,20505],[30340,26102,20505]).
wh([20026,20160,20040],[30340,21407,22240]).
wh([22914,20309],[30340,26041,27861]).
wh([35841],[30340,20154]).

translate_to_infinitive([T|L],LL):-text(T,[21435]),translate_verb_phrase(L,LL).
translate_to_infinitive([T,V],[V]):-text(T,[21435]),transitive_verb(V).
translate_to_infinitive([T,V|X],[V|Y]):-text(T,[21435]),transitive_verb(V),translate_adjective(X,Y).
translate_basic_verb_phrase([A],[A]):-intransitive_verb(A).
translate_basic_verb_phrase([V|N],[V|M]):-transitive_verb(V),translate_noun_phrase(N,M).
translate_basic_verb_phrase([A|J],[A|JJ]):-intransitive_verb(A),translate_adjective(J,JJ).
translate_verb_phrase(X,Y):-translate_basic_verb_phrase(X,Y).
translate_verb_phrase(X,Y):-append(A,B,X),translate_to_infinitive(B,BB),translate_basic_verb_phrase(A,AA),append(AA,BB,Y).
translate_verb_phrase(X,Y):-append(A,B,X),translate_basic_verb_phrase(A,AA),translate_adverbial(B,BB),append(BB,AA,Y).
translate_verb_phrase([A,V],[[34987],V]):-aux(A),transitive_verb(V).
translate_verb_phrase([A,V|J],[[34987]|Y]):-aux(A),transitive_verb(V),translate_adverbial(J,JJ),append(JJ,[V],Y).
translate_verb_phrase([A,B,V],[B,[34987],V]):-aux(A),adverb(B),transitive_verb(V).
translate_verb_phrase([A,B,V|J],[B,[34987]|Y]):-aux(A),adverb(B),transitive_verb(V),translate_adverbial(J,JJ),append(JJ,[V],Y).
translate_verb_phrase([A|J],JJ):-aux(A),translate_verb_phrase(J,JJ).

translate_clause(X,XX):-append(N,V,X),translate_verb_phrase(V,VV),translate_noun_phrase(N,NN),append(NN,VV,XX).
translate_clause([X],[X]).

translate_sentence(X,Y):-translate_clause(X,Y).
translate_sentence(X,Y):-append(A,[C|B],X),conj(C),translate_clause(A,AA),translate_clause(B,BB),append(AA,[C|BB],Y).
translate_sentence([C|X],[C|XX]):-conj(C),translate_clause(X,XX).

translate(X,Y):-translate_sentence(X,Y).
translate(X,Y):-append(A,[C|B],X),separator(C),translate_sentence(A,AA),translate_sentence(B,BB),append(AA,[C|BB],Y).
translate(X,Y):-translate_noun_phrase(X,Y).
translate(X,Y):-translate_verb_phrase(X,Y).

%translate(X,X).

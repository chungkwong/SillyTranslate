append([],X,X).
append([H|T],Y,[H|L]):-append(T,Y,L).

noun(A):-'n.'(A).
noun(A):-'pron.'(A).
noun(A):-'abbr.'(A).
noun(A):-''(A).
verb(A):-'v.'(A).
verb(A):-'vbl.'(A).
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
prep(A):-'prep.'(A).

translate_adverbial([A],[A]):-adverb(A).
translate_adverbial(A):-translate_prepositional_phrase(A).
translate_basic_adjective([A],[A]):-adjective(A).
translate_basic_adjective([H|L],[H|L]):-adjective(H),translate_basic_adjective(L,L).
translate_adjective(X,Y):-translate_basic_adjective(X,Y).
translate_adjective(X,Y):-append(A,[C|B],X),text(C,E),translate_conjection(E,D),translate_basic_adjective(A,AA),translate_adjective(B,BB),append(AA,[D|BB],Y).
translate_prepositional_phrase([H|T],[H|S]):-prep(H),translate_noun_phrase(T,S).

basic_noun_phrase([A]):-noun(A).
basic_noun_phrase([H|T]):-adjective(H),basic_noun_phrase(T).
translate_noun_phrase(X,X):-basic_noun_phrase(X).
translate_noun_phrase(X,Y):-append(A,[C|B],X),text(C,'的'),translate_noun_phrase(A,AA),basic_noun_phrase(B),append(B,[C|AA],Y).
translate_noun_phrase(X,Y):-append(A,[C|B],X),text(C,E),translate_conjection(E,D),translate_noun_phrase(A,AA),translate_noun_phrase(B,BB),append(AA,[D|BB],Y).
translate_noun_phrase(X,Y):-append(N,P,X),translate_noun_phrase(N,M),translate_prepositional_phrase(P,Q),append(M,['的'|Q],Y).
translate_conjection('，','、').
translate_conjection('和','和').
translate_conjection('与','与').
translate_conjection('及','及').
translate_conjection('或','或').

translate_verb_phrase([A],[A]):-intransitive_verb(A).
translate_verb_phrase([A|X],Y):-intransitive_verb(A),translate_adverbial(X,Z),append(Z,[A],Y).
translate_verb_phrase([V|N],[V|M]):-transitive_verb(V),translate_noun_phrase(N,M).
translate_verb_phrase([V|X],Y):-transitive_verb(V),append(N,A,X),translate_noun_phrase(N,M),translate_adverbial(A,B),append(B,[V|M],Y).
translate_verb_phrase([A|J],[A|JJ]):-aux(A),translate_adjective(J,JJ).
translate_verb_phrase([A|J],[A|JJ]):-verb(A),translate_adjective(J,JJ).
translate_verb_phrase([A|J],['被'|JJ]):-aux(A),translate_verb_phrase(J,JJ).
translate_verb_phrase([A|J],JJ):-aux(A),translate_verb_phrase(J,JJ).

translate([X],[X]).
translate(X,XX):-append(N,V,X),translate_noun_phrase(N,NN),translate_verb_phrase(V,VV),append(NN,VV,XX).
translate(X,Y):-translate_noun_phrase(X,Y).
translate(X,Y):-translate_verb_phrase(X,Y).

%translate(X,X).

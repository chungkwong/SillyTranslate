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
adjective(A):-'num.'(A).
adjective(A):-'art.'(A).
adjective(A):-'a.'(A).

adverb(A):-'adv.'(A).

article(A):-'art.'(A).
aux(A):-'aux.'(A).
conj(A):-'conj.'(A).
prep(A):-'prep.'(A).

noun_phrase([A]):-noun(A).
noun_phrase([H|T]):-adjective(H),noun_phrase(T).
translate_noun_phrase(X,X):-noun_phrase(X).
translate_noun_phrase(X,Y):-append(A,[C|B],X),append(B,[C|A],Y),text(C,'的'),noun_phrase(A),noun_phrase(B).
translate_noun_phrase(X,Y):-append(A,[C|B],X),append(A,[D|B],Y),text(C,E),translate_noun_conjection(E,D),noun_phrase(A),noun_phrase(B).
translate_noun_conjection('，','、').
translate_noun_conjection('和','和').
translate_noun_conjection('与','与').
translate_noun_conjection('及','及').
translate_noun_conjection('或','或').

translate_verb_phrase([A],[A]):-intransitive_verb(A).
translate_verb_phrase([V|N],[V|M]):-transitive_verb(V),translate_noun_phrase(N,M).

translate([X],[X]).
translate(X,XX):-append(N,V,X),translate_noun_phrase(N,NN),translate_verb_phrase(V,VV),append(NN,VV,XX).
translate(X,Y):-translate_noun_phrase(X,Y).
translate(X,Y):-translate_verb_phrase(X,Y).

%translate(X,X).

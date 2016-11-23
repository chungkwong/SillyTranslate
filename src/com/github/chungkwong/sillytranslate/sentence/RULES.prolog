append([],X,X).
append([H|T],Y,[H|L]):-append(T,Y,L).

noun([X]):-'n.'(X).
noun([X]):-''(X).

noun_phrase(X):-noun(X).

verb([X]):-'v.'(X).
verb([X]):-''(X).

intransitive_verb([X]):-'vi.'(X).
intransitive_verb(X):-verb(X).

transitive_verb([X]):-'vt.'(X).
transitive_verb(X):-verb(X).

verb_phrase(X):-intransitive_verb(X).
verb_phrase(X):-append(V,N,X),noun_phrase(N),transitive_verb(V).

translate_noun_phrase(X,X):-noun_phrase(X).
%translate_noun_phrase([A,C,B],[A,'、',B]):-text(C,'，'),noun_phrase(A),noun_phrase(B).
translate_verb_phrase(X,X):-verb_phrase(X).

translate([X],[X]).
translate(X,XX):-append(N,V,X),translate_noun_phrase(N,NN),translate_verb_phrase(V,VV),append(NN,VV,XX).
%translate(X,X).

%art., rt., int., prfix., pref., adj., d., suf., cinj., ad., blv., pr., v., adl.,
%  pron., vbl., comb., pro., vt., www., num., n., abbr., conj., aux., vi., lv., st., a., abr., adv., prep.
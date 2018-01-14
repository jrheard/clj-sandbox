(ns clj-sandbox.core
  (:require [com.rpl.specter :refer [select transform ALL FIRST LAST]]
            [quinto.grid :as g]))



; (Much of this introduction section is yanked
; straight from James Trunk's screencast:
; https://www.youtube.com/watch?v=rh5J4vacG98 )


; Specter is a Clojure/Script library that lets you
; elegantly and performantly manipulate your
; data while maintaining its type.

; Let's look at the "while maintaining its type"
; part first.


; Clojure isn't always very good at maintaining your
; data structures' type.

; map turns your vector into a lazyseq.
(map inc [1 2 3 4])







; mapv solves that specific problem...
(mapv inc [1 2 3 4])

; ...but doesn't solve the problem generally.
(mapv inc #{1 2 3 4})







; Specter has two main functions,
; `select` and `transform`. We'll be looking at them
; in detail, but for now, check this out:
(transform ALL inc #{1 2 3 4})






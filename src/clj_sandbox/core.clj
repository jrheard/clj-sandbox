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







; Specter has two main macros,
; `select` and `transform`. We'll be looking at them
; both in detail, but for now, check this out:
(transform ALL inc #{1 2 3 4})
(transform ALL inc [1 2 3 4])
(transform ALL inc '(1 2 3 4))



; So: at this point we're convinced that specter
; has a `transform` macro, and that it doesn't
; mess with our data structures' types.

; We'll come back to `transform`.
; For now, though, let's take a look at `select`.






; Here's some dummy data to play around with.
; It's obviously nonsense, but it's got a couple of
; layers of nesting, and some bits are single
; values while other bits are sequences, etc.

(def pizza {:name         "Pizza"
            :health-value 0.4
            :ingredients  ["Bread" "Cheese" "Pepperoni"]})
(def pho {:name         "Pho"
          :health-value 0.7
          :ingredients  ["Water" "Tendon" "Noodles"]})
(def curry {:name         "Curry"
            :health-value 0.8
            :ingredients  ["Chicken" "Veggies" "Coconut Milk"]})
(def bbq {:name         "BBQ"
          :health-value 0.5
          :ingredients  ["Meat"]})
(def salad {:name         "Salad"
            :health-value 1.0
            :ingredients  ["Lettuce"]})
(def ice-cream {:name         "Ice Cream"
                :health-value 0.1
                :ingredients  ["Ice" "Cream"]})
(def fries {:name         "Fries"
            :health-value 0.2
            :ingredients  ["Potatoes" "Oil"]})
(def sausage {:name         "sausage"
              :health-value 0.3
              :ingredients  ["Meat"]})



(def people
  [{:name           "JR"
    :favorite-foods [pizza pho curry]
    :pets           ["Frank"]}
   {:name           "Jane"
    :favorite-foods [bbq fries]
    :pets           []}
   {:name           "Joe"
    :favorite-foods [ice-cream salad sausage]
    :pets           ["Ed" "Edd" "Eddy"]}])



; OK, let's start off by getting the names of
; everyone's favorite foods.

(map #(->> %
           :favorite-foods
           (map :name))
     people)

; Oh, right, gotta change that line to use mapcat
; instead of map.






; Here's how to do the same thing using Specter.

; (select path data)

(select [ALL :favorite-foods ALL :name] people)

; `select` is the name of the macro.

; `data` is the data structure we're asking
; specter to look at.

; So what's `path` mean, and why is it
; [ALL :favorite-foods ALL :name] here?












(def a-move [[[2 2] 5]
             [[2 3] 8]
             [[2 4] 7]
             [[2 5] 0]
             [[2 6] 0]])



(ns clj-sandbox.core
  (:require [clojure.string :refer [split join upper-case]]
            [com.rpl.specter :refer
             [select transform ALL FIRST LAST parser
              INDEXED-VALS collect-one selected?]]
            [quinto.grid :as g]))

(defn print-grid [grid]
  (doseq [y (range (count (first grid)))]
    (doseq [x (range (count grid))]
      (let [cell (get-in grid [x y])]
        (print (if (nil? cell)
                 "x"
                 cell))))
    (print "\n")))







; Lightning talk: Specter

; JR Heard
; jrheard.com


; (Much of this lightning talk is yanked
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
(map inc #{1 2 3 4})
(mapv inc #{1 2 3 4})






; Specter, on the other hand, respects your data's types.
(transform ALL inc #{1 2 3 4})
(transform ALL inc [1 2 3 4])
(transform ALL inc '(1 2 3 4))










; Specter has two main macros,
; `select` and `transform`.

; `select` is basically `get-in` on steroids, and
; `transform` is basically `update-in` on steroids.

; We'll come back to `transform`.
; For now, though, let's take a look at `select`.






; Here's some dummy data we'll be playing around with.
; It's obviously nonsense, but it's got a couple of
; layers of nesting, and some bits are single
; values while other bits are sequences, etc.

;; Foods
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
(def sausage {:name         "Sausage"
              :health-value 0.3
              :ingredients  ["Meat"]})


;; People
(def jr {:name           "JR"
         :favorite-foods [pizza pho curry]
         :pets           ["Frank"]})

(def jane {:name           "Jane"
           :favorite-foods [bbq fries]
           :pets           []})

(def joe {:name           "Joe"
          :favorite-foods [ice-cream salad sausage]
          :pets           ["Ed" "Edd" "Eddy"]})


(def people [jr jane joe])



; OK, let's start off by getting the names of
; everyone's favorite foods.

(mapcat #(->> %
              :favorite-foods
              (map :name))
        people)






; Here's how to do the same thing using Specter.

(select [ALL :favorite-foods ALL :name] people)




; (select path data)

; `select` is the name of the macro.

; `data` is the data structure we're asking
; specter to look at.

; So what's `path` mean, and why is it
; [ALL :favorite-foods ALL :name] here?




; START
[jr jane joe]

; ALL
jr
jane
joe

; :favorite-foods
[pizza pho curry]
[bbq fries]
[ice-cream salad sausage]

; ALL
pizza
pho
curry
bbq
fries
ice-cream
salad
sausage

; :name
"Pizza"
"Pho"
"Curry"
"BBQ"
"Fries"
"Ice Cream"
"Salad"
"Sausage"


(select [ALL :favorite-foods ALL :name] people)







; [ALL :favorite-foods ALL :name] is a "path".
; Each of the elements within that path
; is a "navigator".








; `transform` is just like `select`, except that it
; transforms the original data structure
; instead of returning a filtered view of it.

; We used `select` like this:
; (select path data)

(select [ALL :favorite-foods ALL :name] people)

; `transform` has a similar signature:
; (transform path transform-fn data)

(transform [ALL :favorite-foods ALL :name]
           upper-case
           people)

; Notice how the result is the original data structure,
; with all of its nested values' types kept intact,
; with modifications easily made several levels down.



















; We've mainly been using ALL and keywords
; as navigators, but Specter has tons more
; built-in navigators, some of which can be
; pretty powerful.

; `parser` is a navigator that's used like this:
; (parser parse-fn unparse-fn)

(defn parse-food [food] (split food #" "))
(defn unparse-food [food] (clojure.string/join " " food))

; We can use it to e.g. do silly things to the :name
; field of any food that has more than one word in it.

(transform [ALL :favorite-foods ALL :name

            (parser parse-food unparse-food)

            #(> (count %) 1)]

           reverse

           people)












; I'm sure there are lots of uses for `transform`,
; but so far I've mainly found myself using `select`.




; I was working on a simple grid-based game,
; where players make a move by placing
; "tiles" down on the board in a horizontal or
; vertical line.
; A "tile" is a number between 0 and 9.




; So a move looks like [move-part move-part ...],
; and a move-part looks like [[x y] value].

(def a-move [[[2 2] 5]
             [[2 3] 8]
             [[2 4] 7]
             [[2 5] 0]
             [[2 6] 0]])









; If I want to see whether a move is horizontal
; or vertical, I can grab all of its Xs and see if
; they're identical.


; START
[[[2 2] 5]
 [[2 3] 8]
 [[2 4] 7]
 [[2 5] 0]
 [[2 6] 0]]

; ALL
[[2 2] 5]
[[2 3] 8]
[[2 4] 7]
[[2 5] 0]
[[2 6] 0]

; FIRST
[2 2]
[2 3]
[2 4]
[2 5]
[2 6]

; FIRST
2
2
2
2
2



(select [ALL FIRST FIRST] a-move)


; The x-values are all the same,
; so that means that this move was vertical.

; Just for grins, let's grab the Ys:

(select [ALL FIRST LAST] a-move)














; Now let's look at a slightly trickier select.
; In my game, the board is represented as a 2D
; vector of integers-or-nils.

(comment
  (print-grid (g/make-empty-grid 5 5)))



; I wrote a lot of functions that returned the [x y]
; positions of cells that met certain criteria -
; functions like `find-empty-cells`,
; `find-blocked-cells`, etc.


; Here's a grid with some tiles placed on it.

(def grid (-> (g/make-empty-grid 5 5)
              (assoc-in [4 4] 5)
              (assoc-in [4 3] 8)
              (assoc-in [4 2] 7)
              (assoc-in [3 2] 3)))

(comment
  (print-grid grid))



; Let's take a stab at finding the positions
; of all of the non-nil cells.

(select [ALL ALL some?] grid)




; Dang, that gave us the _values_ of the non-nil cells,
; but we wanted their _positions_!

; We're gonna need to use a few new navigators for this.








; First, here's how we get the even numbers
; in a sequence.

(select [ALL even?]
        [5 6 7 8])



; Now let's figure out how to get the _indexes_
; of the even numbers in a sequence.

(select [INDEXED-VALS] [5 6 7 8])

(select [INDEXED-VALS (collect-one FIRST) LAST
         even?]
        [5 6 7 8])

; `collect-one` lets you save some data for later.

; If you're midway through a `select`'s path and you
; want to hold on to a piece of data that you're about
; to navigate past, `collect-one` is your friend.

; Here, we used it to save the indexes of our
; even numbers.


; You could also do this instead:

(select [INDEXED-VALS (selected? LAST even?) FIRST]
        [5 6 7 8])

; `selected?` lets you peek ahead.

; We used it here to say: "Select the indexes of
; this list's elements, but only those indexes
; that belong to elements whose value is even."











; We can combine `collect-one` and `selected?`
; to find the [x y] positions of non-nil values
; in our 2D vector.

(def grid (-> (g/make-empty-grid 5 5)
              (assoc-in [4 4] 5)
              (assoc-in [4 3] 8)
              (assoc-in [4 2] 7)
              (assoc-in [3 2] 3)))


(comment
  (print-grid grid))


(select [INDEXED-VALS (collect-one FIRST) LAST

         INDEXED-VALS (selected? LAST some?) FIRST]

        grid)


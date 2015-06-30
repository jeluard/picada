(ns picada.aria)

(def labelled-by "aria-labelledby")
(def described-by "aria-describedby")

#?(:cljs
(defn set-labelled-by!
  [el lel id]
  (if lel
    (do (.setAttribute el labelled-by id)
        (set! (.-id lel) id))
    (.removeAttribute el labelled-by))))

#?(:cljs
(defn set-described-by!
  [el lel id]
  (if lel
    (do (.setAttribute el described-by id)
        (set! (.-id lel) id))
    (.removeAttribute el described-by))))
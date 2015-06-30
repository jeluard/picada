(ns picada.doc)


(defn property-doc
  [m]
  {:name (key m) :default nil :doc nil :type nil :events? nil :attributes? nil})

(defn method-doc
  [m]
  {:name m :args nil :doc nil})

(defmacro doc
  [c]
  (let [m (meta c)]
    (merge
       {:name (:name m)
        :ns (:ns m)
        :doc (:doc m)
        :material-ref (:picada/material-ref m)}
       (let [s# (:mixins c)]                                 ; TODO collect all mixins
         (if (seq s#)
           {:mixins s#}))
       (let [s# (mapv property-doc (:properties c))]
         (if (seq s#)
           {:properties s#}))
       (let [s# (mapv method-doc (:methods c))]
         (if (seq s#)
           {:properties s#})))

    `{:aa ~m
      :bb ~c}))
(ns picada.component.spinner
  #?(:clj (:require [garden.def :refer [defkeyframes]]))
  #?@(:cljs
      [(:require [picada.component :as comp]
                 [garden.types :as gt] ; if removed garden won't be loaded
                 [hipo.core :as h]
                 [lucuma.core :as l])
       (:require-macros [lucuma.core :refer [defcustomelement]]
                        [garden.def :refer [defkeyframes]])]))

(defkeyframes color
  ["100%, 0%"
   {:stroke "red"}]
  ["40%"
   {:stroke "blue"}]
  ["66%"
   {:stroke "green"}]
  ["80%, 90%"
   {:stroke "yellow"}])

(defkeyframes dash
  ["0%"
   {:stroke-dasharray "1,200"
    :stroke-dashoffset 0}]
  ["50%"
   {:stroke-dasharray "89,200"
    :stroke-dashoffset -35}]
  ["100%"
   {:stroke-dasharray "89,200"
    :stroke-dashoffset -124}])

(defkeyframes rotate
  ["100%"
   {:transform "rotate(360deg)"}])

(def styles
  [color rotate dash
   [:pica-spinner
    {:display "inline-block"}
    [:svg
     {:position "relative"
      :width "100px"
      :height "100px"}
     [:circle
      {:fill "none"
       :stroke "var(--pica-spinner-color, currentColor)"
       :stroke-dasharray "1,200"
       :stroke-dashoffset 0
       :stroke-linecap "round"
       :stroke-width 4
       :stroke-miterlimit 10}]]
    ["&:not([value])"
     [:svg
      {:animation [[rotate "2s" :linear :infinite]]}
      [:circle
       {:animation [[dash "1.5s" :ease-in-out :infinite]
                    [color "6s" :ease-in-out :infinite]]}]]]]])

#?(:cljs
(defn- set-dasharray!
  [el r]
  (set! (.. (.querySelector el "circle") -style -strokeDasharray) (* r js/Math.PI 2))))

#?(:cljs
(defn- set-dashoffset!
  [el r v]
  (set! (.. (.querySelector el "circle") -style -strokeDashoffset) (* (/ (* r js/Math.PI 2) 100) (- 100 v)))))

#?(:cljs
(defcustomelement pica-spinner
  {:picada/material-ref {:button "http://www.google.com/design/spec/components/progress-activity.html"}}
  :mixins [comp/reconciliate]
  :on-attached (fn [el]
                (set-dasharray! el 20)
                 (if-let [value (:value (l/get-properties el))]
                   (set-dashoffset! el 20 value)))
  :on-property-changed
  (fn [el m]
    (if-let [value (:value (l/changes->map m))]
      (set-dashoffset! el 20 value)))
  :document
  (fn [_ {:keys [attrs]}]
    [:svg
     [:circle ^:attrs (merge {:cx 50 :cy 50 :r 20}
                             (if attrs {:cx (:c attrs) :cy (:c attrs) :r (:r attrs)}))]])
  :properties {:value {:type :number :default nil} :attrs nil}))

#_(:cljs
   (defcustomelement pica-spinner
                     {:picada/material-ref {:button "http://www.google.com/design/spec/components/progress-activity.html"}}
                     :mixins [comp/reconciliate]
                     :document
                     (fn [_ {:keys [attrs value]}]
                       [:host
                         [:svg
                          [:circle ^:attrs (merge {:cx 50 :cy 50 :r 20}
                                                  {:style {:styleDashOffset (* (/ (* r js/Math.PI 2) 100) (- 100 value))
                                                            :styleDashArray (* r js/Math.PI 2)}}
                                                  (if attrs {:cx (:c attrs) :cy (:c attrs) :r (:r attrs)}))]]])
                     :properties {:value {:type :number :default nil} :attrs nil}))
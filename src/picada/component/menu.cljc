(ns picada.component.menu
  #?@(:clj
      [
       (:require [picada.color :as col]
                 [picada.component.subheader :as sub]
                 [picada.style :as sty]
                 [garden.stylesheet :refer [at-media]])]
      :cljs
      [(:require [picada.color :as col]
                 [picada.component :as comp]
                 [picada.component.subheader :as sub]
                 [picada.style :as sty]
                 [hipo.core :as h]
                 [garden.stylesheet :refer [at-media]])
       (:require-macros [lucuma.core :refer [defcustomelement]])]))

(def styles
  [:pica-menu
   {:display "block"
    :position "fixed"
    :padding "8px 0"
    :background-color "white"
    :min-width "112px"
    :max-width "280px"}
   (sty/shadows 8)
   sub/styles
   [:ul
    {:padding 0
     :margin 0}]
   ["ul + ul"
    {:border-top "solid black"}]])

#?(:cljs
(defn show
  [pos actions]
  (let [[mel _] (h/create [:pica-menu {:actions actions}])]
    (.setAttribute mel "style" (str "left: " (:x pos) "px; top:" (:y pos) "px"))
    (.appendChild js/document.body mel))))

#?(:cljs
(defcustomelement pica-menu
  {comp/material-ref {:menu "http://www.google.com/design/spec/components/menus.html"}}
  :mixins [comp/reconciliate]
  :document
  (fn [_ {:keys [actions]}]
    [:ul
     (for [m actions]
       [:pica-item {:action m}])])
  :properties {:actions nil}
  :methods {:dismiss dismiss}))

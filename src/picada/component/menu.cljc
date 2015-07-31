(ns picada.component.menu
  #?@(:clj
      [(:require [picada.component.subheader :as sub]
                 [picada.style :as sty]
                 [garden.stylesheet :refer [at-media]])]
      :cljs
      [(:require [picada.animation :as anim]
                 [picada.component :as comp]
                 [picada.component.subheader :as sub]
                 [picada.style :as sty]
                 [garden.stylesheet :refer [at-media]]
                 [hipo.core :as h])
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
     :margin 0}
    [:pica-item
     {:padding "0 16px"}]]
   ["ul + ul"
    {:border-top "solid black"}]
   (at-media {:--desktop ""}
     ["& ul"
      {:padding "0 24px"}])])

#?(:cljs (declare dismiss))

#?(:cljs (def click-outside-listener #(if-not (.closest (.-target %) "pica-menu") (dismiss (.querySelector js/document "pica-menu")))))

#?(:cljs
(defn show
  [actions]
  (let [mel (h/create [:pica-menu {:actions actions}])]
    (.appendChild js/document.body mel)
    (comp/show mel #(.addEventListener js/document.body "click" click-outside-listener true)))))

#?(:cljs
(defn location-from-component
  [evt el s]
  (let [cel (.closest (.-target evt) s)
        r (.getBoundingClientRect cel)]
    {:x (.-left r) :y (.-bottom r)})))

#?(:cljs
(defn show-at-event
  ([evt actions] (show-at-event evt nil actions))
  ([evt s actions]
   (let [mel (show actions)
         pos (or (if s (location-from-component evt mel s))
                 {:x (.-clientX evt) :y (.-clientY evt)})]
     (.setAttribute mel "style" (str "left: " (:x pos) "px; top: " (:y pos) "px"))))))

#?(:cljs
(defn dismiss
  [el]
  (anim/dismiss el #(.removeEventListener js/document.body "click" click-outside-listener true))))

#?(:cljs
(defcustomelement pica-menu
  {comp/material-ref {:menu "http://www.google.com/design/spec/components/menus.html"}}
  :mixins [comp/component]
  :document
  (fn [_ {:keys [actions]}]
    [:host
     [:ul
      (for [m actions]
       [:pica-item  {:action (comp/wrap-action m (fn [evt f] (f) (dismiss (.closest (.-target evt) "pica-menu"))))}])]])
  :properties {:actions nil}
  :methods {:dismiss dismiss}))

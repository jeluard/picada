(ns picada.component.drawer
  #?@(:clj
      [(:require [picada.component.subheader :as sub]
                 [picada.color :as col]
                 [picada.style :as sty]
                 [garden.stylesheet :refer [at-media]])]
      :cljs
      [(:require [picada.animation :as anim]
                 [picada.color :as col]
                 [picada.component :as comp]
                 [picada.component.subheader :as sub]
                 [picada.style :as sty]
                 [hipo.core :as h]
                 [hipo.hiccup :as hhic]
                 [garden.stylesheet :refer [at-media]])
       (:require-macros [lucuma.core :refer [defcustomelement]])]))

(def styles
  [:pica-drawer
   {:display "block"
    :position "fixed"
    :height "100vh"
    :top 0
    :left 0
    :width "calc(100vh - 56px)"
    :max-width "320px"
    :background-color "white"
    :transform "translateX(-100%)"}
   (sty/shadows 16)
   ["&[visible]"
    {:transform "none"}]
   sub/styles
   [:ul
    {:padding 0
     :margin 0}]
   ["ul + ul"
    {:border-top (str "solid 1px var(--divider-color)")}]
   ["pica-item"
    {:padding "0 16px"}]
   (at-media {:--desktop ""}
     [:&
      {:width "auto"
       :max-width "400px"}])])

#?(:cljs (declare hide))

#?(:cljs (def click-outside-listener #(if-not (.closest (.-target %) "pica-drawer") (hide (.querySelector js/document "pica-drawer")))))

#?(:cljs
(defn hide
  [el]
  (anim/hide el #(.removeEventListener js/document.body "click" click-outside-listener true))))

#?(:cljs
(defn items
  [& v]
  [:ul
   (for [i v]
     (let [attrs (hhic/attributes i)]
       [:pica-item (merge attrs {:action (comp/wrap-action (:action attrs) (fn [evt f] (f) (hide (.closest (.-target evt) "pica-drawer"))))})]))]))

#?(:cljs
(defn create
  [c]
  (let [[del _] (h/create [:pica-drawer c])]
    (.appendChild js/document.body del)
    del)))

#?(:cljs
(defn show
  [c]
  (let [del (or (.querySelector js/document "pica-drawer") (create c))]
    (anim/show del #(.addEventListener js/document.body "click" click-outside-listener true)))))

#?(:cljs
(defcustomelement pica-drawer
  {comp/material-ref {:navigation-drawer "http://www.google.com/design/spec/patterns/navigation-drawer.html"}}
  :mixins [anim/animation-lifecycle]
  :properties {:animation-entry :left-entry :animation-exit :left-exit}
  :methods {:hide hide}))

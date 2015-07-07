(ns picada.component.overlay
  #?(:cljs (:require-macros [lucuma.core :refer [defcustomelement]])))

(def styles
  [:pica-overlay
   {:position "fixed"
    :top 0
    :left 0
    :width "100vw"
    :height "100vh"
    :background-color "var(--pica-overlay-background-color, currentColor)"
    :opacity 0
    :transition "opacity 0.2s"
    :z-index 9}                                             ; TODO should be bellow dialog
   ["&[visible]"
    {:opacity 0.6}]])

#?(:cljs
(defn dismiss
  [& [el]]
  (if-let [oel (.querySelector (or el js/document) "pica-overlay")]
    (.remove oel))))

; TODO switch to picada.animation

#?(:cljs
(defcustomelement pica-overlay
  :properties {:visible false}))
(ns picada.component.overlay
  #?(:cljs (:require-macros [picada.component :refer [defcomponent]])))

(def styles
  [:pica-overlay
   {:position "fixed"
    :top 0
    :left 0
    :width "100vw"
    :height "100vh"
    :background-color "var(--pica-overlay-background-color, currentColor)"
    :opacity 0.6
    :z-index 9}                                             ; TODO should be bellow dialog
    ])

#?(:cljs
(defcomponent pica-overlay
  :style styles
  :properties {:animation-entry :overlay-entry :animation-exit :overlay-exit}))

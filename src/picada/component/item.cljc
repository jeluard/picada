(ns picada.component.item
  #?@(:clj
      [(:require [picada.color :as col]
                 [picada.style :as sty]
                 [garden.stylesheet :refer [at-media]])]
      :cljs
      [(:require [picada.color :as col]
                 [picada.style :as sty]
                 [hipo.core :as h]
                 [garden.stylesheet :refer [at-media]])
       (:require-macros [picada.component :refer [defcomponent]])]))

(def styles
  [:pica-item
   {:display "flex"
    :align-items "center"
    :height "48px"}
   ["&:hover:not([disabled])"
    {:background-color "rgba(0, 0, 0, 0.03)"
     :cursor "pointer"}]
   ["&[disabled]"
    {:color (get-in col/text [:dark :--disabled-text-color])
     :background (get-in col/text [:dark :--divider-color])
     :cursor "auto"
     :pointer-events "none"}]
   [:li
    {:display "flex"
     :align-items "center"
     :width "100%"
     :height "100%"
     :list-style-type "none"}
    [:pica-icon
     {:margin-right "24px"}]]])

#?(:cljs
(defcomponent pica-item
  :material-ref {:menu "http://www.google.com/design/spec/components/menus.html"}
  :document
  (fn [_ {:keys [action]}]
    [:host
     [:li {:on-click (:fn action)}
      (if (contains? action :icon)
       [:pica-icon {:icon (:icon action)}])
      [:span (:name action)]]])
  :style styles
  :properties {:action nil :disabled false}))

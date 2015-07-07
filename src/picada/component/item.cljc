(ns picada.component.item
  #?@(:clj
      [
       (:require [picada.color :as col]
                 [picada.style :as sty]
                 [garden.stylesheet :refer [at-media]])]
      :cljs
      [(:require [picada.color :as col]
                 [picada.component :as comp]
                 [picada.style :as sty]
                 [hipo.core :as h]
                 [garden.stylesheet :refer [at-media]])
       (:require-macros [lucuma.core :refer [defcustomelement]])]))

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
(defcustomelement pica-item
  {comp/material-ref {:menu "http://www.google.com/design/spec/components/menus.html"}}
  :mixins [comp/reconciliate]
  :document
  (fn [_ {:keys [action]}]
    [:li {:on-click (:fn action)}
     (if (contains? action :icon)
       [:pica-icon {:icon (:icon action)}])
     [:span (:name action)]])
  :properties {:action nil :disabled false}))

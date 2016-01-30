(ns picada.component.tab
  (:require [picada.color :as col]
            [picada.typography :as typ]
            [garden.stylesheet :refer [at-media]])
  #?(:cljs (:require-macros [picada.component :refer [defcomponent]])))

(def styles
  (list
    [:pica-tab
     {:height "46px"
      :padding-bottom "18px"
      :box-sizing "border-box"
      :min-width "160px"
      :max-width "264px"
      :border-width "0px 0px 2px"
      :border-style "solid"
      :border-color "transparent"}
     ["&[active]"
      {:border-color "var(--pica-tab-indicator-color, var(--accent-color, #FFF))"}
      ["h2"
       {:color "var(--pica-tab-label-color, #FFF)"}]]
     ["&:hover:not([disabled])"
      {:cursor "pointer"}]
     ["&[disabled]"
      {:pointer-events "none"}
      ["h2"
       {:color (get-in col/text [:dark :--disabled-text-color])}]]
     (at-media {:--desktop ""}
       [:&:first-of-type
        {:left-padding "80px"}])
     [:h2
      {:padding "0 12px 20px"
       :font-size "14px"
       :text-transform "uppercase"
       :text-align "center"
       :font-weight (:medium typ/font-weights)
       :color "rgba(255, 255, 255, 0.7)"
       :overflow "hidden"
       :text-overflow "ellipsis"
       :white-space "nowrap"}]]
    [:pica-tab-bar
     {:display "flex"
      :width "100%"
      :background-color "var(--pica-tab-bar-background-color, var(--primary-color))"}
     ["&[centered]"
      {:justify-content "center"}]]))

#?(:cljs
(defn activate-tab
  [evt]
  (let [tel (.closest (.-target evt) "pica-tab")]
    (if-let [el (.querySelector (.-parentElement tel) "[active]")]
      (set! (.-active el) false))
    (set! (.-active tel) true))))

 #?(:cljs
 (defcomponent pica-tab
   :material-ref {:tabs "https://www.google.com/design/spec/components/tabs.html"}
   :document
   (fn [el {:keys [action label]}]
     [:host {:on-click (:fn action)}
       [:h2 label]])
   :on-created
   (fn [el _]
     (.addEventListener el "click" activate-tab))
   :style styles
   :properties {:label "" :action nil :active false}))

#?(:cljs
(defcomponent pica-tab-bar
  :material-ref {:tabs "https://www.google.com/design/spec/components/tabs.html"}
  :document
  (fn [el {:keys []}]
    [:host {:role "tab"}
      ])
  ;:on-attached TODO ensure there is a single or 0 active tab
  :style styles
  :properties {:centered false}))

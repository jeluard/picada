(ns picada.component.button
  (:require [picada.color :as col]
            [picada.style :as st]
            [picada.typography :as typ]
            #?@(:cljs
              [[picada.animation :as anim]
               [picada.component :as comp]
               [lucuma.core :as l]]))
  #?(:cljs (:require-macros [picada.component :refer [defcomponent]])))

(def display
  {:display "inline-block"
   :box-sizing "border-box"})

(def styles
  (list
    [":--pica-button"
     {:position "relative"
      :transition st/shadow-transition
      :transform-origin "50% 50%"}
     [:pica-icon
      {:position "absolute"}]
      ; TODO cssnext bug?
     ["&[disabled]"
      {:color (get-in col/text [:dark :--disabled-text-color])
       :cursor "auto"
       :pointer-events "none"}]
     ["&:focus"
      {:outline "none"}]
     ["&[pressed]"
     ; TODO should be 40% of currentColor color(var(--pica-button-icon-color, currentColor) alpha(40%))
      {:background-color (get-in col/text [:dark :--divider-color])}]
     ["&[busy]"
      {:color (get-in col/text [:dark :--disabled-text-color])
       :background-color (get-in col/text [:dark :--divider-color])
       :cursor "auto"
       :pointer-events "none"}]
     ["&:hover:not([disabled]):not([busy])"
      {:cursor "pointer"}]]
    [:pica-button
     display
     {:border "10px"
      :min-width "88px"
      :height "36px"
      :padding "0 8px"
      :margin "0 8px"
      :font-size "14px"
      :text-transform "uppercase"
      :text-align "center"
      :font-weight (:medium typ/font-weights)
      :line-height "36px"
      :user-select "none"}
     ["&[raised]"
      {:border-radius "3px"}
      (st/shadows 2)]
     ["&[raised][pressed]"
      (st/shadows 8)]]
    [:pica-icon-button
     display
     {:width "48px"
      :height "48px"
      :border-radius "50%"}
     [:pica-icon
      {:width "inherit"
       :height "inherit"}]]
    [:pica-fab
     display
     {:width "56px"
      :height "56px"
      :padding "16px"
      :border-radius "50%"
      :color "var(--pica-fab-color, var(--secondary-text-color))"
      :background-color "var(--pica-fab-background-color, var(--accent-color))"}
     [:pica-icon
      {:width "inherit"
       :height "inherit"}]
     ["&:not([busy])"
      (st/shadows 6)]
     ["&[disabled] &[busy]"
      {:color "var(--pica-fab-disabled-background-color, var(--disabled-background-color))"}]
     ["&[pressed]"
      (st/shadows 12)]
     ["&[mini]"
      {:width "40px"
       :height "40px"
       :padding "8px"}]
     ["&[busy]"
      {:position "relative"}
      [:pica-spinner
       {:position "absolute" :top "-4px" :left "-4px"}
       [:svg
        {:height "64px" :width "64px"}]]]
     ["&[mini][busy]"
      [:pica-spinner
       [:svg
        {:height "48px" :width "48px"}]]]]
    [:pica-button-bar
     (merge
       {:display "inline-flex"
        :border-radius "2px"}
       (st/shadows 1))
     [:pica-icon-button
      {:border-radius "initial"}]
     ["pica-icon-button:not(:last-of-type)"
      {:border-color "var(--disabled-background-color)"
       :border-width "1px"
       :border-right-style "solid"}]]))

; TODO if action is nil, auto disabled?

#?(:cljs
(def pica-button-base
  {:style styles
   :properties {:action nil :disabled false :pressed false :trigger-type {:type :keyword :default nil}}
   :on-attached (fn [el]
                  (.addEventListener el "mousedown" #(when-not (.-pressed el) (set! (.-pressed el) true) ))
                  (.addEventListener el "mouseleave" #(if (.-pressed el) (set! (.-pressed el) false)))
                  (.addEventListener el "mouseup" #(set! (.-pressed el) false)))}))

#?(:cljs
(defn trigger
  [t]
  (case t
    :mousedown :on-mousedown
    :mouseup :on-mouseup
    :doubleclick :on-doubleclick
    :on-click)))

#?(:cljs
(defn properties
  [{:keys [action disabled trigger-type]}]
  {:tabindex (if disabled -1 0) (trigger trigger-type) (:fn action)}))

#?(:cljs
(defcomponent pica-button
  :material-ref {:button "http://www.google.com/design/spec/components/buttons.html"}
  :mixins [pica-button-base]
  :document
  (fn [_ {:keys [action] :as m}]
    [:host (properties m)
      (:name action)])
  :properties {:raised false}))

#?(:cljs
(defn- icon
  [m]
  (or (:icon m) (get-in m [:action :icon]))))

#?(:cljs
(defcomponent pica-icon-button
  :material-ref {:button "http://www.google.com/design/spec/components/buttons.html"}
  :mixins [pica-button-base]
  :document
  (fn [_ m]
    [:host (properties m)
      [:pica-icon {:icon (icon m)}]])
  :properties {:icon {:type :keyword :default nil}}))

#?(:cljs
(defcomponent pica-fab
  :material-ref {:button "http://www.google.com/design/spec/components/buttons-floating-action-button.html"}
  :mixins [pica-button-base]
  :document
  (fn [el {:keys [action disabled mini busy animation-icon-entry animation-icon-exit] :as m}]
    [:host ^:attrs (merge {:tabindex (if disabled -1 0)}
                    (if (or action busy)
                      {:name (:name action)
                       (trigger m) (comp/wrap-action action #(when-let [f (:fn action)] (set! (.-busy el) true) (f %) (set! (.-busy el) false)))}))
     [:pica-icon {:icon (icon m) :animation-icon-entry animation-icon-entry :animation-icon-exit animation-icon-exit}]
     (if busy
       [:pica-spinner {:attrs (if mini {:c 24 :r 22} {:c 32 :r 30})}])])
  :properties {:icon {:type :keyword :default nil} :mini false :busy false :animation-icon-entry {:type :keyword :default nil} :animation-icon-exit {:type :keyword :default nil}}))

#?(:cljs
(defcomponent pica-button-bar
  :material-ref {:button "http://www.google.com/design/spec/components/buttons.html"}))

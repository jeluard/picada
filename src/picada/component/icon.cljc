(ns picada.component.icon
  #?@(:cljs
      [(:require [picada.animation :as anim]
                 [picada.component :as comp]
                 [lucuma.core :as l]
                 [goog.string :as gstring]
                 goog.string.format)
       (:require-macros [picada.component :refer [defcomponent]])]))

(def styles
  [:pica-icon
   {:position "relative"
    :top 0
    :left 0
    :display "inline-flex"
    :justify-content "center"
    :align-items "center"}
   [:svg
     {:width "24px"
      :height "24px"
      :fill "currentColor"}]])

(def default-url-sprite-format (atom "svg-sprite-%s-symbol.svg#ic_%s_24px"))

(defn set-url-sprite-format!
  [s]
  (reset! default-url-sprite-format s))

(defn icon-id
  [c i]
  (#?(:clj format :cljs gstring/format) @default-url-sprite-format c i))

#?(:cljs
(defcomponent pica-icon
  :on-property-changed
  (fn [el s]
    (if-let [m (l/get-change s :icon)]
      (if (= :icon (:property m))
        (when-let [iel (.querySelector el "svg")]
          (if (and (:old-value m) (:new-value m))
            (if-let [anim (:animation-icon-exit (l/get-properties el))]
              (anim/animate iel anim)))
          (if (:new-value m)
            (if-let [anim (:animation-icon-entry (l/get-properties el))]
              (anim/animate iel anim)))))))
  :document
  (fn [_ {:keys [icon]}]
    [:host
     [:svg/svg
     (if icon
       [:svg/use {:xlink/href (icon-id (namespace icon) (name icon))}])]])
  :style styles
  :properties {:icon {:type :keyword :default nil} :animation-icon-entry {:type :keyword :default nil} :animation-icon-exit {:type :keyword :default nil}}))

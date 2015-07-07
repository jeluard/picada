(ns picada.component.icon
  #?@(:cljs
      [(:require [picada.animation :as anim]
                 [picada.component :as comp]
                 [lucuma.core :as l :refer-macros [defcustomelement]])]))

; http://www.google.com/design/spec/components/snackbars-toasts.html

(def styles
  [:pica-icon
   {:position "relative"
    :top 0
    :left 0
    :display "flex"
    :justify-content "center"
    :align-items "center"}
   [:i
    {:user-select "none"}]])

#?(:cljs
(defcustomelement pica-icon
  :mixins [anim/animation-lifecycle comp/reconciliate]
  :on-property-changed
  (fn [el s]
    (if-let [m (l/get-change s :icon)]
      (let [n (:property m)]
        (if (= :icon n)
          (when-let [iel (.querySelector el "i")]
            (if (and  (:old-value m) (:new-value m))
              (if-let [anim (:animation-icon-exit (l/get-properties el))]
                (anim/animate iel anim)))
            (if (:new-value m)
              (if-let [anim (:animation-icon-entry (l/get-properties el))]
                (anim/animate iel anim))))))))
  :document (fn [_ {:keys [icon]}] [:i {:class "material-icons"} icon])
  :properties {:icon "" :animation-icon-entry {:type :keyword :default nil} :animation-icon-exit {:type :keyword :default nil}}))

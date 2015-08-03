(ns picada.component.snackbar
  #?@(:clj
      [(:require [picada.color :as col]
                 [picada.style :as st]
                 [garden.stylesheet :refer [at-media]])]
      :cljs
      [(:require [picada.animation :as anim]
                 [picada.color :as col]
                 [picada.style :as st]
                 [hipo.core :as h]
                 [garden.stylesheet :refer [at-media]])
       (:require-macros [picada.component :refer [defcomponent]])]))

; TODO
; integrate with fab
; swipe-off dismiss snackbar

(def styles
  [:pica-snackbar
   {:box-sizing "border-box"
    :position   "fixed"
    :bottom     0
    :left       0
    :width      "100vw"
    :min-height "48px"
    :max-height "80px"
    :color      (str "var(--pica-snackbar-text-color, " (get-in col/text [:light :--secondary-text-color]) ")")
    :background "var(--pica-snackbar-background-color, #323232)"
    :padding    "16px 24px 12px"
    :font-size  "14px"
    :transition st/shadow-transition}
   (st/shadows 6)
   [:pica-button
    {:float "right"
     :height "auto"
     :padding-left "24px"
     :margin 0
     :line-height "initial"
     :color "var(--pica-snackar-action-color, var(--accent-color))"}]
   (at-media {:--desktop ""}
     (list
       [:&
        {:bottom "12px"
         :left "50%"
         :transform "translateX(-50%)"
         :width "initial"
         :min-width "288px"
         :max-width "568px"
         :border-radius "2px"}]))])

(def ^:private snackbar-id "snackbar")

#?(:cljs
(defn- append-snackbar
  [s d a]
  (let [el (h/create [:pica-snackbar ^:attrs (merge {:id snackbar-id} (if a {:action a})) s])]
    (.appendChild js/document.body el)
    (js/setTimeout #(anim/dismiss el) d))))

#?(:cljs
(defn show
  ([s] (show s nil))
  ([s a] (show s 3000 a))
  ([s d a]
   {:pre [(string? s) (pos? d) (or (nil? a) (map? a))]}
   (if-let [sel (.querySelector js/document (str "#" snackbar-id))]
     (anim/dismiss sel #(show s d a))
     (append-snackbar s d a)))))

#?(:cljs
(defcomponent pica-snackbar
  :material-ref {:snackar "http://www.google.com/design/spec/components/snackbars-toasts.html"}
  :document
  (fn [_ {:keys [action]}]
    [:host
      (if action
        [:pica-button {:action action}])])
  :style styles
  :properties {:action nil :animation-entry :snackbar-entry :animation-exit :snackbar-exit}))

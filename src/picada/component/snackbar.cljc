(ns picada.component.snackbar
  #?@(:clj
      [
       (:require [picada.color :as col]
                 [picada.style :as st]
                 [garden.stylesheet :refer [at-media]])]
      :cljs
      [(:require [picada.color :as col]
                 [picada.component :as comp]
                 [picada.style :as st]
                 [hipo.core :as h]
                 [garden.stylesheet :refer [at-media]])
       (:require-macros [lucuma.core :refer [defcustomelement]])]))

; http://www.google.com/design/spec/components/snackbars-toasts.html

; TODO
; integrate with fab
; swipe-off dismiss snackbar

(def ^:private transition-duration 300)

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
    :transition [st/shadow-transition (str "transform " transition-duration "ms")]}
   (st/shadows 6)
   ["&:not([visible])"
    {:transform "translateY(100px)"}]
   [:pica-button
    {:float "right"
     :height "auto"
     :padding-left "24px"
     :margin 0
     :line-height "initial"
     :color "var(--pica-snackar-action-color, var(--accent-color))"}
    [:&:hover
     {:cursor "pointer"}]]
   (at-media {:--desktop ""}
     (list
       [:&
        {:bottom "12px"
         :left "50%"
         :transform "translateX(-50%)"
         :width "initial"
         :min-width "288px"
         :max-width "568px"
         :border-radius "2px"}]
       ["&:not([visible])"
        {:transform "translateX(-50%) translateY(100px)"}]))])

#?(:cljs
(defn sel [] (.querySelector js/document "pica-snackbar")))

#?(:cljs
(defn dismiss
  [el]
  (when (.-visible el)
    (set! (.-visible el) false)
    (js/setTimeout #(.removeChild (.-parentElement el) el) transition-duration))))

#?(:cljs
(defn- append-snackbar
  [s d a]
  (let [[el _] (h/create [:pica-snackbar ^:attrs (if a {:action a}) s])]
    (.appendChild js/document.body el)
    (js/setTimeout #(set! (.-visible el) true) 20)
    (js/setTimeout #(dismiss el) d))))

#?(:cljs
(defn show
  ([s] (show s nil))
  ([s a] (show s 3000 a))
  ([s d a]
   {:pre [(string? s) (pos? d) (or (nil? a) (map? a))]}
   (if-let [sel (sel)]
     (do
       (dismiss sel)
       (js/setTimeout #(show s d a) (+ 10 transition-duration)))
     (append-snackbar s d a)))))

#?(:cljs
(defcustomelement pica-snackbar
  :mixins [comp/reconciliate]
  :document (fn [_ {:keys [action]}] [:pica-button {:action action}])
  :properties {:action nil :visible false}
  :methods {:dismiss dismiss}))

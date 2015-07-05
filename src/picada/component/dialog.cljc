(ns picada.component.dialog
  #?@(:clj [(:require [picada.color :as col]
                      [picada.style :as st])]
      :cljs
      [(:require [picada.aria :as aria]
                 [picada.animation :as anim]
                 [picada.color :as col]
                 [picada.component :as comp]
                 [picada.component.overlay :as ovl]
                 [picada.style :as st]
                 [hipo.core :as h]
                 [lucuma.core :as l :refer-macros [defcustomelement]])]))

(def styles
  [:pica-dialog
   {:display "flex"
    :justify-content "center"
    :align-items "center"
    :position "fixed"
    :top 0
    :left 0
    :height "100vh"
    :width "100vw"
    :outline "none"
    :z-index 24}
   ["> div"
    "> form"
    {:background "white"
     :outline "none"
     :min-width "200px"
     :max-width "400px"
     :transition st/shadow-transition}
    (st/shadows 24)
    ["> *"
     {:margin "0px 24px"
      :padding "24px 0"}]
    [:h2
     {:text-rendering "optimizeLegibility"
      :word-wrap "break-word"
      :white-space "nowrap"
      :overflow "hidden"
      :text-overflow "ellipsis"
      :font-size "20px"
      :font-weight 500
      :padding-bottom "8px"
      :line-height "28px"}]
    [:p
     {:color "var(--pica-dialog-paragraph-color," (get-in col/text [:dark :--secondary-text-color]) ")"}]
    ["h2 + *"
     {:margin-top "20px"}]
    ["h2 + *"
     {:padding-top 0}]
    [:.actions
     {:display "flex"
      :justify-content "flex-end"
      :margin 0
      :padding "8px 8px 8px 24px"
      :color "var(--pica-dialog-button-color, var(--primary-color))"}
     [:pica-button
      {:min-width "64px"}]
     [:pica-button.confirm
      {:font-weight "bold"}]
     ["pica-button:not([disabled]).discard"
      {:color "var(--pica-dialog-button-discard-color, currentColor)"}]
     ["pica-button:not([disabled]).confirm"
      {:color "var(--pica-dialog-button-confirm-color, currentColor)"}]]]])

#?(:cljs
(defn dismiss
  [el]
  (ovl/dismiss)
  (anim/hide el #(.remove el))))

(def ^:private confirm-class "confirm")

#?(:cljs
(defn button-confirm
  [fel]
  (.querySelector fel (str "pica-button." confirm-class))))

#?(:cljs
(defn show
  ([el] (show el false))
  ([el modal?] (show nil el modal?))
  ([pel el modal?]
   (if modal?
     (let [oel (.createElement js/document "pica-overlay")]
       (.appendChild (.-body js/document) oel)
       (.-position (.getComputedStyle js/window oel))
       (set! (.-visible oel) true))
     (.addEventListener el "click" #(if (= el (.-target %)) (dismiss el))))
   (.addEventListener el "keydown"
                      #(condp = (.-which %)
                        27 (dismiss el)
                        13 (if-let [fel (.querySelector el "form")]
                             (if-let [bel (button-confirm fel)]
                               (if-let [a (l/get-property bel :action)]
                                 (if-not (.-disabled bel)
                                   ((:fn a) %)))))
                        nil))
   (.appendChild (or pel (.-body js/document)) el)
   (.focus el)
   el)))

#?(:cljs
(defn input-id
  [iel]
  (let [id (.-id iel)]
    (if-not (empty? id)
      id
      (if-let [lel (.-nextElementSibling iel)]
        (if lel
          (.-textContent lel)))))))

#?(:cljs
(defn values
  [el]
  (into {} (for [iel (array-seq (.-elements el))] [(keyword (input-id iel)) (.-value iel)]))))

#?(:cljs (defn wrap [a] (comp/wrap-action a #(do (if %2 (%2 %1)) (dismiss (.closest (.-target %1) "pica-dialog"))))))
#?(:cljs (defn wrap-with-values [a] (comp/wrap-action a #(do (if %2 (%2 %1 (values (.closest (.-target %1) "form")))) (dismiss (.closest (.-target %1) "pica-dialog"))))))

; TODO changes to children should trigger reconciliation: mixin react-on-container-changes

#?(:cljs
(defn create-actions
  ([d] (create-actions nil d))
  ([c d]
   (if (or c d)
     [:div {:class "actions"}
      (if d [:pica-button {:class "discard" :action (wrap d)}])
      (if c [:pica-button {:class confirm-class :action (wrap-with-values c)}])]))))

#?(:cljs
(defn create-dialog
  [k m t c mc md]
  (first (h/create
           [:pica-dialog ^:attrs (merge {:animate-on-attached true :animation-entry "zoom-in" :animation-exit "zoom-out"} m)
            (let [cs (list (if-not (empty? t) [:h2 t])
                           c (create-actions mc md))]
              (if (= :div k)
                [:div cs]
                [:form cs]))]))))

#?(:cljs
(defn show-alert
  ([s] (show-alert s nil))
  ([s mc] (show-alert s mc nil))
  ([s mc md] (show-alert nil s mc md))
  ([t s mc md] (show-alert false t s mc md))
  ([modal? t s mc md] (show-alert nil modal? {} t s mc md))
  ([pel modal? m t s mc md]
   (let [el (create-dialog :div m t [:p s] mc md)]
     (show pel el modal?)))))

#?(:cljs
   (defn show-form
     ([t s] (show-form false t s nil nil))
     ([t s mc] (show-form false t s mc nil))
     ([t s mc md] (show-form false t s mc md))
     ([modal? t s mc md] (show-form nil modal? {} t s mc md))
     ([pel modal? m t s mc md]
      (let [el (create-dialog :form m t s mc md)]
        (show pel el modal?)))))

#?(:cljs
(defn show-multisteps-form
  [v]
  ))

(def ^:private labeled-by "dialog-title")
(def ^:private described-by "dialog-content")

#?(:cljs
(defn adjust-button-validity!
  ([fel bel] (adjust-button-validity! fel bel (.checkValidity fel)))
  ([fel bel v]
   (if bel
     (set! (.-disabled bel) (not v))))))

#?(:cljs
(defcustomelement pica-dialog
  {comp/material-ref {:button ""}}
  :mixins [anim/animation-lifecycle]
  :on-created #(do (.setAttribute % "tabindex" 1) (.setAttribute % "role" "dialog"))
  :on-attached
  (fn [el]
    (aria/set-labelled-by! el (.querySelector el "pica-dialog > div > h2") labeled-by)
    (aria/set-described-by! el (.querySelector el "pica-dialog > div > p") described-by)
    (when-let [fel (.querySelector el "form")]
      (let [bel (button-confirm fel)]
        (.requestAnimationFrame js/window (fn [] (adjust-button-validity! fel bel)))
        (adjust-button-validity! fel bel)
        (.addEventListener fel "invalid" #(adjust-button-validity! fel bel))
        (doseq [cel (array-seq (.-elements fel))]
          (.addEventListener cel "change" #(adjust-button-validity! fel bel))
          (.addEventListener cel "input" #(adjust-button-validity! fel bel))))))
  :methods {:dismiss dismiss}))
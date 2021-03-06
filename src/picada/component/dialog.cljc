(ns picada.component.dialog
  (:require [picada.color :as col]
            [picada.style :as st]
            [picada.typography :as typ]
            #?@(:cljs
              [[picada.aria :as aria]
               [picada.animation :as anim]
               [picada.component :as comp]
               [hipo.core :as h]
               [lucuma.core :as l]]))
  #?(:cljs (:require-macros [picada.component :refer [defcomponent]])))

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
      :font-weight (:medium typ/font-weights)
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
      {:font-weight (:bold typ/font-weights)}]
     ["pica-button:not([disabled]).discard"
      {:color "var(--pica-dialog-button-discard-color, currentColor)"}]
     ["pica-button:not([disabled]).confirm"
      {:color "var(--pica-dialog-button-confirm-color, currentColor)"}]]]])

#?(:cljs (def ^:private input-id-prefix "pica-dia-"))
#?(:cljs (defn id->input-id [s] (str input-id-prefix s)))
#?(:cljs (defn input-id->id [s] (subs s (count input-id-prefix))))

#?(:cljs
(defn dismiss
  [el]
  (if-let [oel (.querySelector js/document "pica-overlay")]
    (anim/dismiss oel))
  (anim/dismiss el)))

(def ^:private confirm-class "confirm")
(def ^:private discard-class "discard")

#?(:cljs
(defn button-confirm
  [fel]
  (.querySelector fel (str "pica-button." confirm-class))))

#?(:cljs
(defn button-discard
  [fel]
  (.querySelector fel (str "pica-button." discard-class))))

; TODO Cancel action must be called when dismissing dialog. Might requiring introducing a different component storing actions.

#?(:cljs
(defn call-action-for
  [el c]
  (if-let [fel (.querySelector el "form")]
    (if-let [bel (.querySelector fel (str "pica-button." c))]
      (if-let [a (l/get-property bel :action)]
        (if-not (.-disabled bel)
          ((:fn a) el)))))))

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
     (.addEventListener el "click" #(when (= el (.-target %))
                                      (call-action-for el discard-class)
                                      (dismiss el))))
   (.addEventListener el "keydown"
                      #(condp = (.-which %)
                        27 (do (call-action-for el discard-class) (dismiss el))
                        13 (if-let [fel (.querySelector el "form")]
                             (call-action-for el confirm-class))
                        nil))
   (.appendChild (or pel (.-body js/document)) el)
   (.focus el)
   el)))

#?(:cljs
(defn input-id
  [iel]
  (let [id (.-id iel)]
    (if-not (empty? id)
      (input-id->id id)
      (if-let [lel (.-nextElementSibling iel)]
        (if lel
          (.-textContent lel)))))))

#?(:cljs
(defn values
  [el]
  (into {} (for [iel (array-seq (.-elements el))] [(input-id iel) (.-value iel)]))))

#?(:cljs (defn wrap [a] (comp/wrap-action a #(do (if %2 (%2 %1)) (dismiss (.closest (.-target %1) "pica-dialog"))))))
#?(:cljs (defn wrap-with-values [a] (comp/wrap-action a #(do (if %2 (if-let [fel (.closest (.-target %1) "form")] (%2 %1 (values fel)) (%2 %1))) (dismiss (.closest (.-target %1) "pica-dialog"))))))

; TODO changes to children should trigger reconciliation: mixin react-on-container-changes

#?(:cljs
(defn create-actions
  ([d] (create-actions nil d))
  ([c d]
   (if (or c d)
     [:div {:class "actions"}
      (if d [:pica-button {:class discard-class :action (wrap d)}])
      (if c [:pica-button {:class confirm-class :action (wrap-with-values c)}])]))))

#?(:cljs
(defn create-dialog
  [k m t c mc md]
  (h/create [:pica-dialog ^:attrs (merge {:show-on-attached true :animation-entry "zoom-in" :animation-exit "zoom-out"} m)
             (let [cs (list (if-not (empty? t) [:h2 t])
                            c (create-actions mc md))]
               (if (= :div k)
                 [:div cs]
                 [:form cs]))])))

#?(:cljs
(defn show-alert
  ([s] (show-alert s nil))
  ([s mc] (show-alert s mc nil))
  ([s mc md] (show-alert true s mc md))
  ([modal? s mc md] (show-alert modal? nil s mc md))
  ([modal? t s mc md] (show-alert modal? {} t s mc md))
  ([modal? m t s mc md] (show-alert nil modal? m t s mc md))
  ([pel modal? m t s mc md]
   (let [el (create-dialog :div m t [:p s] mc md)]
     (show pel el modal?)))))

#?(:cljs
(defn show-form
  ([t s] (show-form t s nil))
  ([t s mc] (show-form t s mc nil))
  ([t s mc md] (show-form true t s mc md))
  ([modal? t s mc md] (show-form modal? {} t s mc md))
  ([modal? m t s mc md] (show-form nil modal? m t s mc md))
  ([pel modal? m t s mc md]
    (let [el (create-dialog :form m t s mc md)]
      (show pel el modal?)))))

(def ^:private labeled-by "dialog-title")
(def ^:private described-by "dialog-content")

#?(:cljs
(defn adjust-button-validity!
  ([fel bel] (adjust-button-validity! fel bel (.checkValidity fel)))
  ([fel bel v]
   (if bel
     (set! (.-disabled bel) (not v))))))

#?(:cljs
(defcomponent pica-dialog
  :material-ref {:dialog ""}
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
  :style styles
  :methods {:dismiss dismiss}))

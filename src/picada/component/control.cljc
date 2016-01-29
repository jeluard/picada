(ns picada.component.control
  #?@(:clj [(:require [picada.color :as col]
                      [picada.style :as sty])]
      :cljs
      [(:require [picada.component.dialog :as dia]
                 [picada.color :as col]
                 [picada.style :as sty]
                 [lucuma.core :as l])
       (:require-macros [picada.component :refer [defcomponent]])]))
; http://hiram-madelaine.github.io/om-inputs/playground/
(def ^:private line-height "29px")

(def styles
  [[:pica-input
    {:display "block"
     :position "relative"}
    [:input
     {:background   "none"
      :padding      "2px 2px 1px"
      :font-size    "18px"
      :border-width "0 0 1px"
      :line-height  line-height
      :border-color "#999"
      :width        "100%"
      :outline      "none"
      :box-shadow "none"}]
    [:label
     {:position "absolute"
      :top 0
      :left 0
      :line-height line-height
      :color (str "var(--pica-input-disabled-label-color, " (get-in col/text [:dark :--disabled-text-color]) ")")
      :pointer-events "none"}]
    ["input:focus + label"
     {:transition "all 0.25s"}]
    [:label.floating
     {:color "var(--pica-input-valid-label-color, var(--pica-input-valid-color, var(--primary-color)))"
      :font-size "12px"
      :top "-16px"}]
    [:.underline
     {:width "100%"
      :position "relative"
      :height "2px"
      :bottom "1px"
      :transform-origin "center center"
      :transform "scale3d(0,1,1)"
      :padding "0 2px"
      :background-color "var(--pica-input-underline-color, var(--disabled-text-color))"}]
    ["input:not(.validating):valid ~ div.underline"
     {:transition "all 0.25s"}]
    ["input:not(.validating):valid:focus ~ div.underline"
     {:background-color "var(--pica-input-valid-underline-color, var(--pica-input-valid-color, var(--primary-color)))"
      :transform "none"}]
    ["input:not(.validating):invalid ~ div.underline"
     {:background-color "var(--pica-input-invalid-underline-color, var(--pica-input-invalid-color, var(--error-color)))"
      :transform "none"}]
    [:.error
     {:font-size "14px";
      :position "relative"
      :float "left"
      :top "4px"
      :width "100%"
      :white-space "nowrap"
      :overflow "hidden"
      :text-overflow "ellipsis"
      :color "var(--pica-input-error-color, var(--error-color))"}]
    [:.error:empty
     {:display "none"}]]
   [:pica-checkbox
    {:display "block"
     :height "18px"}
    [:input
     {:position "relative"
      :vertical-align "text-bottom"
      :width "18px"
      :height "18px"
      :margin "0 8px 0 0"}
     [:&:before
      {:display "block"
       :box-sizing "border-box"
       :width "100%"
       :height "100%"
       :content "''"
       :background-color "white"
       :border "2px solid"
       :border-radius "2px"
       :transition "all .2s"}]
     [:&:checked
      [:&:before
       {:background-color "var(--pica-checkbox-color, var(--accent-color))"
        :border-color "var(--pica-checkbox-color, var(--accent-color))"
        :transition "all .2s"}]
      [:&:after
       {:content "''"
        :box-sizing "border-box"
        :transform "rotate(45deg)"
        :border "2px solid white"
        :width "5px"
        :position "absolute"
        :left "7px"
        :top "3px"
        :height "10px"}
       {:border-top "none"
        :border-left "none"}]]
     [:&:hover
      {:cursor "pointer"}]]]
   [:pica-switch
    {:display "block"}
    [:input
     {:opacity 0
      :position "absolute"
      :width "35px"
      :margin 0
      :height "15px"
      :outline "none"}
     [:&:hover
      {:pointer "cursor"}]]
    [:span
     {:position "relative"
      :width "35px"
      :height "15px"
      :border-radius "15px"
      :display "inline-block"
      :margin-right "8px"
      :background-color "grey"
      :pointer-events "none"
      :outline "none"}
     [:div
      {:content "''"
       :display "block"
       :position "absolute"
       :top "-3px"
       :left 0
       :width "20px"
       :height "20px"
       :background-color "white"
       :border-radius "50%"
       :transform "none"
       :transition "all .08s"
       :outline "none"
       :will-change "transform"}
      (sty/shadows 1)]]
    ["input:checked + span"
     {:background-color "color(var(--pica-switch-color, var(--accent-color)) alpha(-50%))"}]
    ["input:checked + span div"
     {:background-color "var(--pica-switch-color, var(--accent-color))"
      :transform "translate(20px)"}]]])

#?(:cljs
(defn add-class
  [el s]
  (.add (.-classList el) s)))

#?(:cljs
(defn remove-class
  [el s]
  (.remove (.-classList el) s)))

#?(:cljs
(defn valid?
  [iel]
  (.. iel -validity -valid)))

#?(:cljs
(defn adjust-error-message!
  [iel eel]
  (set! (.-textContent eel) (.. iel -validationMessage))))

(def ^:private validating-class "validating")

#?(:cljs
(defn on-delayed-result
  [iel v submitter validating? o]
  (let [fel (.closest iel "form")
        cbel (dia/button-confirm fel)]
    (if (string? o)
      (.setCustomValidity iel o)
      (do (.setCustomValidity iel "")
          (if (and submitter (not cbel))
            (submitter iel v))))
    (when validating?
      (adjust-error-message! iel (.querySelector (.-parentElement iel) "div.error"))
      (dia/adjust-button-validity! fel cbel)
      (remove-class iel validating-class)))))

#?(:cljs
(defprotocol IResetableTimer
  (reset [this] [this o])
  (cancel [this])))

#?(:cljs
(defn create-timer
  [f i]
  (let [aid (atom nil)]
    (reify IResetableTimer
      (reset [this] (reset this nil))
      (reset [this o]
        (cancel this)
        (reset! aid (js/setTimeout (if o #(f o) f) i)))
      (cancel [_]
        (if-let [id @aid] (js/clearTimeout id)))))))

#?(:cljs
(defn input-listener
  [t validating?]
  (fn [evt]
    ; TODO if running call prevent next call? or cancel current?
    (let [iel (.-target evt)
          fel (.closest iel "form")
          cbel (dia/button-confirm fel)]
      (.setCustomValidity iel "")
      (when (valid? iel)
        (when validating?
          (add-class iel validating-class)
          (adjust-error-message! iel (.querySelector (.-parentElement iel) "div.error"))
          (dia/adjust-button-validity! fel cbel false))
        (reset t evt))))))

#?(:cljs
(defn delayed-listener
  [el fom submitter]
  (let [f (or (:fn fom) fom)
        rf (or f #(%2))
        d (or (:delay fom) 500)
        iel (.querySelector el "input")
        validating? (not (nil? f))
        t (create-timer #(let [v (.-value iel)] (rf % (fn [o] (on-delayed-result iel v submitter validating? o)) v)) d)]
    (input-listener t validating?))))

#?(:cljs
(defcomponent pica-input
  :material-ref {:text-field ["http://www.google.com/design/spec/components/text-fields.html"
                                   "http://www.google.com/design/spec/patterns/errors.html"]}
  :on-attached (fn [el]
                 (let [iel (.querySelector el "input")
                       lel (.querySelector el "label")
                       eel (.querySelector el "div.error")]
                   (adjust-error-message! iel eel)
                   (.addEventListener iel "input" #(adjust-error-message! iel eel))
                   (if-not (empty? (.-value iel))
                     (add-class lel "floating"))
                   (.addEventListener iel "change" #(if-not (empty? (.-value iel))
                                                    (add-class lel "floating")))
                   (.addEventListener iel "focus" #(add-class lel "floating"))
                   (.addEventListener iel "blur" #(if-not (empty? (.. % -target -value))
                                                   (add-class lel "floating")
                                                   (remove-class lel "floating")))))
  :on-property-changed #(let [m (l/changes->map %2)
                              attrs (:input-attributes m)]
                (if attrs
                  ; Check performed on rAF as input attributes are propagated async
                  (.requestAnimationFrame js/window (fn [] (adjust-error-message! (.querySelector % "input") (.querySelector % "div.error")))))
                (if (or (contains? m :value) (contains? attrs :value))
                  (if (or (:value m) (:value attrs))
                    (add-class (.querySelector %1 "label") "floating")
                    (remove-class (.querySelector %1 "label") "floating"))))
  :document
  (fn [el {:keys [id validator submitter label value input-attributes]}]
    (let [by (if-not (empty? id) (dia/id->input-id id))]
      [:host
       [:input ^:attrs (merge
                         (if id {:id by})
                         (if (or validator submitter)
                           {:on-input (delayed-listener el validator submitter)})
                         (if value {:value value})
                         input-attributes)]
       [:label ^:attrs (merge (if by {:for by})) label]
       [:div.underline]
       [:div.error]]))
  :style styles
  :properties {:id {:default "" :override? true} :value "" :input-attributes nil :validator nil :submitter nil :label "" :error-mapping nil}))

; TODO  checked must reflect input value
#(:cljs
  (def checkbox
    {:style styles
     :properties {:checked false :label ""}}))

#?(:cljs
(defcomponent pica-checkbox
  :material-ref {:checkbox ["http://www.google.com/design/spec/components/selection-controls.html#selection-controls-checkbox"]}
  :document
  (fn [_ {:keys [checked label]}]
    [:host
     [:label
      [:input {:type "checkbox" :checked checked}]
      label]])
  :style styles
  :properties {:checked false :label ""}))

#?(:cljs
(defcomponent pica-switch
  :material-ref {:switch ["http://www.google.com/design/spec/components/selection-controls.html#selection-controls-switch"]}
  :document
  (fn [_ {:keys [checked label]}]
    [:host
     [:label
      [:input {:type "checkbox" :checked checked}]
      [:span
       [:div]]
      label]])
  :style styles
  :properties {:checked false :label ""}))

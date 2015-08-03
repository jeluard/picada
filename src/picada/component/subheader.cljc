(ns picada.component.subheader
  (:require [picada.color :as col]))

; http://www.google.com/design/spec/components/subheaders.html
; Used with List, Grid and Menu

(def styles
  [:h2
   {:font-size "14px"
    :margin 0
    :padding "16px"
    :color (str "var(--pica-subheader-color, " (get-in col/text [:dark :--secondary-text-color]) ")")}])

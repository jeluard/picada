(ns picada.styles
  (:require [picada.bootstrap :as boot]
            [picada.style :as st]
            [garden.def :refer [defrule defstyles]]
            [garden.stylesheet :refer [rule]]))

; TODO
; boot-garden won't recompile if this ns is not part of changes even so pointed ns does change
; https://github.com/martinklepsch/boot-garden/issues/3

(def all
  [["@custom-media --desktop (min-width: 600px);" {}]
   ["@custom-selector --pica-button pica-button, pica-fab, pica-icon-button;" {}]
   [":root" (merge
              (st/create-theme :light-blue :deep-orange)
              {:--error-color "#db4437"})]
   [:body
    {:font-family "RobotoDraft, Arial"}]
   boot/all-styles])
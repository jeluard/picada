(ns picada.style
  (:require [picada.color :as col]))

; TODO z-index
; Snackbars appear above most elements on screen, and they are equal in elevation to the floating action button.
; However, they are lower in elevation than dialogs, bottom sheets, and navigation drawers.

; http://www.google.com/design/spec/what-is-material/elevation-shadows.html
(def shadow-transition [:box-shadow "0.28s" "cubic-bezier(0.4, 0, 0.2, 1)"])

(defn shadows
  [z]
  ; TODO find proper values for 12 and 24 and add 9
  {:z-index z
   :box-shadow
   (case z
     1  [[0 "3px"  "1px"  "-2px" "rgba(0, 0, 0, 0.14)"]
         [0 "2px"  "2px"  0      "rgba(0, 0, 0, 0.098)"]
         [0 "1px"  "5px"  0      "rgba(0, 0, 0, 0.084)"]]
     2  [[0 "2px"  "2px"  0      "rgba(0, 0, 0, 0.14)"]
         [0 "1px"  "5px"  0      "rgba(0, 0, 0, 0.12)"]
         [0 "3px"  "1px"  "-2px" "rgba(0, 0, 0, 0.2)"]]
     3  [[0 "3px"  "4px"  0      "rgba(0, 0, 0, 0.14)"]
         [0 "1px"  "8px"  0      "rgba(0, 0, 0, 0.12)"]
         [0 "3px"  "3px"  "-2px" "rgba(0, 0, 0, 0.4)"]]
     4  [[0 "4px"  "5px"  0      "rgba(0, 0, 0, 0.14)"]
         [0 "1px"  "10px" 0      "rgba(0, 0, 0, 0.12)"]
         [0 "2px"  "4px"  "-1px" "rgba(0, 0, 0, 0.4)"]]
     6  [[0 "6px"  "10px" 0      "rgba(0, 0, 0, 0.14)"]
         [0 "1px"  "18px" 0      "rgba(0, 0, 0, 0.12)"]
         [0 "3px"  "5px"  "-1px" "rgba(0, 0, 0, 0.4)"]]
     8  [[0 "8px"  "10px" "1px"  "rgba(0, 0, 0, 0.14)"]
         [0 "3px"  "18px" "2px"  "rgba(0, 0, 0, 0.12)"]
         [0 "5px"  "5px"  "-3px" "rgba(0, 0, 0, 0.4)"]]
     12 [[0 "8px"  "10px" "1px"  "rgba(0, 0, 0, 0.14)"]
         [0 "3px"  "18px" "2px"  "rgba(0, 0, 0, 0.12)"]
         [0 "5px"  "5px"  "-3px" "rgba(0, 0, 0, 0.4)"]]
     16 [[0 "16px" "24px" "2px"  "rgba(0, 0, 0, 0.14)"]
         [0 "6px"  "30px" "5px"  "rgba(0, 0, 0, 0.12)"]
         [0 "8px"  "10px" "-5px" "rgba(0, 0, 0, 0.4)"]]
     24 [[0 "16px" "24px" "2px"  "rgba(0, 0, 0, 0.14)"]
         [0 "6px"  "30px" "5px"  "rgba(0, 0, 0, 0.12)"]
         [0 "8px"  "10px" "-5px" "rgba(0, 0, 0, 0.4)"]])})

(defn light-theme?
  [kp]
  (some #{kp} #{:light-green :lime :yellow :amber :grey}))

(defn create-theme
  [kp ka]
  (merge
    (let [p (kp col/palette)]
      {:--dark-primary-color (:700 p) ; use for status bar color
       :--primary-color (:500 p)
       :--light-primary-color (:100 p)})
    {:--accent-color (:A200 (ka col/palette))
     :--accent-icon-color (get-in col/text [(if (light-theme? ka) :dark :light) :--icon-color])}
    (get col/text (if (light-theme? kp) :dark :light))
    {:--disabled-background-color "#eaeaea"
     :--error-color "#db4437"}))

(ns picada.styles
  (:require [picada.bootstrap :as bs]))

; TODO
; boot-garden won't recompile if this ns is not part of changes even so pointed ns does change
; https://github.com/martinklepsch/boot-garden/issues/3

(def all (bs/all :light-blue :deep-orange))
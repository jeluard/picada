(ns picada.component.toolbar
  #?@(:clj
      [
       (:require [picada.color :as col]
                 [picada.style :as sty]
                 [garden.stylesheet :refer [at-media]])]
      :cljs
      [(:require [picada.color :as col]
                 [picada.component :as comp]
                 [picada.component.drawer :as dra]
                 [picada.style :as sty]
                 [hipo.core :as h]
                 [garden.stylesheet :refer [at-media]])
       (:require-macros [lucuma.core :refer [defcustomelement]])]))

(def styles
  [:pica-toolbar
   {:display "block"
    :min-height "56px"
    :color "var(--pica-snackbar-text-color, var(--secondary-text-color))"}
   ["&[primary]"
    {:position "fixed"
     :top 0
     :left 0
     :width "100%"}]
   ["&:not([transparent])"
    {:background "var(--pica-toolbar-background-color, var(--primary-color))"}
    (sty/shadows 4)]
   [:div
    {:display "flex"
     :align-items "center"}]
   [:.left-actions
    {}]
   [:h2
    {:padding-left "24px"
     :color "var(--pica-toolbar-title-color, var(:--primary-text-color))"}]
   [:.flex
    {:flex 1}]
   [:.right-actions
    {:cursor "pointer"}]
   (at-media {:--desktop ""}
     (list
       [:&
        {:height "64px"}]
       [:h2
        {:padding-left "32px"}]))])

; Appbar http://www.google.com/design/spec/layout/structure.html#structure-app-bar

#?(:cljs
(defcustomelement pica-toolbar
  {comp/material-ref {:toolbar "http://www.google.com/design/spec/components/toolbars.html"
                      :structure "http://www.google.com/design/spec/layout/structure.html#structure-toolbars"}}
  :mixins [comp/reconciliate]
  :document
  (fn [_ {:keys [title left-actions right-actions]}]
    [:div
     [:span {:class "left-actions"}
      (for [m left-actions]
        [:pica-icon-button {:action m}])]
     [:h2 title]
     [:span {:class "flex"}]
     [:span {:class "right-actions"}
      (for [m right-actions]
        [:pica-icon-button {:action m}])]])
  :properties {:primary false :title {:default "" :override? true} :left-actions nil :right-actions nil :transparent false}))

#?(:cljs
(defcustomelement pica-app-bar
  {comp/material-ref {:app-bar "http://www.google.com/design/spec/components/toolbars.html"
                      :structure "http://www.google.com/design/spec/layout/structure.html#structure-app-bar"}}
  :mixins [comp/reconciliate]
  :document
  (fn [_ {:keys [title nav transparent]}]
    [:pica-toolbar {:primary true :title title :left-actions [{:icon "menu" :fn #(dra/show nav) :name "Side navigation"}] :transparent transparent}])
  :properties {:title {:default "" :override? true} :nav nil :menu nil :actions nil :transparetn false}))

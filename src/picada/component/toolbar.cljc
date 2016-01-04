(ns picada.component.toolbar
  #?@(:clj
      [(:require [picada.style :as sty]
                 [garden.stylesheet :refer [at-media]])]
      :cljs
      [(:require [picada.component.drawer :as dra]
                 [picada.style :as sty]
                 [garden.stylesheet :refer [at-media]])
       (:require-macros [picada.component :refer [defcomponent]])]))
; https://design.google.com/articles/evolving-the-google-identity/
(def styles
  [:pica-toolbar
   {:display "flex"
    :align-items "center"
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
   ["&[primary]:not([transparent])"
    {:z-index 15 ; just bellow drawers
    }]
   [:h2
    {:margin 0
     :padding-left "24px"
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

#?(:cljs
(defcomponent pica-toolbar
  :material-ref {:toolbar "http://www.google.com/design/spec/components/toolbars.html"
                 :structure "http://www.google.com/design/spec/layout/structure.html#structure-toolbars"}
  :document
  (fn [_ {:keys [title left-actions right-actions]}]
    [:host
     [:div {:class "left-actions"}
      (for [m left-actions]
        [:pica-icon-button {:action m}])]
     [:h2 title]
     [:div {:class "flex"}]
     (if right-actions
       [:div {:class "right-actions"}
        (for [m right-actions]
          [:pica-icon-button {:action m}])])])
  :style styles
  :properties {:primary false :title {:default "" :override? true} :left-actions nil :right-actions nil :transparent false}))

#?(:cljs
(defn create-app-bar
  "http://www.google.com/design/spec/components/toolbars.html
   http://www.google.com/design/spec/layout/structure.html#structure-app-bar"
  ([s nav] (create-app-bar false s nav nil))
  ([t s nav menu]
    [:pica-toolbar {:primary true :title s :left-actions [{:icon :navigation/menu :fn #(dra/show nav) :name "Side navigation"}] :transparent t}])))

(ns picada.component.grid-list
  (:require [picada.color :as col]
            [garden.stylesheet :refer [at-media]])
  #?(:cljs (:require-macros [picada.component :refer [defcomponent]])))

(def styles
  (list
    [:pica-grid-list
     {:display "flex"
      :flex-wrap "wrap"
      :color (get-in col/text [:light :--primary-text-color])
      :margin "0 2px"}]
     (at-media {:--desktop ""}
      (list
        [:&
         {:margin "0 4px"}]))
    [:pica-tile
     {:display "inline-block"
      :position "relative"
      :width "calc( 50% - 4px )" ; 4px = 2*2px
      :height "50vw"
      :margin "2px"}
     (at-media {:--desktop ""}
       (list
         [:&
          {:width "calc( 100% / 3 - 16px )" ; 16px = 2*8px
           :height "30vw"
           :margin "8px"}]))
     [:img
      {:width "100%"
       :height "100%"
       :object-fit "cover"}]
     ["img:hover"
      {:cursor "pointer"}]
     ["> header, footer"
      {:position "absolute"
       :width "100%"
       :height "48px"
       :pointer-events "none"}]
     ["> header"
      {:top 0}]
     ["> footer"
      {:bottom 0}]
     ["> footer h2:not(:only-child)"
       {:max-width "calc(100% - 32px)"}]
     ["pica-icon-button"
      {:float "right"
       :pointer-events "initial"}
      ["&:hover"
       {:cursor "pointer"
        :color "var(--pica-tile-icon-color, var(--accent-color))"}]]
     ["h2"
      {:position "absolute"
       :max-width "100%"
       :margin 0
       :font-size "16px"
       :line-height "16px"
       :padding "16px"
       :text-overflow "ellipsis"
       :white-space "nowrap"
       :overflow "hidden"}]]))

#?(:cljs
(defcomponent pica-grid-list
  :material-ref {:grid-list "https://www.google.com/design/spec/components/grid-lists.html"}
  :style styles))

#?(:cljs
(defcomponent pica-tile
  :material-ref {:tile "https://www.google.com/design/spec/components/grid-lists.html"}
  :document
  (fn [el {:keys [title primary-action secondary-action src]}]
    [:host
      [:img {:on-click (:fn primary-action) :src src}]
      [:footer
        [:h2
          title]
        [:pica-icon-button {:action secondary-action}]]])
  :style styles
  :properties {:title {:override? true :default ""} :primary-action nil :secondary-action nil :src ""}))

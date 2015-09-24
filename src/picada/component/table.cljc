(ns picada.component.table
  #?@(:clj
      [(:require [picada.style :as sty]
                 [garden.stylesheet :refer [at-media]])]
      :cljs
      [(:require [picada.component.drawer :as dra]
                 [picada.style :as sty]
                 [garden.stylesheet :refer [at-media]])
       (:require-macros [picada.component :refer [defcomponent]])]))

(def styles
  [:pica-table
   {:display "block"}])

(defn header-mapping
  [v]
  (mapv name (keys (first v))))

(defn create-header
  [data v]
  (let [s (or v (header-mapping data))]
    [:thead
     [:tr (for [o s] [:th (name o)])]]))

#?(:cljs
(defcomponent pica-table
  :material-ref {}
  :document
  (fn [_ {:keys [data header header-mapping]}]
    [:host
     [:table
      (if header
        (create-header data header-mapping))
      [:tbody
       (for [m data]
         [:tr
          (for [[k v] m]
            [:td (str v)])])]]])
  :style styles
  :properties {:data nil :header false :header-mapping nil :selectable false}))
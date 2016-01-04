(ns picada.main
  (:require [picada.bootstrap :as boot]
            [picada.component.dialog :as pdia]
            [picada.component.drawer :as pdra]
            [picada.component.menu :as pmen]
            [picada.component.snackbar :as psna]
            [picada.component.toolbar :as ptb]
            [hipo.core :as h]
            cljsjs.document-register-element
            cljsjs.dom4
            cljsjs.web-animations)
  (:require-macros [picada.doc :refer [doc]]))

(boot/register-all)

(defn ^:export open-snackbar [] (psna/show "Test snackar" {:name "Undo" :fn #(psna/show "You UNDOed the snackbar")}))
(defn ^:export open-alert [] (pdia/show-alert "Just wanted to inform you." {:name "OK" :fn #(psna/show "You clicked OK")}))
(defn ^:export open-modal-alert
  []
  (pdia/show-alert nil true {:animation-entry "zoom-in" :animation-exit "zoom-out"} "Use your location?" "Yes please I really want to use your location."
                   {:name "OK" :fn #(psna/show "You clicked OK")}
                   {:name "Undo" :fn #(psna/show "You clicked Undo")}))

(defn ^:export open-dialog-form
  []
  (pdia/show-form "Some stuff"
                  (list
                    [:pica-input {:label "Nom" :validator (fn [evt f] (.setTimeout js/window #(f (if-let [s (.. evt -target -value)] (if (< 6 (count s)) "No more than 6 characters"))) 1000))
                                  :input-attributes {:pattern ".{3,}" :required true :autofocus true}}]
                    [:pica-input {:label "Prénom" :value "Jean-Claude"}]
                    [:pica-checkbox {:checked true :label "Check this"}]
                    [:pica-switch {:checked true :label "Switch this"}])
                  {:name "OK" :fn #(pdia/show-alert (str "Got values: " %2))} {:name "Cancel"}))

#_
(defn ^:export open-dialog-multi-form
  []
  (pdia/show-multi-form
    [{}]
    "Some stuff"
                  (list
                    [:pica-input {:label "Nom" :validator (fn [evt f] (.setTimeout js/window #(f (if-let [s (.. evt -target -value)] (if (< 6 (count s)) "No more than 6 characters"))) 1000))
                                  :input-attributes {:pattern ".{3,}" :required true :autofocus true}}]
                    [:pica-input {:label "Prénom" :value "Jean-Claude"}]
                    [:pica-checkbox {:checked true :label "Check this"}]
                    [:pica-switch {:checked true :label "Switch this"}])
                  {:name "OK" :fn #(pdia/show-alert (str "Got values: " %2))} {:name "Cancel"}))

(defn ^:export open-dialog-input
  []
  (pdia/show-form "Some other stuff"
                  (list
                    [:pica-input {:label "Nom" :validator (fn [evt f] (.setTimeout js/window #(f (if-let [s (.. evt -target -value)] (if (< 6 (count s)) "No more than 6 characters"))) 1000))
                                  :submitter #(psna/show "Change saved!") :input-attributes {:pattern ".{3,}" :required true}}]
                    [:pica-input {:label "Prénom (no validator)" :value "Jean-Claude" :submitter #(psna/show "Change saved!")}])
                  {:name "OK" :fn #(psna/show "Saved" {:name "Undo" :fn (fn [] (psna/show "Undid"))})}))

(defn ^:export open-menu
  [evt]
  (pmen/show-at-event evt "button" [{:name "Click" :fn #(psna/show "Yep")}
                                    {:name "Settings" :fn #(psna/show "Settings") :icon :action/settings}]))

(defn init
  []
  (.appendChild (.querySelector js/document "#tables")
                (h/create [:pica-table {:header true :data [{:a "1"}]}]))
  (.appendChild js/document.body (h/create (ptb/create-app-bar "Titre" (list [:h2 "Your stuff"]
                                                                             (pdra/items
                                                                               [:pica-item {:disabled true :action {:name "Action disabled"}}]
                                                                               [:pica-item {:action {:name "Settings" :icon :action/settings :fn (fn [] (psna/show "You clicked on settings"))}}]))))))

(.addEventListener js/document "DOMContentLoaded" init)

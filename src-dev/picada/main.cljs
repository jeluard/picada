(ns picada.main
  (:require [picada.bootstrap :as boot]
            [picada.component.control :as pcon]
            [picada.component.dialog :as pdia]
            [picada.component.menu :as pmen]
            [picada.component.snackbar :as psna]
            [picada.component.toolbar :as ptb]
            [hipo.core :as h]
            [devtools.core :as devtools]
            cljsjs.document-register-element)
  (:require-macros [picada.doc :refer [doc]]))

(devtools/install!)

(boot/register-all)
(.log js/console (doc (meta pcon/pica-input)))

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
                  {:name "OK" :fn #(psna/show "Saved" {:name "Undo" :fn (fn [] (psna/show "Undid"))})} {:name "Cancel"}))

(defn ^:export open-dialog-input
  []
  (pdia/show-form "Some other stuff"
                  (list
                    [:pica-input {:label "Nom" :validator (fn [evt f] (.setTimeout js/window #(f (if-let [s (.. evt -target -value)] (if (< 6 (count s)) "No more than 6 characters"))) 1000))
                                  :submitter #(psna/show "Change saved!") :input-attributes {:pattern ".{3,}" :required true}}]
                    [:pica-input {:label "Prénom (no validator)" :value "Jean-Claude" :submitter #(psna/show "Change saved!")}])
                  {:name "OK" :fn #(psna/show "Saved" {:name "Undo" :fn (fn [] (psna/show "Undid"))})}))

(defn ^:export show-menu
  [evt]
  (.log js/console evt)
  (let [bel (.closest (.-target evt) "pica-icon-button")
        r (.getBoundingClientRect bel)
        pos {:x (- (.-right r) 112) :y (.-top r)}]
    (pmen/show pos [{:name "Click" :fn #(psna/show "Yep")}])))

(.addEventListener js/document "DOMContentLoaded"
                   #(.appendChild js/document.body (first (h/create (ptb/create-app-bar "Titre" (list [:h2 "Your stuff"] [:pica-item {:action {:name "Settings" :icon "settings"}}]))))))
(ns picada.bootstrap
  (:require [picada.component.button :as but]
            [picada.component.control :as con]
            [picada.component.dialog :as dia]
            [picada.component.drawer :as dra]
            [picada.component.icon :as ico]
            [picada.component.item :as ite]
            [picada.component.menu :as men]
            [picada.component.overlay :as ovl]
            [picada.component.snackbar :as sna]
            [picada.component.spinner :as spi]
            [picada.component.table :as tab]
            [picada.component.toolbar :as tlb]
            [picada.style :as st]
            #?(:cljs [lucuma.core :as luc])
            #_[garden.compiler :as comp]
            #_[garden.util :as util]
            #_[garden.types :as t])
  #_(:clj (:import garden.types.CSSAtRule)))

#?(:cljs
(def all-components [but/pica-button but/pica-fab but/pica-icon-button con/pica-checkbox con/pica-input con/pica-switch
                     dia/pica-dialog dra/pica-drawer ico/pica-icon ite/pica-item men/pica-menu ovl/pica-overlay
                     sna/pica-snackbar spi/pica-spinner tab/pica-table tlb/pica-toolbar]))

(def all-styles [but/styles con/styles dia/styles dra/styles ico/styles ite/styles men/styles ovl/styles sna/styles spi/styles tab/styles tlb/styles])

#_
(defn- at-rule [identifier value]
  (t/CSSAtRule. identifier value))

#_
(defn custom-media
  [name & a]
  (at-rule :custom-media {:name name}))

#_
(defmethod #'comp/render-at-rule custom-media
  [{:keys [value]}]
  (let [{:keys [url media-queries]} value
        url (if (string? url)
              (util/wrap-quotes url)
              #_(render-css url))
        queries (when media-queries
                  #_(render-media-expr media-queries))]
    (str "@custom-media "
         (if queries (str url " " queries) url)
         #'comp/semicolon)))

(defn all
  [kp ka]
  [#_(custom-media :desktop [])
   #_["@custom-media --desktop (min-width: 600px);" {}]
   ["@custom-selector :--pica-button pica-button, pica-fab, pica-icon-button;" {}]
   [":root" (st/create-theme kp ka)]
   [:body
    {:font-family "RobotoDraft, Arial"}]
   all-styles])

#?(:cljs
(defn register-all
  []
  (luc/register all-components)))

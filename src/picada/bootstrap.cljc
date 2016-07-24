(ns picada.bootstrap
  (:require [picada.component.button :as but]
            [picada.component.control :as con]
            [picada.component.dialog :as dia]
            [picada.component.drawer :as dra]
            [picada.component.grid-list :as gri]
            [picada.component.icon :as ico]
            [picada.component.item :as ite]
            [picada.component.menu :as men]
            [picada.component.overlay :as ovl]
            [picada.component.snackbar :as sna]
            [picada.component.spinner :as spi]
            [picada.component.tab :as tab]
            [picada.component.table :as tabl]
            [picada.component.toolbar :as tlb]
            [picada.style :as st]
            #?(:cljs [lucuma.core :as luc])))

#?(:cljs
(def all-components [but/pica-button but/pica-button-bar but/pica-fab but/pica-icon-button con/pica-checkbox con/pica-input con/pica-switch
                     dia/pica-dialog dra/pica-drawer gri/pica-grid-list gri/pica-tile ico/pica-icon ite/pica-item men/pica-menu ovl/pica-overlay
                     sna/pica-snackbar spi/pica-spinner tab/pica-tab tab/pica-tab-bar tabl/pica-table tlb/pica-toolbar]))

(def all-styles [but/styles con/styles dia/styles dra/styles gri/styles ico/styles ite/styles men/styles ovl/styles sna/styles spi/styles tab/styles tabl/styles tlb/styles])

(defn all
  [kp ka]
  [["@custom-selector :--pica-button pica-button, pica-fab, pica-icon-button;" {}]
   [":root" (st/create-theme kp ka)]
   [:body
    {:font-family "RobotoDraft, Arial"}]
   all-styles])

#?(:cljs
(defn register-all
  []
  (luc/register all-components)))

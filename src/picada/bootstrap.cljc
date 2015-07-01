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
            [picada.component.toolbar :as tlb]
            [picada.style :as st]
            [lucuma.core :as luc]))

(def all-styles [but/styles con/styles dia/styles dra/styles ico/styles ite/styles men/styles ovl/styles sna/styles spi/styles tlb/styles])

(defn all
  [kp ka]
  [["@custom-media --desktop (min-width: 600px);" {}]
   ["@custom-selector --pica-button pica-button, pica-fab, pica-icon-button;" {}]
   [":root" (st/create-theme kp ka)]
   [:body
    {:font-family "RobotoDraft, Arial"}]
   all-styles])

#?(:cljs
(defn register-all
  []
  (luc/register but/pica-button but/pica-fab but/pica-icon-button con/pica-checkbox con/pica-input con/pica-switch dia/pica-dialog dra/pica-drawer ico/pica-icon
                ite/pica-item men/pica-menu ovl/pica-overlay sna/pica-snackbar spi/pica-spinner tlb/pica-toolbar)))
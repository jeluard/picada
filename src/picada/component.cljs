(ns picada.component
  (:require [hipo.core :as h]
            [hipo.hiccup :as hic]
            [hipo.interpreter :as hi]
            [lucuma.core :as l]))

; https://github.com/webcomponents/gold-standard/wiki

(defn- raf
  [f]
  (.requestAnimationFrame js/window f))

(defn perform-reconciliation
  [el m f a]
  (letfn [(r [] (f @a #_{:target el}))]
    (if-let [af (:on-reconciliation m)]
      (let [o (af el @a)]
        (cond
          (fn? o) (o r)
          (false? o) nil
          :else (r)))
      (r))))

(defn trigger-raf-when-needed
  [el m f a raf-id]
  (if-not @raf-id
    (let [id (raf #(do (perform-reconciliation el m f a) (reset! raf-id nil)))]
      (reset! raf-id id))))

(defn enqueue-changes
  [el m mp f a raf-id]
  (let [ps (merge @a mp)]
    (trigger-raf-when-needed el m f a raf-id)
    (reset! a ps)))

(def ^:private hipo-attrs (atom nil))
(def ^:private registered (atom nil))

(defn registered?
  [s]
  (some #(= % s) @registered))

(defn create-element
  [ns tag attrs m]
  (if                                                       false ;(registered? tag)
    (do
      (reset! hipo-attrs attrs)
      (hi/default-create-element ns tag nil m))
    (hi/default-create-element ns tag attrs m)))


(defn get-and-reset-attrs
  [el]
  (if (registered? (name (l/element-name el)))
    (when-let [m @hipo-attrs]
      (l/set-properties! el m false false)
      (reset! hipo-attrs nil)
      m)))

; [:todo-app {:items items}]
; hipo : createElement("todo-app")
;  lucuma :on-created
;  set properties from merge default and optional attributes
;  document-f with items nil
;  default :document
; hipo :set-prop "items"
; lucuma :on-changed triggered
; on next RAF, hipo reconciliate calls :document-fn with items

; [:todo-app {:items items}]
; hipo/create with custom create-element
;   sets attrs from default attrs
;   calls createElement("todo-app")
;     lucuma generic :on-created
;     set properties from merge default, optional attributes and result of :default-properties-fn
;     lucuma custom :on-created with merged properties
;     calls hipo :create-element-fn on (:document-fn items) with proper items


; lucuma on-created
;  gets properties
;  sets as lucuma properties
;  calls on-created


; Attributes are stored globally are they are set in one level and consume in another

(def material-ref :picada/material-ref)

; TODO Higher level macro defcomponent hiccup specific that could handle pure server side (i.e. no Custom Elements) through hiccup
; TODO support :host
; TODO support multiple elements in host
; TODO embed style in component
; TODO meta on-changed (or directly :document?) that aggregates all changes (pluggabble): property, children (via MutationObserver), events, media queries
; (defcomponent pica-dialog
;   :document
;   (fn [{:keys [properties events children]}]
;     [:div]))
; => hipo must support partial reconciliation?

#_
(defn create-content
  [el o {:keys [document] :as m} hm]
  (if (fn? document)
    (let [[del f] (h/create #(document el (merge % (get-and-reset-attrs el))) o (merge {:create-element-fn create-element} hm))
          a (atom (l/get-properties (l/host el)))
          raf-id (atom nil)]
      [del
       {:on-property-changed
        #(let [mp (l/changes->map %2)]
          (enqueue-changes el m mp f a raf-id))}])
    [(h/create document)]))

#_
(defn reconciliate
  [{:keys [document] :as m} & [hm]]
  (assert (not (nil? document)))
  (assert (or (nil? hm) (map? hm)))
  (swap! registered conj (:name m))
  (merge-with l/default-mixin-combiner m
              {:on-created
               (fn [el o]
                 ; TODO handle list of elements.
                 (let [[del m] (create-content el o m hm)]
                   (.appendChild el del)
                   m))}))

(defn create
  [hf o hm]
  (h/create (hf o)
            (merge {:create-element-fn create-element :force-interpretation? false #_ :interceptors #_ [(hipo.interceptor/LogInterceptor. true)]} hm))
  )

(defn reconciliate
  [{:keys [document] :as m} & [hm]]
  (assert (not (nil? document)))
  (assert (or (nil? hm) (map? hm)))
  (swap! registered conj (:name m))
  (merge-with l/default-mixin-combiner m
              {:on-created (fn [el o]
                             ; TODO handle list of elements.
                             (let [hf #(document el (merge % (get-and-reset-attrs el)))
                                   [del f] (create hf o hm)]
                               (aset el "pica_update" #(f (hf %)))
                               (.appendChild el del)))
               :on-property-changed (fn [el s]
                                      (if-let [f (aget el "pica_update")]
                                        (let [mp (l/changes->map s)
                                              a (atom (l/get-properties (l/host el)))
                                              raf-id (atom nil)]
                                          (enqueue-changes el m mp f a raf-id)
                                          nil)))}))

#_
(defn create2
  [h hm]
  (h/create h
                   (merge {:create-element-fn create-element :force-interpretation? true #_:interceptor #_ (hipo.interceptor/LogInterceptor. true)} hm))
  )
#_
(defn reconciliate2
  [{:keys [document] :as m} & [hm]]
  (assert (not (nil? document)))
  (assert (or (nil? hm) (map? hm)))
  (swap! registered conj (:name m))
  (merge-with l/default-mixin-combiner m
              {:on-created (fn [el o]
                             ; TODO handle list of elements.
                             (let [h (document el o #_(merge % (get-and-reset-attrs el)))
                                   cs (let [v (hic/children h)] (if (= 1 (count v)) (nth v 1) (seq v)))
                                   del (create2 cs hm)
                                   m (hic/attributes h)
                                   m (dissoc m :action)]
                               ; Set initial attributes for host
                               (hi/reconciliate-attributes! el nil m nil)
                               (aset el "pica_host_previous" m)
                               ;(aset el "pica_update" f)

                               (.appendChild el del)))
               :on-property-changed (fn [el s]
                                       (.log js/console (clj->js (merge (aget el "pica_host_previous")  (l/changes->map s))))
                                       (hi/reconciliate-attributes! el (aget el "pica_host_previous")
                                                                    (dissoc (merge (aget el "pica_host_previous")  (l/changes->map s))
                                                                            :action)
                                                                     nil)
                                      (if-let [f (aget el "pica_update")]
                                        (let [mp (l/changes->map s)
                                              a (atom (l/get-properties (l/host el)))
                                              raf-id (atom nil)]
                                          (enqueue-changes el m mp f a raf-id)
                                          nil)))}))

(defn wrap-listener [f wf] (fn [evt] (f evt wf)))
(defn wrap-action [m f] (merge {:fn (wrap-listener f (:fn m))} (if-let [s (:name m)] {:name s}) (if-let [s (:icon m)] {:icon s})))

; Use as #(comp/reconciliate % {:interceptor (hipo.interceptor/LogInterceptor. false)}) for some debug



(comment
  ; In partial mode only children with ^:updated will be reconciliated
  (let [el f] (hipo/create [:div] {})
              (f [:div ^:updated [:li]
                  ] {:partial-update? true}))
  )
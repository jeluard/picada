(ns picada.component
  #?(:cljs
     (:require [picada.animation :as anim]
               [hipo.core :as h]
               [hipo.interpreter :as hi]
               [lucuma.core :as l])))

#?(:cljs (defn wrap-listener [f wf] (fn [evt] (f evt wf))))
#?(:cljs (defn wrap-action [m f] (merge {:fn (wrap-listener f (:fn m))} (if-let [s (:name m)] {:name s}) (if-let [s (:icon m)] {:icon s}))))

(def ^:private changes (atom []))
(def ^:private raf-scheduled (atom false))
(def ^:private default-hiccup [:host])
(def ^:private hiccup-property "host_hiccup")

(defn replace-host
  [h t]
  (assoc h 0 t))

#?(:cljs
(defn perform-reconciliation
  [hm]
  (reset! raf-scheduled false)
  (doseq [{:keys [el m]} @changes]
    (let [h ((:document m) el (l/get-properties el))]
      (h/reconciliate! el (aget el hiccup-property) h hm)
      (aset el hiccup-property h))
    (swap! changes subvec 1))))

#?(:cljs
(defn schedule-reconciliation-if-needed!
  "Schedule reconciliation using rAF if no call is scheduled yet"
  [hm]
  (if (compare-and-set! raf-scheduled false true)
    (.requestAnimationFrame js/window #(perform-reconciliation hm)))))

#?(:cljs
(defn enqueue-changes
  [el m hm]
  (swap! changes conj {:el el :m m})
  (schedule-reconciliation-if-needed! hm)))

#?(:cljs
(defn reconciliate
  [{:keys [document] :as m} hm] ; {:interceptors [(hipo.interceptor/LogInterceptor. false)]}
   (assert (not (nil? document)) (str "No :document provided for " (:name m)))
   (merge-with l/default-mixin-combiner m
               {:on-created (fn [el o]
                              (let [h (document el o)]
                                (assert (= :host (first h)) ":document fn does not generate a hiccup vector with :host as root node")
                                ; Do not enqueue but synchronously reconciliate so that the element content is accurate at the end of :on-created
                                (h/reconciliate! el default-hiccup h hm)
                                (aset el hiccup-property h)))
                :on-property-changed (fn [el s] (enqueue-changes el m hm))})))

; TODO idea
; document gets element and a map that can be augmented
; properties (filtered), last event of an handler, result from DS query , .. (user can provide new)
; :document-map {:properties {:filtered #{:active}}  :events }

; :on-created performs a reconciliation of the host element created by CustomElements with the default host content
; :on-property-changed queues changes that will be reconciliated in a rAF.
; Reconciliation explicitely receives the previous hiccup value (stored in a picada specific property) so that it does not clashes with the hipo one.
; This allows to have to separate reconciliation on the same element, none stepping on each other foot.

; e.g.
; (def el (h/create [:pica-snackbar {:id "id" :action {:name "Some Action"}}]))
; => 1. creates an empty pica-snackbar
;    2. this triggers :on-created calls that performs a reconciliation of picada-snackbar :document content with default property values
;    3. id and action property are then set; that triggers an :on-property-changed call and a :host content reconciliation
;    4. :host children are created as needed
; (h/reconciliate! el [:pica-snackbar {:id "id" :action {:name "Some other Action"}}])
; => 1. reconciliation is performed, updating the value of action property
;    2. :on-property-changed is called triggering a second reconciliation of the same element (but with different args) that update the button child

#?(:cljs
(defn show
  ([el] (show el nil))
  ([el f]
   (anim/animate-property el :animation-entry #(do (if f (f)) (set! (.-visible el) true))))))

#?(:cljs
(defn hide
  ([el] (hide el nil))
  ([el f]
   (set! (.-visible el) false)
   (anim/animate-property el :animation-exit #(if f (f))))))

#?(:cljs
(defn hideable
  [show?]
  {:on-attached
   (fn [el]
     (if (:show-on-attached (l/get-properties el))
       (show el)))
   :properties {:visible false :show-on-attached show?}
   :methods {:show show :hide hide}}))

#?(:cljs
(defn component
  [m]
  (assoc m :mixins
           [anim/animation
            (if (:hideable? m)
              (hideable (:show-on-attached? m)))
            (if (contains? m :document)
              reconciliate)])))

#?(:clj
(defmacro defcomponent
  [n & kvs]
  (let [m (apply hash-map kvs)]
    (lucuma.core/define-specification n `(update-in ~m [:mixins] conj picada.component/component)))))

(comment
;TODO perf
; Investigate how :create-element-fn can help creating complete element in one pass, bypassing set-attribute stepping
(def ^:private hipo-attrs (atom nil))
;in reconciliate
(def ^:private registered (atom nil))
(swap! registered conj (:name m))
(defn registered?
  [s]
  (some #(= % s) @registered))

(defn create-element
  [ns tag attrs m]
  (if false (registered? tag)
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
      m))))

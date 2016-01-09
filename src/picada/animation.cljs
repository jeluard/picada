(ns picada.animation
  (:require [lucuma.core :as l]))

(def ^:private animations (atom {}))

(defn register-animation
  [& [k a]]
  (swap! animations assoc k a))

(defn register-animations
  [m]
  (doseq [[k v] m]
    (register-animation k v)))

(register-animations
  {:delayed-entry {:frames [{:transform "scale(0)"} {:transform "scale(1)"}] :options {:duration 1000 :easing "cubic-bezier(0.4, 0, 0.2, 1)"}}
   :left-entry {:frames [{:transform "translateX(-100%)"} {:transform "none"}] :options {:duration 300 :easing "cubic-bezier(.37, .15, .32, .94)"}}
   :left-exit {:frames [{:transform "none"} {:transform "translateX(-100%)"}] :options {:duration 300 :easing "cubic-bezier(.37, .15, .32, .94)"}}
   :entry {:frames [{:transform "translateY(0)" :opacity 1} {:transform "translateY(50px)" :opacity 0}] :options {:duration 500}}
   :exit {:frames [{:transform "translateY(-50px)" :opacity 0} {:transform "translateY(0)" :opacity 1}] :options {:duration 500 :delay 500}}
   :slide-in-up {:frames [{:opacity 0 :transform "translateY(100%)"} {:opacity 1 :transform "none"}] :options {:duration 200}}
   :slide-in-down {:frames [{:opacity 0 :transform "translateY(-100%)"} {:opacity 1 :transform "none"}] :options {:duration 200}}
   :slide-out-up {:frames [{:opacity 1 :transform "none"} {:opacity 0 :transform "translateY(100%)"}] :options {:duration 200}}
   :slide-out-down {:frames [{:opacity 1 :transform "none"} {:opacity 0 :transform "translateY(-100%)"}] :options {:duration 200}}
   :zoom-in {:frames [{:transform "scale(0.5)"} {:transform "scale(1)"}] :options {:duration 100}}
   :zoom-out {:frames [{:transform "scale(1)"} {:transform "scale(0.5)"}] :options {:duration 100 :easing "ease-in-out"}}
   ; TODO add support for mobile (bottom 0px)
   :snackbar-entry {:frames [{:bottom "-100px"} {:bottom "12px"}] :options {:duration 300 :easing "ease-in-out"}}
   :snackbar-exit {:frames [{:bottom "12px"} {:bottom "-100px"}] :options {:duration 300 :easing "ease-in-out"}}
   :overlay-entry {:frames [{:opacity 0} {:opacity 0.6}] :options {:duration 200 :easing "ease-in-out"}}
   :overlay-exit {:frames [{:opacity 0.6} {:opacity 0}] :options {:duration 200 :easing "ease-in-out"}}})

(def default-options {:duration 1000})

(defn ^:export all [] (keys @animations))

(defn- get-animation
  [k]
  (if k
    (or (k @animations) (.warn js/console (str "Unknown animation <" k ">")))))

(defn animate
  [el k]
  (if-let [o (get-animation k)]
    (let [frames (or (:frames o) o)
          options (:options o)]
      (.animate el (clj->js frames) (clj->js (or options default-options))))))

(defn animate-property
  [el k f]
  (if-let [p (k (l/get-properties el))]
    (.requestAnimationFrame
      js/window
      #(.addEventListener (animate el p) "finish" f))
    (if f
      (f)))
  el)

(defn dismiss
  ([el] (dismiss el nil))
  ([el f]
   (animate-property el :animation-exit #(do (if f (f el)) (.remove el)))))

(def animation
  {:on-attached
   (fn [el]
     (if (:animation-entry (l/get-properties el))
       (animate-property el :animation-entry nil)))
   :properties {:animation-entry {:type :keyword :default nil} :animation-exit {:type :keyword :default nil}}
   :methods {:dismiss dismiss}})

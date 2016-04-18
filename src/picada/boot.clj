(ns picada.boot
  {:boot/export-tasks true}
  (:require [clojure.java.io :as jio]
            [boot.core :as boot :refer [deftask]]
            [boot.util :as butil]))

(defn- find-css-files
  [fs files]
  (->> fs
       boot/input-files
       (boot/by-re files)
       (map (juxt boot/tmp-path boot/tmp-file identity))))

(deftask cssnext
  "Transpile css files using cssnext"
  [f files    FILES       [regex] "A vector of filenames to process with autoprefixer."
   b browsers BROWSERS    str   "A string describing browsers autoprefixer will target."]
  (let [tmp-dir (boot/tmp-dir!)]
    (boot/with-pre-wrap fileset
      (doseq [[in-path in-file file] (find-css-files fileset files)]
        (boot.util/info "Transpiling %s\n" (:path file))
        (let [out-file (doto (jio/file tmp-dir in-path) jio/make-parents)]
          (butil/dosh "cssnext" (.getPath in-file) (.getPath out-file) (if browsers (str "-b" browsers)))))
      (-> fileset
          (boot/add-resource tmp-dir)
          boot/commit!))))

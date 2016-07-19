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

(defn concat [s f1 out]
  (with-open [o (jio/output-stream out)]
    (jio/copy s o)
    (jio/copy f1 o)))

(deftask cssnext
  "Transpile css files using cssnext"
  [f files    FILES       [regex] "A vector of filenames to process with autoprefixer."
   b browsers BROWSERS    str   "A string describing browsers autoprefixer will target."]
  (let [tmp-dir (boot/tmp-dir!)]
    (boot/with-pre-wrap fileset
      (doseq [[in-path in-file file] (find-css-files fileset files)]
        (boot.util/info "Transpiling %s\n" (:path file))
        (let [in-file2 (doto (jio/file tmp-dir in-path) jio/make-parents)
              out-file (doto (jio/file tmp-dir in-path) jio/make-parents)]
          (concat "@custom-media --desktop (min-width: 600px);\n" in-file in-file2)
          (butil/dosh "cssnext" (.getPath in-file2) (.getPath out-file) (if browsers (str "-b" browsers)))))


      (-> fileset
          (boot/add-resource tmp-dir)
          boot/commit!))))

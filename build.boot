(set-env!
 :source-paths  #{"src"}
 :resources-paths  #{"resources-dev"}
 :dependencies '[[org.clojure/clojure           "1.7.0"]
                 [adzerk/boot-cljs              "0.0-3308-0" :scope "test"]
                 [org.martinklepsch/boot-garden "1.2.5-5"    :scope "test"]
                 [adzerk/boot-reload            "0.2.6"      :scope "test"]
                 [jeluard/boot-notify           "0.2.0"      :scope "test"]
                 [adzerk/bootlaces              "0.1.11"     :scope "test"]

                 [org.clojure/clojurescript "1.7.28" :classifier "aot"]
                 [lucuma "0.5.0-SNAPSHOT"]
                 [hipo "0.5.0-SNAPSHOT"]
                 [garden "1.2.5"]])

(def dev-dependencies '[[cljsjs/document-register-element "0.4.3-0"]
                        [cljsjs/dom4 "1.4.5-0"]
                        [cljsjs/web-animations-js "2.1.2-0"]])

(require
 '[adzerk.boot-cljs              :refer [cljs]]
 '[org.martinklepsch.boot-garden :refer [garden]]
 '[adzerk.boot-reload            :refer [reload]]
 '[jeluard.boot-notify           :refer [notify]]
 '[adzerk.bootlaces              :refer [bootlaces! build-jar]])

(def +version+ "0.5.0-SNAPSHOT")

(bootlaces! +version+)
(task-options!
  pom  {:project     'picada
        :version     +version+
        :description "A Material inspired collection of HTML elements (Custom Elements)"
        :url         "https://github.com/jeluard/picada"
        :scm         {:url "https://github.com/jeluard/picada"}
        :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

(defn- find-css-files [fs files]
  (->> fs
       boot.core/input-files
       (boot.core/by-re files)
       (map (juxt boot.core/tmp-path boot.core/tmp-file identity))))

(deftask cssnext
  [f files    FILES       [regex] "A vector of filenames to process with autoprefixer."
   b browsers BROWSERS    str   "A string describing browsers autoprefixer will target."]
  (let [tmp-dir (boot.core/temp-dir!)]
    (boot.core/with-pre-wrap fileset
      (doseq [[in-path in-file file] (find-css-files fileset files)]
        (boot.util/info "Transpiling %s\n" (:path file))
        (let [out-file (doto (clojure.java.io/file tmp-dir in-path) clojure.java.io/make-parents)]
          (boot.util/dosh "cssnext" (.getPath in-file) (.getPath out-file) (if browsers (str "-b" browsers)))))
      (-> fileset
          (boot.core/add-resource tmp-dir)
          boot.core/commit!))))

(deftask css
  []
  (comp
    (garden :styles-var 'picada.styles/all :output-to "picada.css")
    (cssnext :files [#"picada.css"])))

(deftask dev
  []
  (merge-env! :dependencies dev-dependencies)
  (merge-env! :source-paths #{"src-dev"})
  (merge-env! :resource-paths #{"resources-dev"})
  (comp
    (watch :verbose true)
    (notify)
    (reload)
    (css)
    (cljs :compiler-options {:asset-path "out" :output-to "picada.js"})
    (build-jar)))

(deftask release
  []
  (merge-env! :dependencies dev-dependencies)
  (merge-env! :source-paths #{"src-dev"})
  (comp
    (css)
    (cljs :optimizations :advanced :compiler-options {:output-to "picada.js"})))

(set-env!
 :source-paths  #{"src"}
 :resources-paths  #{"resources-dev"}
 :dependencies '[[org.clojure/clojure           "1.8.0"]
                 [adzerk/boot-cljs              "1.7.228-1"      :scope "test"]
                 [pandeiro/boot-http            "0.7.3"          :scope "test"]
                 [org.martinklepsch/boot-garden "1.3.0-0"        :scope "test"]
                 [adzerk/boot-reload            "0.4.7"          :scope "test"]
                 [jeluard/boot-notify           "0.2.1"          :scope "test"]
                 [adzerk/bootlaces              "0.1.13"         :scope "test"]

                 [org.clojure/clojurescript     "1.8.40"]
                 [lucuma                        "0.5.1"]
                 [hipo                          "0.5.2"]
                 [garden                        "1.3.2"]])

(def dev-dependencies '[[cljsjs/document-register-element "0.5.3-1"]
                        [cljsjs/dom4 "1.5.2-1"]
                        [cljsjs/web-animations "2.1.4-0"]])

(require
 '[picada.boot                   :refer [cssnext]]
 '[adzerk.boot-cljs              :refer [cljs]]
 '[pandeiro.boot-http            :refer [serve]]
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

#_
(defn- find-css-files [fs files]
  (->> fs
       boot.core/input-files
       (boot.core/by-re files)
       (map (juxt boot.core/tmp-path boot.core/tmp-file identity))))

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
    (serve)
    (watch :verbose true)
    (notify)
    (reload)
    (css)
    (cljs :compiler-options {:parallel-build true})
    (build-jar)))

(deftask release
  []
  (merge-env! :dependencies dev-dependencies)
  (merge-env! :source-paths #{"src-dev"})
  (comp
    (css)
    (cljs :optimizations :advanced)))
